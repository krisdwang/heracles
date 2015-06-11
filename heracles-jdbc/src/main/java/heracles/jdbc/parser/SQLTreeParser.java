package heracles.jdbc.parser;

import heracles.jdbc.parser.common.HintType;
import heracles.jdbc.parser.common.JoinType;
import heracles.jdbc.parser.common.OrderByType;
import heracles.jdbc.parser.exception.SQLParserException;
import heracles.jdbc.parser.expression.Alias;
import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.ExpressionList;
import heracles.jdbc.parser.expression.OrderByExpr;
import heracles.jdbc.parser.expression.function.AvgFunc;
import heracles.jdbc.parser.expression.function.CountFunc;
import heracles.jdbc.parser.expression.function.Function;
import heracles.jdbc.parser.expression.function.MaxFunc;
import heracles.jdbc.parser.expression.function.MinFunc;
import heracles.jdbc.parser.expression.function.SumFunc;
import heracles.jdbc.parser.expression.operators.conditional.AndExpression;
import heracles.jdbc.parser.expression.operators.conditional.OrExpression;
import heracles.jdbc.parser.expression.operators.relational.EqualsTo;
import heracles.jdbc.parser.expression.operators.relational.In;
import heracles.jdbc.parser.expression.variable.Column;
import heracles.jdbc.parser.expression.variable.Numerical;
import heracles.jdbc.parser.expression.variable.Param;
import heracles.jdbc.parser.expression.variable.QuotedString;
import heracles.jdbc.parser.expression.variable.Variable;
import heracles.jdbc.parser.statement.Statement;
import heracles.jdbc.parser.statement.delete.Delete;
import heracles.jdbc.parser.statement.insert.Insert;
import heracles.jdbc.parser.statement.select.Join;
import heracles.jdbc.parser.statement.select.Select;
import heracles.jdbc.parser.statement.select.from.Table;
import heracles.jdbc.parser.statement.select.select.AllColumns;
import heracles.jdbc.parser.statement.select.select.SelectExpression;
import heracles.jdbc.parser.statement.update.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import antlr.ASTPair;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

@SuppressWarnings("unused")
public class SQLTreeParser extends antlr.TreeParser implements SQLTreeTokenTypes {

	private Statement statement;
	private int paramIndex = 0;
	private Map<String, List<Integer>> paramIndexMap = new HashMap<String, List<Integer>>();
	private Map<String, List<String>> paramValueMap = new HashMap<String, List<String>>();
	private Map<String, Table> tableMap = new HashMap<String, Table>();
	private Map<Column, Column> columnEqualMap = new HashMap<Column, Column>();

	public int getParamCount() {
		return this.paramIndex;
	}

	public Map<String, List<Integer>> getParamIndexMap() {
		return this.paramIndexMap;
	}

	public Map<String, List<String>> getParamValueMap() {
		return this.paramValueMap;
	}

	public Map<Column, Column> getColumnEqualMap() {
		return this.columnEqualMap;
	}

	public Statement getStatement() {
		return this.statement;
	}

	public HintType getHintType() {
		return getSelect().getHintType();
	}

	public Select getSelect() {
		if (statement instanceof Select) {
			return (Select) statement;
		}
		throw new UnsupportedOperationException("only support select statement");
	}

	public Insert getInsert() {
		if (statement instanceof Insert) {
			return (Insert) statement;
		}
		throw new UnsupportedOperationException("only support insert statement");
	}

	public Delete getDelete() {
		if (statement instanceof Delete) {
			return (Delete) statement;
		}
		throw new UnsupportedOperationException("only support delete statement");
	}

	public Update getUpdate() {
		if (statement instanceof Update) {
			return (Update) statement;
		}
		throw new UnsupportedOperationException("only support update statement");
	}

	public void createStatement(int statementType) {
		if (statementType == SELECT) {
			statement = new Select();
		} else if (statementType == INSERT) {
			statement = new Insert();
		} else if (statementType == DELETE) {
			statement = new Delete();
		} else if (statementType == UPDATE) {
			statement = new Update();
		} else {
			throw new SQLParserException("not support statement type : " + statementType);
		}
	}

	public SelectExpression createSelectExpression(AST column, AST alias, boolean useAs) {
		SelectExpression item = new SelectExpression();
		item.setExpression(createColumn(column.getText()));
		if (alias != null) {
			item.setAlias(new Alias(alias.getText(), useAs));
		}
		return item;
	}

	public void addFunction(Function function, Alias alias) {
		SelectExpression item = new SelectExpression();
		item.setExpression(function);
		if (alias != null) {
			item.setAlias(alias);
		}
		getSelect().addSelectItem(item);
	}

	public Table createTable(AST table, AST alias, boolean useAs) {
		Table item = new Table();
		item.setName(table.getText());
		if (alias != null) {
			item.setAlias(new Alias(alias.getText(), useAs));
			tableMap.put(alias.getText(), item);
		}
		tableMap.put(table.getText(), item);

		return item;
	}

	public ExpressionList createSetList(AST setAst) {
		AST ast = setAst.getFirstChild();
		if (ast == null) {
			throw new SQLParserException("expressions is null");
		}

		ExpressionList expressionList = new ExpressionList();
		do {
			AST left = ast.getFirstChild();
			if (left == null) {
				throw new SQLParserException("left is null");
			}
			AST right = left.getNextSibling();
			if (right == null) {
				throw new SQLParserException("right is null");
			}

			Variable variable = null;
			if (right.getType() == PARAM) {
				variable = new Param();
			} else if (right.getType() == NUMERICAL) {
				variable = new Numerical(right.getText());
			} else if (right.getType() == QUOTED_STRING) {
				variable = new QuotedString(right.getText());
			} else {
				throw new UnsupportedOperationException();
			}

			expressionList.addExpression(new EqualsTo(createColumn(left.getText()), variable));
			ast = ast.getNextSibling();
		} while (ast != null);

		return expressionList;
	}

	public ExpressionList createColumnList(AST exprAst) {
		AST ast = exprAst.getFirstChild();
		if (ast == null) {
			throw new SQLParserException("expressions is null");
		}

		ExpressionList expressionList = new ExpressionList();
		do {
			expressionList.addExpression(createColumn(ast.getText()));
			ast = ast.getNextSibling();
		} while (ast != null);

		return expressionList;
	}

	public ExpressionList createExpressionList(AST exprAst) {
		AST ast = exprAst.getFirstChild();
		if (ast == null) {
			throw new SQLParserException("expressions is null");
		}

		ExpressionList expressionList = new ExpressionList();
		do {
			Variable variable = null;
			if (ast.getType() == PARAM) {
				variable = new Param();
			} else if (ast.getType() == NUMERICAL) {
				variable = new Numerical(ast.getText());
			} else if (ast.getType() == QUOTED_STRING) {
				variable = new QuotedString(ast.getText());
			} else {
				throw new UnsupportedOperationException();
			}
			expressionList.addExpression(variable);
			ast = ast.getNextSibling();
		} while (ast != null);

		return expressionList;
	}

	public In createIn(AST exprAst, int preIndex) {
		AST ast = exprAst.getFirstChild();
		if (ast == null) {
			throw new SQLParserException("column is null");
		}
		String column = ast.getText();
		List<Integer> indexes = paramIndexMap.get(column);
		if (indexes == null) {
			indexes = new ArrayList<Integer>();
			paramIndexMap.put(column, indexes);
		}
		List<String> values = paramValueMap.get(column);
		if (values == null) {
			values = new ArrayList<String>();
			paramValueMap.put(column, values);
		}

		ast = ast.getNextSibling();
		if (ast == null) {
			throw new SQLParserException("in expressions is null");
		}

		int index = preIndex;
		ExpressionList expressionList = new ExpressionList();
		do {
			if (ast.getType() == PARAM) {
				expressionList.addExpression(new Param());
				indexes.add(++index);
			} else if (ast.getType() == NUMERICAL) {
				expressionList.addExpression(new Numerical(ast.getText()));
				values.add(ast.getText());
			} else if (ast.getType() == QUOTED_STRING) {
				expressionList.addExpression(new QuotedString(ast.getText()));
				values.add(ast.getText());
			} else {
				throw new UnsupportedOperationException();
			}
			ast = ast.getNextSibling();
		} while (ast != null);

		return new In(createColumn(column), expressionList);
	}

	public Column createColumn(String text) {
		// if (text.contains(".")) {
		// String[] str = text.split(".");
		// return new Column(str[0], str[1]);
		// }
		// return new Column(text);
		return new Column(tableMap, text);
	}

	public SQLTreeParser() {
		tokenNames = _tokenNames;
	}


	public final void statement(AST _t) throws RecognitionException {

		AST statement_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case SELECT_ROOT: {
				selectStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST) currentAST.root;
				break;
			}
			case INSERT_ROOT: {
				insertStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST) currentAST.root;
				break;
			}
			case DELETE_ROOT: {
				deleteStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST) currentAST.root;
				break;
			}
			case UPDATE_ROOT: {
				updateStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = statement_AST;
		_retTree = _t;
	}

	public final void selectStatement(AST _t) throws RecognitionException {

		AST selectStatement_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectStatement_AST = null;

		try { // for error handling
			selectRoot(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			selectStatement_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = selectStatement_AST;
		_retTree = _t;
	}

	public final void insertStatement(AST _t) throws RecognitionException {

		AST insertStatement_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertStatement_AST = null;

		try { // for error handling
			insertRoot(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			insertStatement_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = insertStatement_AST;
		_retTree = _t;
	}

	public final void deleteStatement(AST _t) throws RecognitionException {

		AST deleteStatement_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteStatement_AST = null;
		AST d_AST = null;
		AST d = null;

		Expression where = null;

		try { // for error handling
			AST __t89 = _t;
			AST tmp1_AST = null;
			AST tmp1_AST_in = null;
			tmp1_AST = astFactory.create((AST) _t);
			tmp1_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp1_AST);
			ASTPair __currentAST89 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, DELETE_ROOT);
			_t = _t.getFirstChild();

			createStatement(DELETE);

			d = _t == ASTNULL ? null : (AST) _t;
			deleteClause(_t);
			_t = _retTree;
			d_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			where = whereClause(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			getDelete().setWhere(where);
			currentAST = __currentAST89;
			_t = __t89;
			_t = _t.getNextSibling();
			deleteStatement_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = deleteStatement_AST;
		_retTree = _t;
	}

	public final void updateStatement(AST _t) throws RecognitionException {

		AST updateStatement_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateStatement_AST = null;
		AST s_AST = null;
		AST s = null;

		Expression where = null;

		try { // for error handling
			AST __t93 = _t;
			AST tmp2_AST = null;
			AST tmp2_AST_in = null;
			tmp2_AST = astFactory.create((AST) _t);
			tmp2_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp2_AST);
			ASTPair __currentAST93 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, UPDATE_ROOT);
			_t = _t.getFirstChild();

			createStatement(UPDATE);

			updateClause(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			s = _t == ASTNULL ? null : (AST) _t;
			setClause(_t);
			_t = _retTree;
			s_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			getUpdate().setSetList(createSetList(s_AST));
			where = whereClause(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			getUpdate().setWhere(where);
			currentAST = __currentAST93;
			_t = __t93;
			_t = _t.getNextSibling();
			updateStatement_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = updateStatement_AST;
		_retTree = _t;
	}

	public final void selectRoot(AST _t) throws RecognitionException {

		AST selectRoot_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectRoot_AST = null;
		AST s_AST = null;
		AST s = null;
		AST f_AST = null;
		AST f = null;

		Expression where = null;

		try { // for error handling
			AST __t4 = _t;
			AST tmp3_AST = null;
			AST tmp3_AST_in = null;
			tmp3_AST = astFactory.create((AST) _t);
			tmp3_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp3_AST);
			ASTPair __currentAST4 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, SELECT_ROOT);
			_t = _t.getFirstChild();

			createStatement(SELECT);

			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case FORCE_READ: {
					hintStatement(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case SELECT: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			s = _t == ASTNULL ? null : (AST) _t;
			selectClause(_t);
			_t = _retTree;
			s_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			f = _t == ASTNULL ? null : (AST) _t;
			fromClause(_t);
			_t = _retTree;
			f_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			where = whereClause(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			getSelect().setWhere(where);
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case ORDER: {
					orderByClause(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 3:
				case LIMIT: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case LIMIT: {
					limitClause(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 3: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST4;
			_t = __t4;
			_t = _t.getNextSibling();

			selectRoot_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = selectRoot_AST;
		_retTree = _t;
	}

	public final void hintStatement(AST _t) throws RecognitionException {

		AST hintStatement_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST hintStatement_AST = null;

		try { // for error handling
			AST tmp4_AST = null;
			AST tmp4_AST_in = null;
			tmp4_AST = astFactory.create((AST) _t);
			tmp4_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp4_AST);
			match(_t, FORCE_READ);
			_t = _t.getNextSibling();

			getSelect().setHintType(HintType.FORCE_READ);

			hintStatement_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = hintStatement_AST;
		_retTree = _t;
	}

	public final void selectClause(AST _t) throws RecognitionException {

		AST selectClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectClause_AST = null;

		try { // for error handling
			AST __t10 = _t;
			AST tmp5_AST = null;
			AST tmp5_AST_in = null;
			tmp5_AST = astFactory.create((AST) _t);
			tmp5_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp5_AST);
			ASTPair __currentAST10 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, SELECT);
			_t = _t.getFirstChild();

			selectList(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST10;
			_t = __t10;
			_t = _t.getNextSibling();

			selectClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = selectClause_AST;
		_retTree = _t;
	}

	public final void fromClause(AST _t) throws RecognitionException {

		AST fromClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromClause_AST = null;

		try { // for error handling
			AST __t41 = _t;
			AST tmp6_AST = null;
			AST tmp6_AST_in = null;
			tmp6_AST = astFactory.create((AST) _t);
			tmp6_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp6_AST);
			ASTPair __currentAST41 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, FROM);
			_t = _t.getFirstChild();
			fromExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
				_loop43: do {
					if (_t == null)
						_t = ASTNULL;
					if ((_tokenSet_0.member(_t.getType()))) {
						joinClause(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						break _loop43;
					}

				} while (true);
			}
			currentAST = __currentAST41;
			_t = __t41;
			_t = _t.getNextSibling();
			fromClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = fromClause_AST;
		_retTree = _t;
	}

	public final Expression whereClause(AST _t) throws RecognitionException {
		Expression expr;

		AST whereClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whereClause_AST = null;

		expr = null;

		try { // for error handling
			AST __t59 = _t;
			AST tmp7_AST = null;
			AST tmp7_AST_in = null;
			tmp7_AST = astFactory.create((AST) _t);
			tmp7_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp7_AST);
			ASTPair __currentAST59 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, WHERE);
			_t = _t.getFirstChild();
			expr = logicalExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST59;
			_t = __t59;
			_t = _t.getNextSibling();

			whereClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = whereClause_AST;
		_retTree = _t;
		return expr;
	}

	public final void orderByClause(AST _t) throws RecognitionException {

		AST orderByClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderByClause_AST = null;

		try { // for error handling
			AST __t63 = _t;
			AST tmp8_AST = null;
			AST tmp8_AST_in = null;
			tmp8_AST = astFactory.create((AST) _t);
			tmp8_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp8_AST);
			ASTPair __currentAST63 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, ORDER);
			_t = _t.getFirstChild();
			orderByExpr(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
				_loop65: do {
					if (_t == null)
						_t = ASTNULL;
					if ((_t.getType() == COMMA)) {
						AST tmp9_AST_in = null;
						match(_t, COMMA);
						_t = _t.getNextSibling();
						orderByExpr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						break _loop65;
					}

				} while (true);
			}
			currentAST = __currentAST63;
			_t = __t63;
			_t = _t.getNextSibling();
			orderByClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = orderByClause_AST;
		_retTree = _t;
	}

	public final void limitClause(AST _t) throws RecognitionException {

		AST limitClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST limitClause_AST = null;
		AST i = null;
		AST i_AST = null;
		AST j = null;
		AST j_AST = null;

		try { // for error handling
			AST __t70 = _t;
			AST tmp10_AST = null;
			AST tmp10_AST_in = null;
			tmp10_AST = astFactory.create((AST) _t);
			tmp10_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp10_AST);
			ASTPair __currentAST70 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, LIMIT);
			_t = _t.getFirstChild();
			i = (AST) _t;
			AST i_AST_in = null;
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(_t, NUMERICAL);
			_t = _t.getNextSibling();
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case NUMERICAL: {
					j = (AST) _t;
					AST j_AST_in = null;
					j_AST = astFactory.create(j);
					astFactory.addASTChild(currentAST, j_AST);
					match(_t, NUMERICAL);
					_t = _t.getNextSibling();
					break;
				}
				case 3: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST70;
			_t = __t70;
			_t = _t.getNextSibling();

			if (j_AST == null) {
				getSelect().setRowCount(new Numerical(i_AST.getText()));
			} else {
				getSelect().setLimit(new Numerical(i_AST.getText()), new Numerical(j_AST.getText()));
			}

			limitClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = limitClause_AST;
		_retTree = _t;
	}

	public final void selectList(AST _t) throws RecognitionException {

		AST selectList_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectList_AST = null;

		try { // for error handling
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case STAR: {
					AST tmp11_AST = null;
					AST tmp11_AST_in = null;
					tmp11_AST = astFactory.create((AST) _t);
					tmp11_AST_in = (AST) _t;
					astFactory.addASTChild(currentAST, tmp11_AST);
					match(_t, STAR);
					_t = _t.getNextSibling();

					getSelect().addSelectItem(new AllColumns());

					break;
				}
				case AS:
				case MAX:
				case MIN:
				case AVG:
				case SUM:
				case COUNT:
				case IDENT: {
					selectExpression(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			{
				_loop14: do {
					if (_t == null)
						_t = ASTNULL;
					if ((_t.getType() == COMMA)) {
						AST tmp12_AST_in = null;
						match(_t, COMMA);
						_t = _t.getNextSibling();
						selectExpression(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						break _loop14;
					}

				} while (true);
			}
			selectList_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = selectList_AST;
		_retTree = _t;
	}

	public final void selectExpression(AST _t) throws RecognitionException {

		AST selectExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectExpression_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case AS:
			case IDENT: {
				selectExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				selectExpression_AST = (AST) currentAST.root;
				break;
			}
			case MAX: {
				maxFunc(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				selectExpression_AST = (AST) currentAST.root;
				break;
			}
			case MIN: {
				minFunc(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				selectExpression_AST = (AST) currentAST.root;
				break;
			}
			case SUM: {
				sumFunc(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				selectExpression_AST = (AST) currentAST.root;
				break;
			}
			case AVG: {
				avgFunc(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				selectExpression_AST = (AST) currentAST.root;
				break;
			}
			case COUNT: {
				countFunc(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				selectExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = selectExpression_AST;
		_retTree = _t;
	}

	public final void selectExpr(AST _t) throws RecognitionException {

		AST selectExpr_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectExpr_AST = null;
		AST c1 = null;
		AST c1_AST = null;
		AST a1 = null;
		AST a1_AST = null;
		AST c2 = null;
		AST c2_AST = null;
		AST a2 = null;
		AST a2_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				c1 = (AST) _t;
				AST c1_AST_in = null;
				c1_AST = astFactory.create(c1);
				astFactory.addASTChild(currentAST, c1_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				{
					if (_t == null)
						_t = ASTNULL;
					switch (_t.getType()) {
					case IDENT: {
						a1 = (AST) _t;
						AST a1_AST_in = null;
						a1_AST = astFactory.create(a1);
						astFactory.addASTChild(currentAST, a1_AST);
						match(_t, IDENT);
						_t = _t.getNextSibling();
						break;
					}
					case 3:
					case COMMA: {
						break;
					}
					default: {
						throw new NoViableAltException(_t);
					}
					}
				}

				getSelect().addSelectItem(createSelectExpression(c1_AST, a1_AST, false));

				selectExpr_AST = (AST) currentAST.root;
				break;
			}
			case AS: {
				AST __t39 = _t;
				AST tmp13_AST = null;
				AST tmp13_AST_in = null;
				tmp13_AST = astFactory.create((AST) _t);
				tmp13_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp13_AST);
				ASTPair __currentAST39 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, AS);
				_t = _t.getFirstChild();
				c2 = (AST) _t;
				AST c2_AST_in = null;
				c2_AST = astFactory.create(c2);
				astFactory.addASTChild(currentAST, c2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				a2 = (AST) _t;
				AST a2_AST_in = null;
				a2_AST = astFactory.create(a2);
				astFactory.addASTChild(currentAST, a2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST39;
				_t = __t39;
				_t = _t.getNextSibling();

				getSelect().addSelectItem(createSelectExpression(c2_AST, a2_AST, true));

				selectExpr_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = selectExpr_AST;
		_retTree = _t;
	}

	public final void maxFunc(AST _t) throws RecognitionException {

		AST maxFunc_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maxFunc_AST = null;
		AST id = null;
		AST id_AST = null;

		Alias alias = null;

		try { // for error handling
			AST __t17 = _t;
			AST tmp14_AST = null;
			AST tmp14_AST_in = null;
			tmp14_AST = astFactory.create((AST) _t);
			tmp14_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp14_AST);
			ASTPair __currentAST17 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, MAX);
			_t = _t.getFirstChild();
			id = (AST) _t;
			AST id_AST_in = null;
			id_AST = astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case AS:
				case IDENT: {
					alias = aliasedSuffix(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 3: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST17;
			_t = __t17;
			_t = _t.getNextSibling();

			Column column = createColumn(id_AST.getText());
			addFunction(new MaxFunc(column), alias);

			maxFunc_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = maxFunc_AST;
		_retTree = _t;
	}

	public final void minFunc(AST _t) throws RecognitionException {

		AST minFunc_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST minFunc_AST = null;
		AST id = null;
		AST id_AST = null;

		Alias alias = null;

		try { // for error handling
			AST __t20 = _t;
			AST tmp15_AST = null;
			AST tmp15_AST_in = null;
			tmp15_AST = astFactory.create((AST) _t);
			tmp15_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp15_AST);
			ASTPair __currentAST20 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, MIN);
			_t = _t.getFirstChild();
			id = (AST) _t;
			AST id_AST_in = null;
			id_AST = astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case AS:
				case IDENT: {
					alias = aliasedSuffix(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 3: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST20;
			_t = __t20;
			_t = _t.getNextSibling();

			Column column = createColumn(id_AST.getText());
			addFunction(new MinFunc(column), alias);

			minFunc_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = minFunc_AST;
		_retTree = _t;
	}

	public final void sumFunc(AST _t) throws RecognitionException {

		AST sumFunc_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sumFunc_AST = null;
		AST id = null;
		AST id_AST = null;

		Alias alias = null;

		try { // for error handling
			AST __t23 = _t;
			AST tmp16_AST = null;
			AST tmp16_AST_in = null;
			tmp16_AST = astFactory.create((AST) _t);
			tmp16_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp16_AST);
			ASTPair __currentAST23 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, SUM);
			_t = _t.getFirstChild();
			id = (AST) _t;
			AST id_AST_in = null;
			id_AST = astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case AS:
				case IDENT: {
					alias = aliasedSuffix(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 3: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST23;
			_t = __t23;
			_t = _t.getNextSibling();

			Column column = createColumn(id_AST.getText());
			addFunction(new SumFunc(column), alias);

			sumFunc_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = sumFunc_AST;
		_retTree = _t;
	}

	public final void avgFunc(AST _t) throws RecognitionException {

		AST avgFunc_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST avgFunc_AST = null;
		AST id = null;
		AST id_AST = null;

		Alias alias = null;

		try { // for error handling
			AST __t26 = _t;
			AST tmp17_AST = null;
			AST tmp17_AST_in = null;
			tmp17_AST = astFactory.create((AST) _t);
			tmp17_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp17_AST);
			ASTPair __currentAST26 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, AVG);
			_t = _t.getFirstChild();
			id = (AST) _t;
			AST id_AST_in = null;
			id_AST = astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case AS:
				case IDENT: {
					alias = aliasedSuffix(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case 3: {
					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST26;
			_t = __t26;
			_t = _t.getNextSibling();

			Column column = createColumn(id_AST.getText());
			addFunction(new AvgFunc(column), alias);

			avgFunc_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = avgFunc_AST;
		_retTree = _t;
	}

	public final void countFunc(AST _t) throws RecognitionException {

		AST countFunc_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST countFunc_AST = null;
		AST id = null;
		AST id_AST = null;

		Alias alias = null;

		try { // for error handling
			AST __t29 = _t;
			AST tmp18_AST = null;
			AST tmp18_AST_in = null;
			tmp18_AST = astFactory.create((AST) _t);
			tmp18_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp18_AST);
			ASTPair __currentAST29 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, COUNT);
			_t = _t.getFirstChild();
			{
				if (_t == null)
					_t = ASTNULL;
				switch (_t.getType()) {
				case IDENT: {
					{
						id = (AST) _t;
						AST id_AST_in = null;
						id_AST = astFactory.create(id);
						astFactory.addASTChild(currentAST, id_AST);
						match(_t, IDENT);
						_t = _t.getNextSibling();
						{
							if (_t == null)
								_t = ASTNULL;
							switch (_t.getType()) {
							case AS:
							case IDENT: {
								alias = aliasedSuffix(_t);
								_t = _retTree;
								astFactory.addASTChild(currentAST, returnAST);
								break;
							}
							case 3: {
								break;
							}
							default: {
								throw new NoViableAltException(_t);
							}
							}
						}
					}

					Column column = createColumn(id_AST.getText());
					addFunction(new CountFunc(column), alias);

					break;
				}
				case STAR: {
					{
						AST tmp19_AST = null;
						AST tmp19_AST_in = null;
						tmp19_AST = astFactory.create((AST) _t);
						tmp19_AST_in = (AST) _t;
						astFactory.addASTChild(currentAST, tmp19_AST);
						match(_t, STAR);
						_t = _t.getNextSibling();
						{
							if (_t == null)
								_t = ASTNULL;
							switch (_t.getType()) {
							case AS:
							case IDENT: {
								alias = aliasedSuffix(_t);
								_t = _retTree;
								astFactory.addASTChild(currentAST, returnAST);
								break;
							}
							case 3: {
								break;
							}
							default: {
								throw new NoViableAltException(_t);
							}
							}
						}
					}

					addFunction(new CountFunc(), alias);

					break;
				}
				default: {
					throw new NoViableAltException(_t);
				}
				}
			}
			currentAST = __currentAST29;
			_t = __t29;
			_t = _t.getNextSibling();
			countFunc_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = countFunc_AST;
		_retTree = _t;
	}

	public final Alias aliasedSuffix(AST _t) throws RecognitionException {
		Alias alias;

		AST aliasedSuffix_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aliasedSuffix_AST = null;
		AST i1 = null;
		AST i1_AST = null;
		AST i2 = null;
		AST i2_AST = null;

		alias = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				i1 = (AST) _t;
				AST i1_AST_in = null;
				i1_AST = astFactory.create(i1);
				astFactory.addASTChild(currentAST, i1_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();

				alias = new Alias(i1.getText(), false);

				aliasedSuffix_AST = (AST) currentAST.root;
				break;
			}
			case AS: {
				AST __t36 = _t;
				AST tmp20_AST = null;
				AST tmp20_AST_in = null;
				tmp20_AST = astFactory.create((AST) _t);
				tmp20_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp20_AST);
				ASTPair __currentAST36 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, AS);
				_t = _t.getFirstChild();
				i2 = (AST) _t;
				AST i2_AST_in = null;
				i2_AST = astFactory.create(i2);
				astFactory.addASTChild(currentAST, i2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST36;
				_t = __t36;
				_t = _t.getNextSibling();

				alias = new Alias(i2.getText(), true);

				aliasedSuffix_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = aliasedSuffix_AST;
		_retTree = _t;
		return alias;
	}

	public final void fromExpression(AST _t) throws RecognitionException {

		AST fromExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromExpression_AST = null;
		AST c1 = null;
		AST c1_AST = null;
		AST a1 = null;
		AST a1_AST = null;
		AST c2 = null;
		AST c2_AST = null;
		AST a2 = null;
		AST a2_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				c1 = (AST) _t;
				AST c1_AST_in = null;
				c1_AST = astFactory.create(c1);
				astFactory.addASTChild(currentAST, c1_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				{
					if (_t == null)
						_t = ASTNULL;
					switch (_t.getType()) {
					case IDENT: {
						a1 = (AST) _t;
						AST a1_AST_in = null;
						a1_AST = astFactory.create(a1);
						astFactory.addASTChild(currentAST, a1_AST);
						match(_t, IDENT);
						_t = _t.getNextSibling();
						break;
					}
					case 3:
					case LEFT:
					case RIGHT:
					case JOIN:
					case COMMA: {
						break;
					}
					default: {
						throw new NoViableAltException(_t);
					}
					}
				}

				statement.setFromItem(createTable(c1_AST, a1_AST, false));

				fromExpression_AST = (AST) currentAST.root;
				break;
			}
			case AS: {
				AST __t54 = _t;
				AST tmp21_AST = null;
				AST tmp21_AST_in = null;
				tmp21_AST = astFactory.create((AST) _t);
				tmp21_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp21_AST);
				ASTPair __currentAST54 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, AS);
				_t = _t.getFirstChild();
				c2 = (AST) _t;
				AST c2_AST_in = null;
				c2_AST = astFactory.create(c2);
				astFactory.addASTChild(currentAST, c2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				a2 = (AST) _t;
				AST a2_AST_in = null;
				a2_AST = astFactory.create(a2);
				astFactory.addASTChild(currentAST, a2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST54;
				_t = __t54;
				_t = _t.getNextSibling();

				statement.setFromItem(createTable(c2_AST, a2_AST, true));

				fromExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = fromExpression_AST;
		_retTree = _t;
	}

	public final void joinClause(AST _t) throws RecognitionException {

		AST joinClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinClause_AST = null;

		Table table = null;
		Expression on = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case COMMA: {
				AST __t45 = _t;
				AST tmp22_AST = null;
				AST tmp22_AST_in = null;
				tmp22_AST = astFactory.create((AST) _t);
				tmp22_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp22_AST);
				ASTPair __currentAST45 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, COMMA);
				_t = _t.getFirstChild();
				joinFromExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST45;
				_t = __t45;
				_t = _t.getNextSibling();

				joinClause_AST = (AST) currentAST.root;
				break;
			}
			case LEFT: {
				AST __t46 = _t;
				AST tmp23_AST = null;
				AST tmp23_AST_in = null;
				tmp23_AST = astFactory.create((AST) _t);
				tmp23_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp23_AST);
				ASTPair __currentAST46 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, LEFT);
				_t = _t.getFirstChild();
				table = joinExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				on = onClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST46;
				_t = __t46;
				_t = _t.getNextSibling();

				getSelect().addJoin(new Join(JoinType.LEFT, table, on));

				joinClause_AST = (AST) currentAST.root;
				break;
			}
			case RIGHT: {
				AST __t47 = _t;
				AST tmp24_AST = null;
				AST tmp24_AST_in = null;
				tmp24_AST = astFactory.create((AST) _t);
				tmp24_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp24_AST);
				ASTPair __currentAST47 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, RIGHT);
				_t = _t.getFirstChild();
				table = joinExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				on = onClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST47;
				_t = __t47;
				_t = _t.getNextSibling();

				getSelect().addJoin(new Join(JoinType.RIGHT, table, on));

				joinClause_AST = (AST) currentAST.root;
				break;
			}
			case JOIN: {
				AST __t48 = _t;
				AST tmp25_AST = null;
				AST tmp25_AST_in = null;
				tmp25_AST = astFactory.create((AST) _t);
				tmp25_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp25_AST);
				ASTPair __currentAST48 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, JOIN);
				_t = _t.getFirstChild();
				table = joinExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				on = onClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST48;
				_t = __t48;
				_t = _t.getNextSibling();

				getSelect().addJoin(new Join(JoinType.INNER, table, on));

				joinClause_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = joinClause_AST;
		_retTree = _t;
	}

	public final void joinFromExpression(AST _t) throws RecognitionException {

		AST joinFromExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinFromExpression_AST = null;
		AST c1 = null;
		AST c1_AST = null;
		AST a1 = null;
		AST a1_AST = null;
		AST c2 = null;
		AST c2_AST = null;
		AST a2 = null;
		AST a2_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				c1 = (AST) _t;
				AST c1_AST_in = null;
				c1_AST = astFactory.create(c1);
				astFactory.addASTChild(currentAST, c1_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				{
					if (_t == null)
						_t = ASTNULL;
					switch (_t.getType()) {
					case IDENT: {
						a1 = (AST) _t;
						AST a1_AST_in = null;
						a1_AST = astFactory.create(a1);
						astFactory.addASTChild(currentAST, a1_AST);
						match(_t, IDENT);
						_t = _t.getNextSibling();
						break;
					}
					case 3: {
						break;
					}
					default: {
						throw new NoViableAltException(_t);
					}
					}
				}

				getSelect().addFromItem(createTable(c1_AST, a1_AST, false));

				joinFromExpression_AST = (AST) currentAST.root;
				break;
			}
			case AS: {
				AST __t57 = _t;
				AST tmp26_AST = null;
				AST tmp26_AST_in = null;
				tmp26_AST = astFactory.create((AST) _t);
				tmp26_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp26_AST);
				ASTPair __currentAST57 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, AS);
				_t = _t.getFirstChild();
				c2 = (AST) _t;
				AST c2_AST_in = null;
				c2_AST = astFactory.create(c2);
				astFactory.addASTChild(currentAST, c2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				a2 = (AST) _t;
				AST a2_AST_in = null;
				a2_AST = astFactory.create(a2);
				astFactory.addASTChild(currentAST, a2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST57;
				_t = __t57;
				_t = _t.getNextSibling();

				getSelect().addFromItem(createTable(c2_AST, a2_AST, true));

				joinFromExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = joinFromExpression_AST;
		_retTree = _t;
	}

	public final Table joinExpression(AST _t) throws RecognitionException {
		Table table;

		AST joinExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinExpression_AST = null;
		AST c1 = null;
		AST c1_AST = null;
		AST a1 = null;
		AST a1_AST = null;
		AST c2 = null;
		AST c2_AST = null;
		AST a2 = null;
		AST a2_AST = null;

		table = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				c1 = (AST) _t;
				AST c1_AST_in = null;
				c1_AST = astFactory.create(c1);
				astFactory.addASTChild(currentAST, c1_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				{
					if (_t == null)
						_t = ASTNULL;
					switch (_t.getType()) {
					case IDENT: {
						a1 = (AST) _t;
						AST a1_AST_in = null;
						a1_AST = astFactory.create(a1);
						astFactory.addASTChild(currentAST, a1_AST);
						match(_t, IDENT);
						_t = _t.getNextSibling();
						break;
					}
					case ON: {
						break;
					}
					default: {
						throw new NoViableAltException(_t);
					}
					}
				}

				table = createTable(c1_AST, a1_AST, false);

				joinExpression_AST = (AST) currentAST.root;
				break;
			}
			case AS: {
				AST __t51 = _t;
				AST tmp27_AST = null;
				AST tmp27_AST_in = null;
				tmp27_AST = astFactory.create((AST) _t);
				tmp27_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp27_AST);
				ASTPair __currentAST51 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, AS);
				_t = _t.getFirstChild();
				c2 = (AST) _t;
				AST c2_AST_in = null;
				c2_AST = astFactory.create(c2);
				astFactory.addASTChild(currentAST, c2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				a2 = (AST) _t;
				AST a2_AST_in = null;
				a2_AST = astFactory.create(a2);
				astFactory.addASTChild(currentAST, a2_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST51;
				_t = __t51;
				_t = _t.getNextSibling();

				table = createTable(c2_AST, a2_AST, true);

				joinExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = joinExpression_AST;
		_retTree = _t;
		return table;
	}

	public final Expression onClause(AST _t) throws RecognitionException {
		Expression expr;

		AST onClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST onClause_AST = null;

		expr = null;

		try { // for error handling
			AST __t61 = _t;
			AST tmp28_AST = null;
			AST tmp28_AST_in = null;
			tmp28_AST = astFactory.create((AST) _t);
			tmp28_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp28_AST);
			ASTPair __currentAST61 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, ON);
			_t = _t.getFirstChild();
			expr = logicalExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST61;
			_t = __t61;
			_t = _t.getNextSibling();

			onClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = onClause_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression logicalExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST logicalExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpression_AST = null;

		expr = null;

		try { // for error handling
			expr = expression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			logicalExpression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = logicalExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final void orderByExpr(AST _t) throws RecognitionException {

		AST orderByExpr_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderByExpr_AST = null;
		AST i = null;
		AST i_AST = null;
		AST a = null;
		AST a_AST = null;
		AST d = null;
		AST d_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				i = (AST) _t;
				AST i_AST_in = null;
				i_AST = astFactory.create(i);
				astFactory.addASTChild(currentAST, i_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();

				getSelect().addOrderByExpr(new OrderByExpr(tableMap, i_AST.getText()));

				orderByExpr_AST = (AST) currentAST.root;
				break;
			}
			case ASC: {
				AST __t67 = _t;
				AST tmp29_AST = null;
				AST tmp29_AST_in = null;
				tmp29_AST = astFactory.create((AST) _t);
				tmp29_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp29_AST);
				ASTPair __currentAST67 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, ASC);
				_t = _t.getFirstChild();
				a = (AST) _t;
				AST a_AST_in = null;
				a_AST = astFactory.create(a);
				astFactory.addASTChild(currentAST, a_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST67;
				_t = __t67;
				_t = _t.getNextSibling();

				getSelect().addOrderByExpr(new OrderByExpr(tableMap, a_AST.getText(), OrderByType.ASC));

				orderByExpr_AST = (AST) currentAST.root;
				break;
			}
			case DESC: {
				AST __t68 = _t;
				AST tmp30_AST = null;
				AST tmp30_AST_in = null;
				tmp30_AST = astFactory.create((AST) _t);
				tmp30_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp30_AST);
				ASTPair __currentAST68 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, DESC);
				_t = _t.getFirstChild();
				d = (AST) _t;
				AST d_AST_in = null;
				d_AST = astFactory.create(d);
				astFactory.addASTChild(currentAST, d_AST);
				match(_t, IDENT);
				_t = _t.getNextSibling();
				currentAST = __currentAST68;
				_t = __t68;
				_t = _t.getNextSibling();

				getSelect().addOrderByExpr(new OrderByExpr(tableMap, d_AST.getText(), OrderByType.DESC));

				orderByExpr_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = orderByExpr_AST;
		_retTree = _t;
	}

	public final void insertRoot(AST _t) throws RecognitionException {

		AST insertRoot_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertRoot_AST = null;
		AST i_AST = null;
		AST i = null;
		AST c_AST = null;
		AST c = null;
		AST v_AST = null;
		AST v = null;

		try { // for error handling
			AST __t74 = _t;
			AST tmp31_AST = null;
			AST tmp31_AST_in = null;
			tmp31_AST = astFactory.create((AST) _t);
			tmp31_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp31_AST);
			ASTPair __currentAST74 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, INSERT_ROOT);
			_t = _t.getFirstChild();

			createStatement(INSERT);

			i = _t == ASTNULL ? null : (AST) _t;
			insertClause(_t);
			_t = _retTree;
			i_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			c = _t == ASTNULL ? null : (AST) _t;
			columnList(_t);
			_t = _retTree;
			c_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			getInsert().setColumnList(createColumnList(c_AST));
			v = _t == ASTNULL ? null : (AST) _t;
			valuesClause(_t);
			_t = _retTree;
			v_AST = (AST) returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			getInsert().setExpressionList(createExpressionList(v_AST));
			currentAST = __currentAST74;
			_t = __t74;
			_t = _t.getNextSibling();

			insertRoot_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = insertRoot_AST;
		_retTree = _t;
	}

	public final void insertClause(AST _t) throws RecognitionException {

		AST insertClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertClause_AST = null;
		AST i = null;
		AST i_AST = null;

		try { // for error handling
			AST __t76 = _t;
			AST tmp32_AST = null;
			AST tmp32_AST_in = null;
			tmp32_AST = astFactory.create((AST) _t);
			tmp32_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp32_AST);
			ASTPair __currentAST76 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, INSERT);
			_t = _t.getFirstChild();
			i = (AST) _t;
			AST i_AST_in = null;
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			currentAST = __currentAST76;
			_t = __t76;
			_t = _t.getNextSibling();

			statement.setFromItem(createTable(i_AST, null, false));

			insertClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = insertClause_AST;
		_retTree = _t;
	}

	public final void columnList(AST _t) throws RecognitionException {

		AST columnList_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST columnList_AST = null;

		try { // for error handling
			AST __t78 = _t;
			AST tmp33_AST = null;
			AST tmp33_AST_in = null;
			tmp33_AST = astFactory.create((AST) _t);
			tmp33_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp33_AST);
			ASTPair __currentAST78 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, COLUMN_LIST);
			_t = _t.getFirstChild();
			column(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
				_loop80: do {
					if (_t == null)
						_t = ASTNULL;
					if ((_t.getType() == IDENT)) {
						column(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						break _loop80;
					}

				} while (true);
			}
			currentAST = __currentAST78;
			_t = __t78;
			_t = _t.getNextSibling();
			columnList_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = columnList_AST;
		_retTree = _t;
	}

	public final void valuesClause(AST _t) throws RecognitionException {

		AST valuesClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valuesClause_AST = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case VALUES: {
				AST __t82 = _t;
				AST tmp34_AST = null;
				AST tmp34_AST_in = null;
				tmp34_AST = astFactory.create((AST) _t);
				tmp34_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp34_AST);
				ASTPair __currentAST82 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, VALUES);
				_t = _t.getFirstChild();
				variable(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				{
					_loop84: do {
						if (_t == null)
							_t = ASTNULL;
						if ((_t.getType() == NUMERICAL || _t.getType() == QUOTED_STRING || _t.getType() == PARAM)) {
							variable(_t);
							_t = _retTree;
							astFactory.addASTChild(currentAST, returnAST);
						} else {
							break _loop84;
						}

					} while (true);
				}
				currentAST = __currentAST82;
				_t = __t82;
				_t = _t.getNextSibling();
				valuesClause_AST = (AST) currentAST.root;
				break;
			}
			case VALUE: {
				AST __t85 = _t;
				AST tmp35_AST = null;
				AST tmp35_AST_in = null;
				tmp35_AST = astFactory.create((AST) _t);
				tmp35_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp35_AST);
				ASTPair __currentAST85 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, VALUE);
				_t = _t.getFirstChild();
				variable(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				{
					_loop87: do {
						if (_t == null)
							_t = ASTNULL;
						if ((_t.getType() == NUMERICAL || _t.getType() == QUOTED_STRING || _t.getType() == PARAM)) {
							variable(_t);
							_t = _retTree;
							astFactory.addASTChild(currentAST, returnAST);
						} else {
							break _loop87;
						}

					} while (true);
				}
				currentAST = __currentAST85;
				_t = __t85;
				_t = _t.getNextSibling();
				valuesClause_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = valuesClause_AST;
		_retTree = _t;
	}

	public final Expression column(AST _t) throws RecognitionException {
		Expression value;

		AST column_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST column_AST = null;
		AST i = null;
		AST i_AST = null;

		value = null;

		try { // for error handling
			i = (AST) _t;
			AST i_AST_in = null;
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			value = createColumn(i_AST.getText());
			column_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = column_AST;
		_retTree = _t;
		return value;
	}

	public final Expression variable(AST _t) throws RecognitionException {
		Expression value;

		AST variable_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_AST = null;
		AST n = null;
		AST n_AST = null;
		AST q = null;
		AST q_AST = null;

		value = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case NUMERICAL: {
				n = (AST) _t;
				AST n_AST_in = null;
				n_AST = astFactory.create(n);
				astFactory.addASTChild(currentAST, n_AST);
				match(_t, NUMERICAL);
				_t = _t.getNextSibling();
				value = new Numerical(n_AST.getText());
				variable_AST = (AST) currentAST.root;
				break;
			}
			case QUOTED_STRING: {
				q = (AST) _t;
				AST q_AST_in = null;
				q_AST = astFactory.create(q);
				astFactory.addASTChild(currentAST, q_AST);
				match(_t, QUOTED_STRING);
				_t = _t.getNextSibling();
				value = new QuotedString(q_AST.getText());
				variable_AST = (AST) currentAST.root;
				break;
			}
			case PARAM: {
				AST tmp36_AST = null;
				AST tmp36_AST_in = null;
				tmp36_AST = astFactory.create((AST) _t);
				tmp36_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp36_AST);
				match(_t, PARAM);
				_t = _t.getNextSibling();
				value = new Param();
				variable_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = variable_AST;
		_retTree = _t;
		return value;
	}

	public final void deleteClause(AST _t) throws RecognitionException {

		AST deleteClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteClause_AST = null;
		AST i = null;
		AST i_AST = null;

		try { // for error handling
			AST __t91 = _t;
			AST tmp37_AST = null;
			AST tmp37_AST_in = null;
			tmp37_AST = astFactory.create((AST) _t);
			tmp37_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp37_AST);
			ASTPair __currentAST91 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, DELETE);
			_t = _t.getFirstChild();
			i = (AST) _t;
			AST i_AST_in = null;
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();

			statement.setFromItem(createTable(i_AST, null, false));

			currentAST = __currentAST91;
			_t = __t91;
			_t = _t.getNextSibling();
			deleteClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = deleteClause_AST;
		_retTree = _t;
	}

	public final void updateClause(AST _t) throws RecognitionException {

		AST updateClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateClause_AST = null;

		try { // for error handling
			AST __t95 = _t;
			AST tmp38_AST = null;
			AST tmp38_AST_in = null;
			tmp38_AST = astFactory.create((AST) _t);
			tmp38_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp38_AST);
			ASTPair __currentAST95 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, UPDATE);
			_t = _t.getFirstChild();

			fromExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST95;
			_t = __t95;
			_t = _t.getNextSibling();

			updateClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = updateClause_AST;
		_retTree = _t;
	}

	public final void setClause(AST _t) throws RecognitionException {

		AST setClause_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setClause_AST = null;

		try { // for error handling
			AST __t97 = _t;
			AST tmp39_AST = null;
			AST tmp39_AST_in = null;
			tmp39_AST = astFactory.create((AST) _t);
			tmp39_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp39_AST);
			ASTPair __currentAST97 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, SET);
			_t = _t.getFirstChild();
			{
				int _cnt99 = 0;
				_loop99: do {
					if (_t == null)
						_t = ASTNULL;
					if ((_t.getType() == EQ)) {
						setEqualityExpression(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						if (_cnt99 >= 1) {
							break _loop99;
						} else {
							throw new NoViableAltException(_t);
						}
					}

					_cnt99++;
				} while (true);
			}

			currentAST = __currentAST97;
			_t = __t97;
			_t = _t.getNextSibling();
			setClause_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = setClause_AST;
		_retTree = _t;
	}

	public final Expression setEqualityExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST setEqualityExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setEqualityExpression_AST = null;

		expr = null;

		try { // for error handling
			expr = setEqualsToExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			setEqualityExpression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = setEqualityExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression expression(AST _t) throws RecognitionException {
		Expression expr;

		AST expression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;

		expr = null;

		try { // for error handling
			expr = logicalOrExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			expression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = expression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression logicalOrExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST logicalOrExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;

		expr = null;
		Expression l = null;
		Expression r = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case AND:
			case IN:
			case EQ: {
				expr = logicalAndExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);

				logicalOrExpression_AST = (AST) currentAST.root;
				break;
			}
			case OR: {
				AST __t103 = _t;
				AST tmp40_AST = null;
				AST tmp40_AST_in = null;
				tmp40_AST = astFactory.create((AST) _t);
				tmp40_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp40_AST);
				ASTPair __currentAST103 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, OR);
				_t = _t.getFirstChild();
				l = logicalAndExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				r = logicalAndExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST103;
				_t = __t103;
				_t = _t.getNextSibling();

				expr = new OrExpression(l, r);

				logicalOrExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = logicalOrExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression logicalAndExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST logicalAndExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;

		expr = null;
		Expression l = null;
		Expression r = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IN:
			case EQ: {
				expr = negatedExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);

				logicalAndExpression_AST = (AST) currentAST.root;
				break;
			}
			case AND: {
				AST __t105 = _t;
				AST tmp41_AST = null;
				AST tmp41_AST_in = null;
				tmp41_AST = astFactory.create((AST) _t);
				tmp41_AST_in = (AST) _t;
				astFactory.addASTChild(currentAST, tmp41_AST);
				ASTPair __currentAST105 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t, AND);
				_t = _t.getFirstChild();
				l = negatedExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				r = negatedExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST105;
				_t = __t105;
				_t = _t.getNextSibling();

				expr = new AndExpression(l, r);

				logicalAndExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = logicalAndExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression negatedExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST negatedExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negatedExpression_AST = null;

		expr = null;

		try { // for error handling
			expr = equalityExpression(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);

			negatedExpression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = negatedExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression equalityExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST equalityExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		AST i_AST = null;
		AST i = null;

		expr = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case EQ: {
				expr = equalsToExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				equalityExpression_AST = (AST) currentAST.root;
				break;
			}
			case IN: {
				i = _t == ASTNULL ? null : (AST) _t;
				inExpression(_t);
				_t = _retTree;
				i_AST = (AST) returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				expr = createIn(i_AST, paramIndex);
				equalityExpression_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = equalityExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression equalsToExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST equalsToExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalsToExpression_AST = null;
		AST ll = null;
		AST ll_AST = null;

		expr = null;
		Expression rr = null;
		Column column = null;

		try { // for error handling
			AST __t112 = _t;
			AST tmp42_AST = null;
			AST tmp42_AST_in = null;
			tmp42_AST = astFactory.create((AST) _t);
			tmp42_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp42_AST);
			ASTPair __currentAST112 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, EQ);
			_t = _t.getFirstChild();
			ll = (AST) _t;
			AST ll_AST_in = null;
			ll_AST = astFactory.create(ll);
			astFactory.addASTChild(currentAST, ll_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();

			column = createColumn(ll_AST.getText());

			rr = constant(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);

			currentAST = __currentAST112;
			_t = __t112;
			_t = _t.getNextSibling();

			expr = new EqualsTo(createColumn(ll_AST.getText()), rr);
			if (rr instanceof Param) {
				List<Integer> indexes = paramIndexMap.get(ll_AST.getText());
				if (indexes == null) {
					indexes = new ArrayList<Integer>();
					paramIndexMap.put(ll_AST.getText(), indexes);
				}
				indexes.add(++paramIndex);
			} else if (rr instanceof Numerical) {
				List<String> values = paramValueMap.get(ll_AST.getText());
				if (values == null) {
					values = new ArrayList<String>();
					paramValueMap.put(ll_AST.getText(), values);
				}
				values.add(rr.toStr());
			} else if (rr instanceof Column) {
				if (ll != null) {
					columnEqualMap.put(column, (Column) rr);
					columnEqualMap.put((Column) rr, column);
				}
			} else {
				throw new UnsupportedOperationException();
			}

			equalsToExpression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = equalsToExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final void inExpression(AST _t) throws RecognitionException {

		AST inExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inExpression_AST = null;

		try { // for error handling
			AST __t114 = _t;
			AST tmp43_AST = null;
			AST tmp43_AST_in = null;
			tmp43_AST = astFactory.create((AST) _t);
			tmp43_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp43_AST);
			ASTPair __currentAST114 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, IN);
			_t = _t.getFirstChild();
			AST tmp44_AST = null;
			AST tmp44_AST_in = null;
			tmp44_AST = astFactory.create((AST) _t);
			tmp44_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp44_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();
			{
				int _cnt116 = 0;
				_loop116: do {
					if (_t == null)
						_t = ASTNULL;
					if ((_t.getType() == NUMERICAL || _t.getType() == QUOTED_STRING || _t.getType() == PARAM)) {
						variable(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					} else {
						if (_cnt116 >= 1) {
							break _loop116;
						} else {
							throw new NoViableAltException(_t);
						}
					}

					_cnt116++;
				} while (true);
			}
			currentAST = __currentAST114;
			_t = __t114;
			_t = _t.getNextSibling();

			inExpression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = inExpression_AST;
		_retTree = _t;
	}

	public final Expression setEqualsToExpression(AST _t) throws RecognitionException {
		Expression expr;

		AST setEqualsToExpression_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setEqualsToExpression_AST = null;
		AST ll = null;
		AST ll_AST = null;

		expr = null;
		Expression rr = null;

		try { // for error handling
			AST __t110 = _t;
			AST tmp45_AST = null;
			AST tmp45_AST_in = null;
			tmp45_AST = astFactory.create((AST) _t);
			tmp45_AST_in = (AST) _t;
			astFactory.addASTChild(currentAST, tmp45_AST);
			ASTPair __currentAST110 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t, EQ);
			_t = _t.getFirstChild();
			ll = (AST) _t;
			AST ll_AST_in = null;
			ll_AST = astFactory.create(ll);
			astFactory.addASTChild(currentAST, ll_AST);
			match(_t, IDENT);
			_t = _t.getNextSibling();

			rr = variable(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);

			currentAST = __currentAST110;
			_t = __t110;
			_t = _t.getNextSibling();

			expr = new EqualsTo(createColumn(ll_AST.getText()), rr);
			if (rr instanceof Param) {
				List<Integer> indexes = paramIndexMap.get(ll_AST.getText());
				if (indexes == null) {
					indexes = new ArrayList<Integer>();
					paramIndexMap.put(ll_AST.getText(), indexes);
				}
				indexes.add(++paramIndex);
			} else if (rr instanceof Numerical) {
			} else {
				throw new UnsupportedOperationException();
			}

			setEqualsToExpression_AST = (AST) currentAST.root;
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = setEqualsToExpression_AST;
		_retTree = _t;
		return expr;
	}

	public final Expression constant(AST _t) throws RecognitionException {
		Expression value;

		AST constant_AST_in = (_t == ASTNULL) ? null : (AST) _t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;

		value = null;

		try { // for error handling
			if (_t == null)
				_t = ASTNULL;
			switch (_t.getType()) {
			case IDENT: {
				value = column(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				constant_AST = (AST) currentAST.root;
				break;
			}
			case NUMERICAL:
			case QUOTED_STRING:
			case PARAM: {
				value = variable(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				constant_AST = (AST) currentAST.root;
				break;
			}
			default: {
				throw new NoViableAltException(_t);
			}
			}
		} catch (RecognitionException ex) {
			reportError(ex);
			if (_t != null) {
				_t = _t.getNextSibling();
			}
		}
		returnAST = constant_AST;
		_retTree = _t;
		return value;
	}

	public static final String[] _tokenNames = { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"and\"", "\"as\"",
			"\"delete\"", "\"from\"", "\"in\"", "\"insert\"", "\"into\"", "\"values\"", "\"value\"", "\"max\"",
			"\"min\"", "\"avg\"", "\"sum\"", "\"count\"", "\"or\"", "\"select\"", "\"set\"", "\"update\"", "\"where\"",
			"\"left\"", "\"right\"", "\"inner\"", "\"cross\"", "\"outer\"", "\"join\"", "\"on\"", "\"limit\"",
			"\"order\"", "\"by\"", "\"asc\"", "\"desc\"", "\"hint\"", "\"force_read\"", "SELECT_ROOT", "INSERT_ROOT",
			"DELETE_ROOT", "UPDATE_ROOT", "COLUMN_LIST", "OPEN_COMMENT", "CLOSE_COMMENT", "STAR", "COMMA", "IDENT",
			"OPEN", "CLOSE", "NUMERICAL", "EQ", "QUOTED_STRING", "PARAM", "LT", "GT", "SQL_NE", "NE", "LE", "GE",
			"OPEN_BRACKET", "CLOSE_BRACKET", "CONCAT", "PLUS", "MINUS", "DIV", "MOD", "COLON", "DOT",
			"ID_START_LETTER", "ID_LETTER", "ESCqs", "WS", "FROM_FRAGMENT" };

	private static final long[] mk_tokenSet_0() {
		long[] data = { 35184665690112L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
}
