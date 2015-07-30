package heracles.jdbc.parser;

import heracles.jdbc.parser.common.StatementType;

import org.junit.Test;

public class ParserTest {
	@Test
	public void test1() {
		String sql = "select * from table where id in (1,2,3) and name=3";
		Parser parser = new Parser(sql, null);
		// boolean b = parser.parse(sql);
		// Assert.assertFalse(b);

		sql = "select * from table where id in (1,2,3) and name=3";
		parser = new Parser(sql, null);
		// b = parser.parse();
		// Assert.assertTrue(b);

		StatementType type = parser.getParserResult().getType();
		switch (type) {
		case DELETE:

			break;
		default:
			break;
		}
	}
}
