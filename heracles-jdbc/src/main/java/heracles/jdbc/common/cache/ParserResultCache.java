package heracles.jdbc.common.cache;

import heracles.jdbc.parser.ParserResult;

import java.util.Collections;
import java.util.Map;

public class ParserResultCache {

	private static final Map<String, ParserResult> PARSER_RESULT_CACHE = Collections
			.synchronizedMap(new LRUCache<String, ParserResult>());

	public static ParserResult getParserResult(String sql) {
		ParserResult parserResult = PARSER_RESULT_CACHE.get(sql);
		if (parserResult != null) {
			return parserResult;
		}
		return null;
	}

	public static void setParserResult(String sql, ParserResult parserResult) {
		PARSER_RESULT_CACHE.put(sql, parserResult);
	}
}
