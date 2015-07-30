package heracles.jdbc.parser.test1;

import heracles.jdbc.parser.SQLLexer;
import heracles.jdbc.parser.SQLParser;
import heracles.jdbc.parser.SQLTokenTypes;
import heracles.jdbc.parser.SQLTreeParser;
import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.statement.delete.Delete;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class DeleteTest {
	@Test
	public void test1() throws RecognitionException, TokenStreamException {
		String sql = "delete from cust where id = 1 and name = 'tom'";
		SQLLexer lexer = new SQLLexer(new StringReader(sql)); 
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());

		Assert.assertEquals(SQLTokenTypes.DELETE_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);

		Delete delete = (Delete) sqlTreeParser.getStatement();

		Assert.assertEquals("cust", delete.getFromItem().toStr());
		Assert.assertEquals(2, sqlTreeParser.getParamValueMap().size());
		Assert.assertEquals("1", sqlTreeParser.getParamValueMap().get("id").get(0));
		Assert.assertEquals(StatementType.DELETE, delete.getType());
	}
	
	@Test
	public void test2() throws RecognitionException, TokenStreamException {
		String sql = "delete from cust where id = 1 or name = 'tom'";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());

		Assert.assertEquals(SQLTokenTypes.DELETE_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);

		Delete delete = (Delete) sqlTreeParser.getStatement();

		Assert.assertEquals("cust", delete.getFromItem().toStr());
		Assert.assertEquals(2, sqlTreeParser.getParamValueMap().size());
		Assert.assertEquals("\'tom\'", sqlTreeParser.getParamValueMap().get("name").get(0));
		Assert.assertEquals(StatementType.DELETE, delete.getType());
	}
	
	@Test
	public void test3() throws RecognitionException, TokenStreamException {
		String sql = "delete from cust where id in (1,2) or name = 'tom'";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());

		Assert.assertEquals(SQLTokenTypes.DELETE_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);

		Delete delete = (Delete) sqlTreeParser.getStatement();

		Assert.assertEquals("cust", delete.getFromItem().toStr());
		Assert.assertEquals(2, sqlTreeParser.getParamValueMap().size());
		Assert.assertEquals("2", sqlTreeParser.getParamValueMap().get("id").get(1));
		Assert.assertEquals(StatementType.DELETE, delete.getType());
	}
	
	@Test
	public void test4() throws RecognitionException, TokenStreamException {
		String sql = "delete from cust where name = 'tom' or id in (1,2)";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();
		System.out.println(rootAst.toStringTree());

		Assert.assertEquals(SQLTokenTypes.DELETE_ROOT, rootAst.getType());

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);

		Delete delete = (Delete) sqlTreeParser.getStatement();

		Assert.assertEquals("cust", delete.getFromItem().toStr());
		Assert.assertEquals(2, sqlTreeParser.getParamValueMap().size());
		Assert.assertEquals("1", sqlTreeParser.getParamValueMap().get("id").get(0));
		Assert.assertEquals(StatementType.DELETE, delete.getType());
	}

	@Test(expected = Exception.class)
	public void test5() throws RecognitionException, TokenStreamException {
		String sql = "delete from cust";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}
	
	@Test(expected = Exception.class)
	public void test6() throws RecognitionException, TokenStreamException {
		String sql = "delete from cust where id = 1 and name = 'tom' ORDER BY id LIMIT 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}
}
