package heracles.data.datasource.strategy;

import heracles.data.datasource.strategy.RandomLoadBalanceStrategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RandomLoadBalanceStrategyTest {

	@Test(expected = IllegalArgumentException.class)
	public void test1() {
		new RandomLoadBalanceStrategy(new ArrayList<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test2() {
		new RandomLoadBalanceStrategy(null);
	}

	@Test
	public void test3() {
		List<String> targets = new ArrayList<String>();
		targets.add("read01");
		targets.add("read02");
		RandomLoadBalanceStrategy randomLoadBalanceStrategy = new RandomLoadBalanceStrategy(targets);

		randomLoadBalanceStrategy.removeTarget("read01");
		Assert.assertEquals("read02", randomLoadBalanceStrategy.elect());

		randomLoadBalanceStrategy.removeTarget("read02");
		Assert.assertNull(randomLoadBalanceStrategy.elect());

		randomLoadBalanceStrategy.recoverTarget("read01");
		Assert.assertEquals("read01", randomLoadBalanceStrategy.elect());
	}
}
