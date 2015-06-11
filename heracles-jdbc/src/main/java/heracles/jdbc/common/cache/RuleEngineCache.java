package heracles.jdbc.common.cache;

import heracles.jdbc.rule.RuleEngine;

import java.util.Collections;
import java.util.Map;

public class RuleEngineCache {

	private static final Map<String, RuleEngine> TB_RULE_ENGINE_CACHE = Collections
			.synchronizedMap(new LRUCache<String, RuleEngine>());
	private static final Map<String, RuleEngine> DB_RULE_ENGINE_CACHE = Collections
			.synchronizedMap(new LRUCache<String, RuleEngine>());

	public static RuleEngine getTbRuleEngine(String tbName) {
		return TB_RULE_ENGINE_CACHE.get(tbName);
	}

	public static void setTbRuleEngine(String tbName, RuleEngine ruleEngine) {
		TB_RULE_ENGINE_CACHE.put(tbName, ruleEngine);
	}

	public static RuleEngine getDbRuleEngine(String tbName) {
		return DB_RULE_ENGINE_CACHE.get(tbName);
	}

	public static void setDbRuleEngine(String tbName, RuleEngine ruleEngine) {
		DB_RULE_ENGINE_CACHE.put(tbName, ruleEngine);
	}
}
