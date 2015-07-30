package heracles.jdbc.parser;

import heracles.jdbc.parser.common.Utils;
import heracles.jdbc.parser.exception.SQLParserException;
import heracles.jdbc.parser.statement.delete.Delete;
import heracles.jdbc.parser.statement.insert.Insert;
import heracles.jdbc.parser.statement.select.Select;
import heracles.jdbc.parser.statement.select.from.Table;
import heracles.jdbc.parser.statement.select.select.SelectExpression;
import heracles.jdbc.parser.statement.select.select.SelectItem;
import heracles.jdbc.parser.statement.update.Update;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class UpdateTest {

	@Test
	public void test1() throws RecognitionException, TokenStreamException {
		String sql = "update tb set id = 1, name = 'zhuzhen' where id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());
		Assert.assertEquals(SQLTokenTypes.UPDATE_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Update update = (Update) sqlTreeParser.getStatement();
		Assert.assertEquals("UPDATE tb SET id = 1, name = 'zhuzhen' WHERE id = 1 OR id IN (1, 2, 3)", update.getSQL());
		Assert.assertEquals("id = 1, name = 'zhuzhen'", update.getSetList().toStr());
		Assert.assertEquals("id = 1 OR id IN 1, 2, 3", update.getWhere().toStr());
		Assert.assertEquals("tb", update.getFromItem().toStr());
	}

	@Test(expected = SQLParserException.class)
	public void test2() throws RecognitionException, TokenStreamException {
		String sql = "update  set id = 1, name = 'zhuzhen' where id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test3() throws RecognitionException, TokenStreamException {
		String sql = "update tt  id = 1, name = 'zhuzhen' where id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test4() throws RecognitionException, TokenStreamException {
		String sql = "update tt set id = 1 name = 'zhuzhen' where id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test5() throws RecognitionException, TokenStreamException {
		String sql = "update tt set 1 = id, name = 'zhuzhen' where id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test6() throws RecognitionException, TokenStreamException {
		String sql = "update tt set id=1,  'zhuzhen'=zhuzhen where id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test7() throws RecognitionException, TokenStreamException {
		String sql = "update tt set id=1,  'zhuzhen'=zhuzhen  id =1  or id in (1,2,3)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test8() throws RecognitionException, TokenStreamException {
		String sql = "update tt set id=1,  'zhuzhen'=zhuzhen where  ";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test(expected = SQLParserException.class)
	public void test9() throws RecognitionException, TokenStreamException {
		String sql = "update tt set id=1,2,3 where  id =1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	@Test
	public void test10() throws RecognitionException, TokenStreamException {
		String sql = "update tt set id=1 where  id =1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}

	public void test4sdafas() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "select name, dff from tabbbb as table where id = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.getType());

		// select
		AST selectAst = rootAst.getFirstChild();
		System.out.println(selectAst);

		// // from
		// AST fromAst = selectAst.getNextSibling();
		// System.out.println(fromAst);
		// // AST asAst = fromAst.getFirstChild();
		// // System.out.println(asAst);
		// // System.out.println(asAst.getNumberOfChildren());
		// // System.out.println(asAst.getFirstChild());
		// // System.out.println(asAst.getFirstChild().getNextSibling());
		//
		// // where
		// AST whereAst = fromAst.getNextSibling();
		// System.out.println(whereAst);
		// // System.out.println(whereAst.getNumberOfChildren());
		// System.out.println(whereAst.getFirstChild());
		// System.out.println(whereAst.getFirstChild().getNextSibling());
		// System.out.println(whereAst.getFirstChild().getNextSibling().getNextSibling());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Select select = sqlTreeParser.getSelect();
		List<SelectItem> selectItems = select.getSelectItems();
		System.out.println(selectItems.size());
		for (SelectItem selectItem : selectItems) {
			SelectExpression selectExpression = (SelectExpression) selectItem;
			System.out.println(selectExpression.getExpression().toStr());
			System.out.println(selectExpression.getAlias().getName());
			System.out.println(selectExpression.getAlias().toStr());
		}
	}

	public void test5asdfasd() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "select zhuzhen as ttt, name, dff f from tabbbb as table where a1 = a2 and a3 = 589 or a5 = 'asfasd'";
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
		// System.out.println(((AndExpression) ((OrExpression) select.getWhere()).getRightExpression()).getLeftExpression());
		// System.out.println(((AndExpression) ((OrExpression) select.getWhere()).getRightExpression()).getRightExpression());

		System.out.println("**********sql");
		System.out.println(select.getSQL());

		table.setName("zhuzhen");

		System.out.println(select.getSQL());
		HashMap<String, String> map = new HashMap<String, String>();
		Utils.exprToMap(select.getWhere(), map);
		System.out.println(map);
	}

	public void test6asdfasd() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
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
