package heracles.jdbc.parser.test1;

import heracles.jdbc.parser.SQLLexer;
import heracles.jdbc.parser.SQLParser;
import heracles.jdbc.parser.SQLTreeParser;
import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.statement.update.Update;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

public class UpdateTest {

	@Test
	public void testUpdate1() throws RecognitionException, antlr.RecognitionException, TokenStreamException {
		String sql = "update cust set id = 1 , name = 'tom' where id = 1";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();

		AST rootAst = parser.getAST();

		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(rootAst);

		Update update = (Update) sqlTreeParser.getStatement();

		Assert.assertEquals("cust", update.getFromItem().toStr());
		Assert.assertEquals(1, sqlTreeParser.getParamValueMap().size());
		Assert.assertEquals("1", sqlTreeParser.getParamValueMap().get("id").get(0));
		Assert.assertEquals(StatementType.UPDATE, update.getType());

	}
}
