package heracles.jdbc.parser;

import heracles.jdbc.parser.common.HintType;
import heracles.jdbc.parser.exception.SQLParserException;
import heracles.jdbc.parser.statement.delete.Delete;
import heracles.jdbc.parser.statement.insert.Insert;
import heracles.jdbc.parser.statement.select.Select;
import heracles.jdbc.parser.statement.select.from.Table;
import heracles.jdbc.parser.statement.select.select.SelectExpression;
import heracles.jdbc.parser.statement.select.select.SelectItem;
import heracles.jdbc.parser.statement.update.Update;

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class SelectTest {

	@Test
	public void test1() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals("SELECT * FROM table WHERE id IN (1, 2, 3) AND name = 3", select.getSQL());
	}

	@Test(expected = SQLParserException.class)
	public void test2() throws RecognitionException, TokenStreamException {
		String sql = "select name, age from tb_user as user where 1 = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test3() throws RecognitionException, TokenStreamException {
		String sql = "select name, age from tb_user as user where 1 = id";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test4() throws RecognitionException, TokenStreamException {
		String sql = "select from tb_user as user where id = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test5() throws RecognitionException, TokenStreamException {
		String sql = "select * from as where id = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test6() throws RecognitionException, TokenStreamException {
		String sql = "select * from table id = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test7() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where id";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test8() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test
	public void test9() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where id = 1 order by id";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test10() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where id = 1 name = 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test11() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where id = 1 and name in (22 2)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test12() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where id = 1 and name in (1,2,3) and name not in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test13() throws RecognitionException, TokenStreamException {
		String sql = "select * from table where (id = 1) or name in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test14() throws RecognitionException, TokenStreamException {
		String sql = "select * from table, tb2 where (id = 1) or name in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test
	public void test15() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "select name as n, age a from table table where id = 1 and name = 'zhuzhen'";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();

		List<SelectItem> selectItems = select.getSelectItems();
		for (SelectItem selectItem : selectItems) {
			SelectExpression selectExpression = (SelectExpression) selectItem;
			// System.out.println(selectExpression.getExpression().toStr());
			System.out.println(selectExpression.toStr());
			// System.out.println(selectExpression.getAlias().getName());
			// System.out.println(selectExpression.getAlias().toStr());
		}

		System.out.println(select.getFromItem().toStr());
		System.out.println(select.getWhere().toStr());
	}

	@Test
	public void test16() throws RecognitionException, TokenStreamException {
		String sql = "select id as i, t.name n from table as t where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals("SELECT id AS i, t.name n FROM table AS t WHERE id IN (1, 2, 3) AND name = 3",
				select.getSQL());
	}

	@Test
	public void test17() throws RecognitionException, TokenStreamException {
		String sql = "select id as i, t.name n from table as t left join table1 on table1.id = t.id where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT id AS i, t.name n FROM table AS t LEFT JOIN table1 ON table1.id = t.id WHERE id IN (1, 2, 3) AND name = 3",
				select.getSQL());
	}

	@Test
	public void test18() throws RecognitionException, TokenStreamException {
		String sql = "select id as i, t.name n from table as t left join table1 on table1.id = t.id left join table2 dt on table1.id = dt.id where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT id AS i, t.name n FROM table AS t LEFT JOIN table1 ON table1.id = t.id LEFT JOIN table2 dt ON table1.id = dt.id WHERE id IN (1, 2, 3) AND name = 3",
				select.getSQL());
	}

	@Test
	public void test19() throws RecognitionException, TokenStreamException {
		String sql = "select id as i, t.name n from table as t right join table1 on table1.id = t.id left join table2 dt on table1.id = dt.id where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT id AS i, t.name n FROM table AS t RIGHT JOIN table1 ON table1.id = t.id LEFT JOIN table2 dt ON table1.id = dt.id WHERE id IN (1, 2, 3) AND name = 3",
				select.getSQL());
	}

	@Test
	public void test20() throws RecognitionException, TokenStreamException {
		String sql = "select id as i, t.name n from table as t join table1 on table1.id = t.id left join table2 dt on table1.id = dt.id where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT id AS i, t.name n FROM table AS t JOIN table1 ON table1.id = t.id LEFT JOIN table2 dt ON table1.id = dt.id WHERE id IN (1, 2, 3) AND name = 3",
				select.getSQL());
	}

	@Test
	public void test21() throws RecognitionException, TokenStreamException {
		String sql = "select id as i, t.name n from table as t, table1 gg, dddd as dd where id in (1, 2, 3) and name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT id AS i, t.name n FROM table AS t, table1 gg, dddd AS dd WHERE id IN (1, 2, 3) AND name = 3",
				select.getSQL());
	}

	@Test
	public void test22() throws RecognitionException, TokenStreamException {
		String sql = "select * from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 1,2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT * FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 1, 2",
				select.getSQL());
	}

	@Test
	public void test23() throws RecognitionException, TokenStreamException {
		String sql = "select *,max(tt.id) as yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 1,2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT *, MAX(tt.id) AS yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 1, 2",
				select.getSQL());
	}

	@Test(expected = SQLParserException.class)
	public void test24() throws RecognitionException, TokenStreamException {
		String sql = "select * as rrr, tt.id as yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 1,2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test
	public void test25() throws RecognitionException, TokenStreamException {
		String sql = "select *,min(tt.id) as yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 1,2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT *, MIN(tt.id) AS yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 1, 2",
				select.getSQL());
	}

	@Test
	public void test26() throws RecognitionException, TokenStreamException {
		String sql = "select *,avg(tt.id) as yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 1,2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT *, AVG(tt.id) AS yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 1, 2",
				select.getSQL());
	}

	@Test
	public void test27() throws RecognitionException, TokenStreamException {
		String sql = "select *,sum(tt.id) as yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 1,2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT *, SUM(tt.id) AS yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 1, 2",
				select.getSQL());
	}

	@Test
	public void test28() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(tt.id) as yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT *, COUNT(tt.id) AS yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 2",
				select.getSQL());
	}

	@Test
	public void test29() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(*) yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT *, COUNT(*) yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 2",
				select.getSQL());
	}

	@Test(expected = SQLParserException.class)
	public void test30() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(*) yy from table tt where name = 3 order by limit 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test31() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(*) yy from table tt where name = 3 order by desc limit 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test32() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(*) yy from table tt where name = 3 order by asc limit 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test33() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(*) yy from table tt where name = 3 order by tt limit";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test34() throws RecognitionException, TokenStreamException {
		String sql = "select *,count(*) yy from table tt where name = 3 order by tt limit , 5";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test
	public void test35() throws RecognitionException, TokenStreamException {
		String sql = "select id,count(*) yy from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.SELECT_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = (Select) sqlTreeParser.getStatement();
		Assert.assertEquals(
				"SELECT id, COUNT(*) yy FROM table tt WHERE name = 3 ORDER BY tt.id, tt.name ASC, tt.ttt DESC LIMIT 2",
				select.getSQL());
	}

	@Test(expected = SQLParserException.class)
	public void test36() throws RecognitionException, TokenStreamException {
		String sql = "select id as tt, * from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test37() throws RecognitionException, TokenStreamException {
		String sql = "/* asdfas */ select id as tt, * from table tt where name = 3 order by tt.id, tt.name asc, tt.ttt desc limit 2";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test38() throws RecognitionException, TokenStreamException {
		String sql = "/* hint dvfdf */ select * from table tt where name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test39() throws RecognitionException, TokenStreamException {
		String sql = "/* dvfdf */ select * from table tt where name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test
	public void test40() throws RecognitionException, TokenStreamException {
		String sql = "/* hint force_read */ select * from table tt where name = 3";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());
		System.out.println(rootAst.toStringTree());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(parser.getAST());
		Select select = sqlTreeParser.getSelect();
		Assert.assertEquals(HintType.FORCE_READ, select.getHintType());
	}

	public void testsdfsd() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "select zhuzhen as ttt, name, dff f from tabbbb as table where a4 = a2 and a3 = 589 or a5 = 'asfasd'";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());
		System.out.println(rootAst.toStringTree());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = sqlTreeParser.getSelect();
		select.getFromItem();
		List<SelectItem> selectItems = select.getSelectItems();
		System.out.println(selectItems.size());
		for (SelectItem selectItem : selectItems) {
			System.out.println("**********field");
			SelectExpression selectExpression = (SelectExpression) selectItem;
			System.out.println(selectExpression.getExpression().toStr());
			// System.out.println(selectExpression.getAlias().getName());
			// System.out.println(selectExpression.getAlias().isUseAs());
			// System.out.println(selectExpression.getAlias().toStr());
		}
		System.out.println("**********from");
		Table table = (Table) select.getFromItem();
		System.out.println(table.getName());
		System.out.println(table.getAlias().getName());
		System.out.println(table.getAlias().isUseAs());
		System.out.println(table.getAlias().toStr());

		System.out.println("**********where");
		// System.out.println(select.getWhere().getClass());
		// System.out.println(((OrExpression) select.getWhere()).getLeftExpression());
		// System.out.println(((OrExpression) select.getWhere()).getRightExpression());
		// System.out.println(((AndExpression) ((OrExpression)
		// select.getWhere()).getRightExpression()).getLeftExpression());
		// System.out.println(((AndExpression) ((OrExpression)
		// select.getWhere()).getRightExpression()).getRightExpression());

		System.out.println("**********sql");
		System.out.println(select.getSQL());

		table.setName("zhuzhen");

		System.out.println(select.getSQL());
		// HashMap<String, String> map = new HashMap<String, String>();
		// Utils.exprToMap(select.getWhere(), map);
		// System.out.println(map);
	}

	public void testsdfgsdfg() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "select name, dff from tabbbb as table where id = name or aaa = dddd";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());

		// select
		AST selectAst = rootAst.getFirstChild();
		System.out.println(selectAst);
		System.out.println(selectAst.getFirstChild());
		System.out.println(selectAst.getFirstChild().getNextSibling());

		// // from
		AST fromAst = selectAst.getNextSibling();
		System.out.println(fromAst);
		// // AST asAst = fromAst.getFirstChild();
		// // System.out.println(asAst);
		// // System.out.println(asAst.getNumberOfChildren());
		// // System.out.println(asAst.getFirstChild());
		// // System.out.println(asAst.getFirstChild().getNextSibling());
		//
		// // where
		AST whereAst = fromAst.getNextSibling();
		System.out.println(whereAst);
		System.out.println(whereAst.getNumberOfChildren());
		System.out.println(whereAst.getFirstChild());
		System.out.println(whereAst.getFirstChild().getFirstChild());
		System.out.println(whereAst.getFirstChild().getFirstChild().getNextSibling());
		System.out.println(whereAst.getFirstChild().getNextSibling());
	}

	public void testInsert1() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "insert into table (id, name, age) values (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());
		System.out.println(rootAst.toStringTree());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		System.out.println(((Insert) sqlTreeParser.getStatement()).getSQL());

	}

	public void testDelete1() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "delete from table where id = 1 and name = 'zhuzhen'";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());
		System.out.println(rootAst.toStringTree());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		System.out.println(((Delete) sqlTreeParser.getStatement()).getSQL());

	}

	public void testUpdate1() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "update table set id = 1 , name = 'zhuzhen', aaaa = aaa where id = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());
		System.out.println(rootAst.toStringTree());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		System.out.println(((Update) sqlTreeParser.getStatement()).getSQL());

	}

}
