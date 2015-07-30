package heracles.jdbc.common;

import heracles.jdbc.common.exception.ExceptionUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtilsTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtilsTest.class);

	@Test
	public void test1() throws SQLException {
		SQLException sqlException1 = new SQLException("1");
		SQLException sqlException2 = new SQLException("2");
		List<SQLException> sqlExceptions = new ArrayList<SQLException>();
		sqlExceptions.add(sqlException1);
		sqlExceptions.add(sqlException2);

		ExceptionUtils.printSQLException(LOGGER, "sdfasdfa", sqlExceptions);
	}

	@Test
	public void test2() {
		SQLException sqlException1 = new SQLException("1");
		SQLException sqlException2 = new SQLException("2");
		List<SQLException> sqlExceptions = new ArrayList<SQLException>();
		sqlExceptions.add(sqlException1);
		sqlExceptions.add(sqlException2);

		try {
			ExceptionUtils.throwSQLExceptions(LOGGER, sqlExceptions);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
