package heracles.jdbc.rule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class RuleEngineTest {
	@Test
	public void test1() throws InstantiationException, IllegalAccessException {
		ItemRule itemRule = new ItemRule();
		itemRule.setDbIndexes("ds0,ds1,ds2,ds3");
		itemRule.setDbRule("(#ID#.intValue() % 16).intdiv(4)");
		itemRule.setTbSuffixes("_00,_01,_02,_03");
		itemRule.setTbRule("#ID#.intValue() % 4");

		ShardingRule shardingRule = new ShardingRule();
		shardingRule.setTbName("zhuzhen");
		shardingRule.addItemRule(itemRule);

		Map<String, ShardingRule> map = new HashMap<String, ShardingRule>();
		map.put("zhuzhen", shardingRule);

		RuleEngine ruleEngine = new RuleEngineFactory(map).createDbRuleEngine("zhuzhen");
		Assert.assertEquals("ds3", ruleEngine.eval(12L));
		Assert.assertEquals("ID", ruleEngine.getShardingKey());

		ruleEngine = new RuleEngineFactory(map).createTbRuleEngine("zhuzhen");
		Assert.assertEquals("zhuzhen_00", ruleEngine.eval(12L));
		Assert.assertEquals("ID", ruleEngine.getShardingKey());
	}
}
