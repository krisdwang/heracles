package heracles.jdbc.rule;

import groovy.lang.GroovyClassLoader;
import heracles.jdbc.common.cache.RuleEngineCache;
import heracles.jdbc.matrix.model.RuleListModel;
import heracles.jdbc.matrix.model.RuleModel;
import heracles.jdbc.rule.common.Utils;
import heracles.jdbc.rule.exception.ShardingRuleException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class RuleEngineFactory {
	private Map<String, ShardingRule> rulePool;

	public RuleEngineFactory(Map<String, ShardingRule> rulePool) {
		this.rulePool = rulePool;
	}

	public RuleEngineFactory(RuleListModel ruleList) {
		Map<String, ShardingRule> rulePool = new HashMap<String, ShardingRule>();
		if (null != ruleList && CollectionUtils.isNotEmpty(ruleList.getRuleList())) {
			for (RuleModel rule : ruleList.getRuleList()) {
				String tbNameStr = rule.getTableNames();
				if (StringUtils.isNotBlank(tbNameStr)) {
					String[] tbNames = tbNameStr.split(",");
					if (ArrayUtils.isNotEmpty(tbNames)) {
						for (String tbName : tbNames) {
							if (null != rulePool.get(tbName)) {
								continue;
							}
							ItemRule itemRule = new ItemRule();
							itemRule.setDbIndexes(rule.getGroupIndex());
							itemRule.setDbRule(rule.getGroupShardRule());
							itemRule.setTbSuffixes(rule.getTableSuffix());
							itemRule.setTbRule(rule.getTableShardRule());

							ShardingRule shardingRule = new ShardingRule();
							shardingRule.setTbName(tbName);
							shardingRule.addItemRule(itemRule);

							rulePool.put(tbName, shardingRule);
						}
					}
				}
			}
		}
		this.rulePool = rulePool;
	}

	public Map<String, ShardingRule> getRulePool() {
		return rulePool;
	}

	public void setRulePool(Map<String, ShardingRule> rulePool) {
		this.rulePool = rulePool;
	}

	public RuleEngine createDbRuleEngine(String tbName) {
		RuleEngine ruleEngine = RuleEngineCache.getDbRuleEngine(tbName);
		if (ruleEngine != null) {
			return ruleEngine;
		}

		// FIXME Anders 需要处理多个itemrule
		ShardingRule shardingRule = rulePool.get(tbName);
		if (shardingRule == null) {
			shardingRule = rulePool.get("*");
		}
		ItemRule itemRule = shardingRule.getItemRules().get(0);
		String script = null;
		String[] dbIndexes = null;
		if (StringUtils.isNotBlank(itemRule.getDbIndexes())) {
			script = Utils.getScriptFromExpr(itemRule.getDbRule());
			dbIndexes = StringUtils.split(itemRule.getDbIndexes(), ',');
		} else {
			script = itemRule.getDbRule();
		}

		String shardingKey = Utils.getShardingKey(script);
		script = Utils.replaceShardingKey(script);

		ruleEngine = createRuleEngine(script, dbIndexes, shardingKey);
		RuleEngineCache.setDbRuleEngine(tbName, ruleEngine);
		return ruleEngine;
	}

	public Map<String, RuleEngine> createDbRuleEngines(Set<String> tbNames) {
		Assert.notEmpty(tbNames);

		Map<String, RuleEngine> tb2ruleEngine = new HashMap<String, RuleEngine>();
		for (String tbName : tbNames) {
			tb2ruleEngine.put(tbName, createDbRuleEngine(tbName));
		}

		return tb2ruleEngine;
	}

	public Map<String, RuleEngine> createTbRuleEngines(Set<String> tbNames) {
		Assert.notEmpty(tbNames);

		Map<String, RuleEngine> tb2ruleEngine = new HashMap<String, RuleEngine>();
		for (String tbName : tbNames) {
			tb2ruleEngine.put(tbName, createTbRuleEngine(tbName));
		}

		return tb2ruleEngine;
	}

	public RuleEngine createTbRuleEngine(String tbName) {
		RuleEngine ruleEngine = RuleEngineCache.getTbRuleEngine(tbName);
		if (ruleEngine != null) {
			return ruleEngine;
		}

		// FIXME Anders 需要处理多个itemrule
		ShardingRule shardingRule = rulePool.get(tbName);
		if (shardingRule == null) {
			shardingRule = rulePool.get("*");
		}
		ItemRule itemRule = shardingRule.getItemRules().get(0);
		String script = null;
		String[] tbIndexes = null;
		if (StringUtils.isNotEmpty(itemRule.getTbSuffixes())) {
			script = Utils.getScriptFromExpr(itemRule.getTbRule());
			tbIndexes = StringUtils.split(itemRule.getTbSuffixes(), ',');
			for (int i = 0; i < tbIndexes.length; i++) {
				tbIndexes[i] = tbName + tbIndexes[i];
			}
		} else if (itemRule.getTbSuffixes().equals(" ")) {
			tbIndexes = new String[] { tbName };
		} else {
			script = itemRule.getTbRule();
		}

		String shardingKey = Utils.getShardingKey(script);
		script = Utils.replaceShardingKey(script);

		ruleEngine = createRuleEngine(script, tbIndexes, shardingKey);
		RuleEngineCache.setTbRuleEngine(tbName, ruleEngine);
		return ruleEngine;
	}

	public boolean isSharding(Set<String> tbNames) {
		Assert.notEmpty(tbNames);

		if (MapUtils.isEmpty(rulePool)) {
			return false;
		}

		for (String tbName : tbNames) {
			ShardingRule shardingRule = rulePool.get(tbName);
			if (shardingRule == null) {
				shardingRule = rulePool.get("*");
			}
			if (CollectionUtils.isNotEmpty(shardingRule.getItemRules())) {
				return true;
			}
		}

		return false;
	}

	private RuleEngine createRuleEngine(String script, String[] indexes, String shardingKey) {
		ClassLoader cl = null;
		GroovyClassLoader gcl = null;
		Class<?> groovyClass = null;
		try {
			cl = RuleEngineFactory.class.getClassLoader();
			gcl = new GroovyClassLoader(cl);
			groovyClass = gcl.parseClass(Utils.getGroovyRuleEngine(script));
			// GroovyObject ruleEngine = (GroovyObject) groovyClass.newInstance();
			// ruleEngine.invokeMethod("eval", 18L);
			RuleEngine ruleEngine = (RuleEngine) groovyClass.newInstance();
			ruleEngine.setIndexes(indexes);
			ruleEngine.setShardingKey(shardingKey);
			return ruleEngine;
		} catch (InstantiationException e) {
			throw new ShardingRuleException(e);
		} catch (IllegalAccessException e) {
			throw new ShardingRuleException(e);
		} finally {
			if (gcl != null) {
				try {
					gcl.close();
				} catch (IOException e) {
					throw new ShardingRuleException(e);
				}
			}
		}
	}
}
