package heracles.data.common.strategy.table;

import heracles.data.common.strategy.table.NoTableShardingStrategy;

import org.junit.Assert;
import org.junit.Test;

public class NoTableShardingStrategyTest {

	@Test
	public void test() {
		NoTableShardingStrategy noTableShardingStrategy = NoTableShardingStrategy.getInstance();
		Assert.assertTrue(noTableShardingStrategy instanceof NoTableShardingStrategy);
		Assert.assertEquals("select", noTableShardingStrategy.getTargetSql("select"));
	}

}
