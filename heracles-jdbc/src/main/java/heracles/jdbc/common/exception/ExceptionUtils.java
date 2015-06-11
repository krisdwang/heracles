package heracles.jdbc.common.exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.util.Assert;

public class ExceptionUtils {

	public static StackTraceElement SPLIT_BEGIN = new StackTraceElement(
			"---------- matrix exceptions begin ----------", StringUtils.EMPTY, StringUtils.EMPTY, 0);
	public static StackTraceElement SPLIT_END = new StackTraceElement("---------- matrix exceptions end ----------",
			StringUtils.EMPTY, StringUtils.EMPTY, 0);
	public static StackTraceElement SPAN = new StackTraceElement("---------- exception ----------", StringUtils.EMPTY,
			StringUtils.EMPTY, 0);

	public static void throwSQLExceptions(Logger logger, List<SQLException> sqlExceptions) throws SQLException {
		if (CollectionUtils.isNotEmpty(sqlExceptions)) {
			for (SQLException sqlException : sqlExceptions) {
				logger.error(sqlException.getMessage(), sqlException);
			}
			throw mergeException(sqlExceptions);
		}
	}

	public static List<SQLException> appendException(List<SQLException> sqlExceptions, SQLException sqlException) {
		if (sqlExceptions == null) {
			sqlExceptions = new LinkedList<SQLException>();
		}
		sqlExceptions.add(sqlException);
		return sqlExceptions;

	}

	public static SQLException mergeException(List<SQLException> sqlExceptions) {
		Assert.notEmpty(sqlExceptions);

		List<StackTraceElement> stackTraceElements = new ArrayList<StackTraceElement>();
		StringBuilder sb = new StringBuilder();
		stackTraceElements.add(SPLIT_BEGIN);
		for (SQLException sqlException : sqlExceptions) {
			if (ArrayUtils.isNotEmpty(sqlException.getStackTrace())) {
				stackTraceElements.add(SPAN);
				for (StackTraceElement stackTraceElement : sqlException.getStackTrace()) {
					stackTraceElements.add(stackTraceElement);
				}
			}
			if (StringUtils.isNotBlank(sqlException.getMessage())) {
				sb.append(sqlException.getMessage() + ",");
			}
		}
		stackTraceElements.add(SPLIT_END);

		String msg = sb.toString();
		SQLException sqlException = new SQLException(msg.substring(0, msg.length() - 1));
		sqlException.setStackTrace(stackTraceElements.toArray(new StackTraceElement[stackTraceElements.size()]));
		return sqlException;
	}

	public static void printSQLException(Logger logger, String message, List<SQLException> sqlExceptions) {
		if (CollectionUtils.isNotEmpty(sqlExceptions)) {
			for (SQLException sqlException : sqlExceptions) {
				logger.error(message, sqlException);
			}
			sqlExceptions.clear();
		}
	}
}
