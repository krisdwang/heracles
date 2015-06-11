package heracles.jdbc.parser;

import heracles.jdbc.parser.common.HintType;
import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.expression.variable.Column;
import heracles.jdbc.parser.statement.Statement;
import heracles.jdbc.parser.statement.delete.Delete;
import heracles.jdbc.parser.statement.insert.Insert;
import heracles.jdbc.parser.statement.select.Join;
import heracles.jdbc.parser.statement.select.Select;
import heracles.jdbc.parser.statement.select.from.FromItem;
import heracles.jdbc.parser.statement.select.from.Table;
import heracles.jdbc.parser.statement.update.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

public class ParserResult {
	private Statement statement;
	private Map<String, List<Integer>> paramIndexMap;
	private Map<String, List<String>> paramValueMap;
	private Map<Column, Column> columnEqualMap;
	private Map<String, Table> tbName2TableMap = new HashMap<String, Table>();

	public Table getTable(String tbName) {
		return tbName2TableMap.get(tbName);
	}

	public Map<String, Table> getTbName2TableMap() {
		return tbName2TableMap;
	}

	public ParserResult(Statement statement) {
		this.statement = statement;

		if (isSelect()) {
			Select select = (Select) statement;
			List<FromItem> fromItems = select.getFromItems();
			if (CollectionUtils.isNotEmpty(fromItems)) {
				for (FromItem fi : fromItems) {
					tbName2TableMap.put(((Table) fi).getName(), (Table) fi);
				}
			} else {
				List<Join> joins = select.getJoins();
				if (CollectionUtils.isNotEmpty(joins)) {
					for (Join join : joins) {
						tbName2TableMap.put(((Table) join.getFromItem()).getName(), (Table) join.getFromItem());
					}
				}
			}
		}

		tbName2TableMap.put(((Table) statement.getFromItem()).getName(), (Table) statement.getFromItem());
	}

	public Statement getStatement() {
		return statement;
	}

	public boolean isSelect() {
		Assert.notNull(statement);
		return statement instanceof Select;
	}

	public boolean isDelete() {
		Assert.notNull(statement);
		return statement instanceof Delete;
	}

	public boolean isUpdate() {
		Assert.notNull(statement);
		return statement instanceof Update;
	}

	public boolean isInsert() {
		Assert.notNull(statement);
		return statement instanceof Insert;
	}

	public HintType getHintType() {
		return getSelect().getHintType();
	}

	public StatementType getType() {
		Assert.notNull(statement);
		return statement.getType();
	}

	public Select getSelect() {
		if (isSelect()) {
			return (Select) statement;
		}
		throw new UnsupportedOperationException("only support select statement");
	}

	public Delete getDelete() {
		if (isDelete()) {
			return (Delete) statement;
		}
		throw new UnsupportedOperationException("only support delete statement");
	}

	public Update getUpdate() {
		if (isUpdate()) {
			return (Update) statement;
		}
		throw new UnsupportedOperationException("only support update statement");
	}

	public Insert getInsert() {
		if (isInsert()) {
			return (Insert) statement;
		}
		throw new UnsupportedOperationException("only support insert statement");
	}

	public Map<String, List<Integer>> getParamIndexMap() {
		return paramIndexMap;
	}

	public void setParamIndexMap(Map<String, List<Integer>> paramIndexMap) {
		this.paramIndexMap = paramIndexMap;
	}

	public Map<String, List<String>> getParamValueMap() {
		return paramValueMap;
	}

	public void setParamValueMap(Map<String, List<String>> paramValueMap) {
		this.paramValueMap = paramValueMap;
	}

	public Map<Column, Column> getColumnEqualMap() {
		if (!isSelect()) {
			throw new UnsupportedOperationException("only support select statement");
		}
		return columnEqualMap;
	}

	public void setColumnEqualMap(Map<Column, Column> columnEqualMap) {
		this.columnEqualMap = columnEqualMap;
	}

	public String getSQL() {
		Assert.notNull(statement);
		return statement.getSQL();
	}
}
