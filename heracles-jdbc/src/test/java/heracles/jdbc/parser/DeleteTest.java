package heracles.jdbc.parser;

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
	public void test1() {
		String sql = "delete from table where id = 1 and name=3";
		try {
			SQLLexer lexer = new SQLLexer(new StringReader(sql));
			SQLParser parser = new SQLParser(lexer);
			parser.statement();

			AST rootAst = parser.getAST();
			System.out.println(rootAst.toStringTree());

			Assert.assertEquals(SQLTokenTypes.DELETE_ROOT, rootAst.getType());

			SQLTreeParser sqlTreeParser = new SQLTreeParser();
			sqlTreeParser.statement(rootAst);

			Delete delete = (Delete) sqlTreeParser.getStatement();
			System.out.println(delete.getSQL());
			// 检查delete基本语法
			Assert.assertEquals("DELETE FROM table WHERE id = 1 AND name = 3", delete.getSQL());
			Assert.assertEquals("table", delete.getFromItem().toStr());
			// 提供条件列表
			System.out.println(delete.getWhere().getClass());
			Assert.assertEquals(StatementType.DELETE, delete.getType());
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}
	}

	@Test(expected = Exception.class)
	public void test2() throws RecognitionException, TokenStreamException {
		String sql = "delete from table";
		SQLLexer lexer = new SQLLexer(new StringReader(sql));
		SQLParser parser = new SQLParser(lexer);
		parser.statement();
		SQLTreeParser sqlTreeParser = new SQLTreeParser();
		sqlTreeParser.statement(parser.getAST());

	}
}
