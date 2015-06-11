package heracles.jdbc.parser;

import heracles.jdbc.common.cache.ParserResultCache;
import heracles.jdbc.common.param.ParameterContext;
import heracles.jdbc.matrix.model.RuleListModel;
import heracles.jdbc.parser.common.Constants;
import heracles.jdbc.parser.common.ShardingType;
import heracles.jdbc.parser.common.Utils;
import heracles.jdbc.parser.exception.SQLParserException;
import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.variable.Column;
import heracles.jdbc.parser.statement.Statement;
import heracles.jdbc.parser.statement.insert.Insert;
import heracles.jdbc.parser.statement.select.Select;
import heracles.jdbc.parser.statement.select.from.Table;
import heracles.jdbc.rule.RuleEngine;
import heracles.jdbc.rule.RuleEngineFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class Parser {

	private static Log LOGGER = LogFactory.getLog(Parser.class);

	private ParserResult parserResult;
	private Map<String, RuleEngine> tb2ruleEngine = new HashMap<String, RuleEngine>();
	private Map<String, Object[]> tb2KeyValues = new HashMap<String, Object[]>();

	private RuleEngineFactory ruleEngineFactory;

	public Parser(String sql, RuleListModel ruleList) {
		parse(sql);
		this.ruleEngineFactory = new RuleEngineFactory(ruleList);
	}

	private void parse(String sql) {
		Assert.hasText(sql);

		String trimSQL = Utils.trimComments(sql);
		parserResult = ParserResultCache.getParserResult(trimSQL);
		if (parserResult == null) {
			try {
				SQLLexer sqlLexer = new SQLLexer(new StringReader(trimSQL));
				SQLParser sqlParser = new SQLParser(sqlLexer);
				sqlParser.statement();
				SQLTreeParser sqlTreeParser = new SQLTreeParser();
				sqlTreeParser.statement(sqlParser.getAST());
				Statement statement = sqlTreeParser.getStatement();
				parserResult = new ParserResult(statement);
				parserResult.setParamIndexMap(sqlTreeParser.getParamIndexMap());
				parserResult.setParamValueMap(sqlTreeParser.getParamValueMap());
				parserResult.setColumnEqualMap(sqlTreeParser.getColumnEqualMap());
				ParserResultCache.setParserResult(trimSQL, parserResult);
			} catch (Throwable e) {
				throw new SQLParserException(e);
			}
		}
	}

	public boolean eval(Map<Integer, ParameterContext> parameterSettings, ShardingType shardingType) {
		Assert.notNull(parserResult);

		Set<String> tbNames = parserResult.getTbName2TableMap().keySet();
		if (!ruleEngineFactory.isSharding(tbNames)) {
			// FIXME Anders 需要支持不分库分表的SQL能在默认库执行
			return false;
		}

		if (shardingType.equals(ShardingType.DB)) {
			tb2ruleEngine = ruleEngineFactory.createDbRuleEngines(tbNames);
		} else {
			tb2ruleEngine = ruleEngineFactory.createTbRuleEngines(tbNames);
		}

		for (String tbName : tbNames) {
			Object[] keyValues = null;
			switch (parserResult.getType()) {
			case INSERT:
				keyValues = evalInsert(parameterSettings, tbName);
				break;
			case DELETE:
				keyValues = evalDelete(parameterSettings, tbName);
				break;
			case UPDATE:
				keyValues = evalUpdate(parameterSettings, tbName);
				break;
			case SELECT:
				keyValues = evalSelect(parameterSettings, tbName);
				break;
			default:
				throw new UnsupportedOperationException("only support delete, update, insert and select sql statement");
			}

			if (keyValues != null) {
				tb2KeyValues.put(tbName, keyValues);
			}
		}

		return true;
	}

	private Object[] evalSelect(Map<Integer, ParameterContext> parameterSettings, String tbName) {
		Map<String, List<Integer>> paramIndexMap = parserResult.getParamIndexMap();
		Map<String, List<String>> paramValueMap = parserResult.getParamValueMap();
		Map<Column, Column> columnEqualMap = parserResult.getColumnEqualMap();
		List<Integer> paramIndexes = null;
		List<String> paramValues = null;
		RuleEngine ruleEngine = tb2ruleEngine.get(tbName);

		if (paramIndexMap != null) {
			paramIndexes = findParamIndexes(tbName, paramIndexMap, ruleEngine.getShardingKey());
			if (paramIndexes == null) {
				for (Column column : columnEqualMap.keySet()) {
					if (column.getName().equals(ruleEngine.getShardingKey())
							&& column.getTable().getName().equals(tbName)) {
						Column columnEqual = columnEqualMap.get(column);
						String columnEqualTbName = columnEqual.getTable().getName();
						RuleEngine columnEqualRuleEngine = tb2ruleEngine.get(columnEqualTbName);
						paramIndexes = findParamIndexes(columnEqualTbName, paramIndexMap,
								columnEqualRuleEngine.getShardingKey());
						if (paramIndexes != null) {
							break;
						}
					}
				}
			}
			// List<SelectItem> sis = select.getSelectItems();
			// if (paramIndexes == null) {
			// for (SelectItem si : sis) {
			// if (si instanceof SelectExpression) {
			// SelectExpression se = (SelectExpression) si;
			// String alias = se.getAlias().getName();
			// Column column = (Column) se.getExpression();
			// if (ruleEngine.getShardingKey().equals(alias)) {
			// paramIndexes = paramIndexMap.get(column.getName());
			// break;
			// }
			// }
			// }
			// }
		}
		if (paramValueMap != null) {
			paramValues = findParamValues(tbName, paramValueMap, ruleEngine.getShardingKey());
			if (paramValues == null) {
				for (Column column : columnEqualMap.keySet()) {
					if (column.getName().equals(ruleEngine.getShardingKey())
							&& column.getTable().getName().equals(tbName)) {
						Column columnEqual = columnEqualMap.get(column);
						String columnEqualTbName = columnEqual.getTable().getName();
						RuleEngine columnEqualRuleEngine = tb2ruleEngine.get(columnEqualTbName);
						paramValues = findParamValues(columnEqualTbName, paramValueMap,
								columnEqualRuleEngine.getShardingKey());
						if (paramValues != null) {
							break;
						}
					}
				}
			}
		}

		Object[] keyValues;
		if (CollectionUtils.isEmpty(paramIndexes) && CollectionUtils.isEmpty(paramValues)) {
			throw new SQLParserException("can not find sharding key");
		} else if (CollectionUtils.isNotEmpty(paramIndexes)) {
			keyValues = new Object[paramIndexes.size()];
			int j = 0;
			for (Integer i : paramIndexes) {
				keyValues[j++] = parameterSettings.get(i).getArgs()[1];
			}
		} else {
			keyValues = new Object[paramValues.size()];
			int j = 0;
			for (String s : paramValues) {
				keyValues[j++] = s;
			}
		}
		return keyValues;
	}

	private List<String> findParamValues(String tbName, Map<String, List<String>> paramValueMap, String shardingKey) {
		Table table = parserResult.getTable(tbName);
		List<String> paramValues = paramValueMap.get(shardingKey);
		if (paramValues == null) {
			paramValues = paramValueMap.get(tbName + Constants.DOT + shardingKey);
		}
		if (paramValues == null && table.getAlias() != null) {
			paramValues = paramValueMap.get(table.getAlias().getName() + Constants.DOT + shardingKey);
		}
		return paramValues;
	}

	private List<Integer> findParamIndexes(String tbName, Map<String, List<Integer>> paramIndexMap, String shardingKey) {
		Table table = parserResult.getTable(tbName);
		List<Integer> paramIndexes = paramIndexMap.get(shardingKey);
		if (paramIndexes == null) {
			paramIndexes = paramIndexMap.get(tbName + Constants.DOT + shardingKey);
		}
		if (paramIndexes == null && table.getAlias() != null) {
			paramIndexes = paramIndexMap.get(table.getAlias().getName() + Constants.DOT + shardingKey);
		}
		return paramIndexes;
	}

	private Object[] evalDelete(Map<Integer, ParameterContext> parameterSettings, String tbName) {
		return evalUpdate(parameterSettings, tbName);
	}

	private Object[] evalUpdate(Map<Integer, ParameterContext> parameterSettings, String tbName) {
		Map<String, List<Integer>> paramIndexMap = parserResult.getParamIndexMap();
		Map<String, List<String>> paramValueMap = parserResult.getParamValueMap();
		List<Integer> paramIndexes = null;
		List<String> paramValues = null;
		RuleEngine ruleEngine = tb2ruleEngine.get(tbName);

		if (paramIndexMap != null) {
			paramIndexes = paramIndexMap.get(ruleEngine.getShardingKey());
		}
		if (paramValueMap != null) {
			paramValues = paramValueMap.get(ruleEngine.getShardingKey());
		}

		Object[] keyValues;
		if (CollectionUtils.isEmpty(paramIndexes) && CollectionUtils.isEmpty(paramValues)) {
			throw new SQLParserException("can not find sharding key");
		} else if (CollectionUtils.isNotEmpty(paramIndexes)) {
			keyValues = new Object[paramIndexes.size()];
			int j = 0;
			for (Integer i : paramIndexes) {
				keyValues[j++] = parameterSettings.get(i).getArgs()[1];
			}
		} else {
			keyValues = new Object[paramValues.size()];
			int j = 0;
			for (String s : paramValues) {
				keyValues[j++] = s;
			}
		}
		return keyValues;
	}

	private Object[] evalInsert(Map<Integer, ParameterContext> parameterSettings, String tbName) {
		int index = 0;
		boolean haveShardingKey = false;
		Insert insert = parserResult.getInsert();

		List<Expression> columns = insert.getColumnList().getExpressionList();
		List<Expression> values = insert.getExpressionList().getExpressionList();
		RuleEngine ruleEngine = tb2ruleEngine.get(tbName);

		for (Expression expr : columns) {
			// FIXME Anders 获取第一个出现的分表id
			if (StringUtils.contains(expr.toStr(), ruleEngine.getShardingKey())) {
				haveShardingKey = true;
				break;
			}
			++index;
		}

		if (!haveShardingKey) {
			throw new SQLParserException("can not find sharding key");
		}

		Object[] keyValues;
		if (values.get(index).toStr().equals(Constants.QUESTION_MARK)) {
			// FIXME Anders 不支持复杂类型，如clob，stream等
			keyValues = new Object[] { parameterSettings.get(index + 1).getArgs()[1] };
		} else {
			keyValues = new Object[] { values.get(index).toStr() };
		}
		return keyValues;
	}

	public String[] getShardingSQLs() {
		if (parserResult.isSelect()) {
			return getSelectSQLs();
		} else {
			Set<String> sqls = new HashSet<String>();

			for (String tbn : parserResult.getTbName2TableMap().keySet()) {
				Object[] keyValues = tb2KeyValues.get(tbn);
				RuleEngine ruleEngine = tb2ruleEngine.get(tbn);
				Table table = parserResult.getTable(tbn);

				for (int i = 0; i < keyValues.length; i++) {
					String tbName = ruleEngine.eval(keyValues[i]);
					String originalTbName = table.getName();
					table.setName(tbName);
					sqls.add(parserResult.getStatement().getSQL());
					table.setName(originalTbName);
				}

			}

			return sqls.toArray(new String[sqls.size()]);
		}
	}

	private String[] getSelectSQLs() {
		List<Statement> statements0 = new ArrayList<Statement>();
		statements0.add(parserResult.getStatement());
		List<Statement> statements1 = new ArrayList<Statement>();

		Set<String> tables = new HashSet<String>();
		int index = 0;
		for (String tbn : parserResult.getTbName2TableMap().keySet()) {
			Object[] keyValues = tb2KeyValues.get(tbn);
			RuleEngine ruleEngine = tb2ruleEngine.get(tbn);

			for (int i = 0; i < keyValues.length; i++) {
				tables.add(ruleEngine.eval(keyValues[i]));
			}

			if (index % 2 == 0) {
				for (Statement statement : statements0) {
					Select select = (Select) statement;
					for (String table : tables) {
						Select cloneStatement = null;
						try {
							cloneStatement = (Select) select.clone();
							statements1.add(cloneStatement);
							cloneStatement.setTableName(tbn, table);
						} catch (CloneNotSupportedException e) {
							LOGGER.error(e.getMessage());
						}
					}
				}
				statements0.clear();
			} else {
				for (Statement statement : statements1) {
					Select select = (Select) statement;
					for (String table : tables) {
						Select cloneStatement = null;
						try {
							cloneStatement = (Select) select.clone();
							statements0.add(cloneStatement);
							cloneStatement.setTableName(tbn, table);
						} catch (CloneNotSupportedException e) {
							LOGGER.error(e.getMessage());
						}
					}
				}
				statements1.clear();
			}

			index++;
			tables.clear();
		}

		Set<String> sqls = new HashSet<String>();
		if (CollectionUtils.isNotEmpty(statements0)) {
			for (Statement statement : statements0) {
				sqls.add(statement.getSQL());
			}
		} else {
			for (Statement statement : statements1) {
				sqls.add(statement.getSQL());
			}
		}

		return sqls.toArray(new String[sqls.size()]);
	}

	public String[] getShardingDbNames() {
		Set<String> dbNames = new HashSet<String>();

		for (String tbn : parserResult.getTbName2TableMap().keySet()) {
			Object[] keyValues = tb2KeyValues.get(tbn);
			RuleEngine ruleEngine = tb2ruleEngine.get(tbn);

			for (Object keyValue : keyValues) {
				dbNames.add(ruleEngine.eval(keyValue));
			}
		}

		return dbNames.toArray(new String[dbNames.size()]);
	}

	public ParserResult getParserResult() {
		return parserResult;
	}
}
