package heracles.jdbc.parser.test1;

import heracles.jdbc.parser.SQLLexer;
import heracles.jdbc.parser.SQLParser;
import heracles.jdbc.parser.SQLTokenTypes;
import heracles.jdbc.parser.SQLTreeParser;
import heracles.jdbc.parser.statement.insert.Insert;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class InsertTest {
	@Test
	public void testInsert1() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "insert into cust (id, name) values (1, 'tom')";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);
		Assert.assertEquals(SQLTokenTypes.INSERT_ROOT, rootAst.getType());
		Assert.assertEquals(2, ((Insert) sqlTreeParser.getStatement()).getExpressionList().getExpressionList().size());
		Assert.assertEquals("\'tom\'", ((Insert) sqlTreeParser.getStatement()).getExpressionList().getExpressionList().get(1).toStr());

	}
	
	@Test(expected = Exception.class)
	public void testInsert2() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "insert into cust values (1, 'tom')";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}
	
	@Test(expected = Exception.class)
	public void testInsert3() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "insert into cust values (1, 'tom'), (2, 'tom')";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
	}
}
