package heracles.jdbc.parser.common;

import org.apache.commons.lang3.StringUtils;

public interface Constants {
	static final String AS = "AS";
	static final String SPACE = " ";
	static final String EMPTY = StringUtils.EMPTY;
	static final String AND = "AND";
	static final String OR = "OR";
	static final String EQUALS_TO = "=";
	static final String IN = "IN";
	static final String STAR = "*";
	static final String DOT = ".";
	static final String COMMA = ",";
	static final String QUESTION_MARK = "?";
	static final String WHERE = "WHERE";
	static final String JOIN = "JOIN";
	static final String LEFT_JOIN = "LEFT JOIN";
	static final String RIGHT_JOIN = "RIGHT JOIN";
	static final String ORDER_BY = "ORDER BY";
	static final String ASC = "ASC";
	static final String DESC = "DESC";
	static final String LIMIT = "LIMIT";
	static final String MAX = "MAX";
	static final String MIN = "MIN";
	static final String AVG = "AVG";
	static final String SUM = "SUM";
	static final String COUNT = "COUNT";
	static final String OPEN_COMMENT = "/*";
	static final String CLOSE_COMMENT = "*/";

	static final String AS_WS = AS + SPACE;
	static final String COMMA_WS = COMMA + SPACE;
	static final String ASC_WS = SPACE + ASC;
	static final String DESC_WS = SPACE + DESC;
	static final String OPEN_COMMENT_WS = OPEN_COMMENT + SPACE;
	static final String CLOSE_COMMENT_WS = SPACE + CLOSE_COMMENT;

}
