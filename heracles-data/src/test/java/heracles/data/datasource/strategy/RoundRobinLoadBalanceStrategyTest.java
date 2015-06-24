package heracles.data.datasource.strategy;

import heracles.data.datasource.strategy.RoundRobinLoadBalanceStrategy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class RoundRobinLoadBalanceStrategyTest {

	@Test(expected = IllegalArgumentException.class)
	public void test1() {
		new RoundRobinLoadBalanceStrategy(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test2() {
		new RoundRobinLoadBalanceStrategy(new HashMap<String, Integer>());
	}

	@Test
	public void test3() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 2);
		map.put("b", 3);
		map.put("c", 2);
		RoundRobinLoadBalanceStrategy roundRobinLoadBalanceStrategy = new RoundRobinLoadBalanceStrategy(map);
		
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		
		Assert.assertEquals(7, roundRobinLoadBalanceStrategy.getTargets().size());
	}

	@Test
	public void test4() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 2);
		map.put("b", 4);
		map.put("c", 2);
		RoundRobinLoadBalanceStrategy roundRobinLoadBalanceStrategy = new RoundRobinLoadBalanceStrategy(map);
		
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		
		Assert.assertEquals(4, roundRobinLoadBalanceStrategy.getTargets().size());
	}

	@Test
	public void test5() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", -1);
		map.put("b", -1);
		map.put("c", -1);
		RoundRobinLoadBalanceStrategy roundRobinLoadBalanceStrategy = new RoundRobinLoadBalanceStrategy(map);
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals(3, roundRobinLoadBalanceStrategy.getTargets().size());
	}

	@Test
	public void test6() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		RoundRobinLoadBalanceStrategy roundRobinLoadBalanceStrategy = new RoundRobinLoadBalanceStrategy(map);
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals(6, roundRobinLoadBalanceStrategy.getTargets().size());
		roundRobinLoadBalanceStrategy.removeTarget("b");
		Assert.assertEquals(4, roundRobinLoadBalanceStrategy.getTargets().size());
		roundRobinLoadBalanceStrategy.removeTarget("c");
		Assert.assertEquals(1, roundRobinLoadBalanceStrategy.getTargets().size());
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		roundRobinLoadBalanceStrategy.recoverTarget("c");
		Assert.assertEquals(4, roundRobinLoadBalanceStrategy.getTargets().size());
		roundRobinLoadBalanceStrategy.recoverTarget("b");
		Assert.assertEquals(6, roundRobinLoadBalanceStrategy.getTargets().size());
		Assert.assertEquals("a", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("b", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
		Assert.assertEquals("c", roundRobinLoadBalanceStrategy.elect());
	}
}
