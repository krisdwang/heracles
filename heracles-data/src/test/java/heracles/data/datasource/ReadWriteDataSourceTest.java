package heracles.data.datasource;

import heracles.data.common.holder.StrategyHolder;
import heracles.data.datasource.strategy.RoundRobinLoadBalanceStrategy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ReadWriteDataSourceTest {

	@Test
	public void test3() {
		StrategyHolder.removeRepositoryShardingStrategy();
		StrategyHolder.clearDataSourceKey();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("r1", 10);
		map.put("r2", 20);
		map.put("r3", 10);
		RoundRobinLoadBalanceStrategy roundRobinLoadBalanceStrategy = new RoundRobinLoadBalanceStrategy(map);
		ReadWriteDataSourceKey readWriteDataSourceKey = new ReadWriteDataSourceKey();
		readWriteDataSourceKey.setStrategy(roundRobinLoadBalanceStrategy);
		readWriteDataSourceKey.setWriteKey("w");
		Map<String, String> m = new HashMap<String, String>();
		m.put("r1", "r1");
		m.put("r2", "r2");
		m.put("r3", "r3");
		readWriteDataSourceKey.setReadDateSources(m);

		ReadWriteDataSource readWriteDataSource = new ReadWriteDataSource();
		readWriteDataSource.setDataSourceKey(readWriteDataSourceKey);
		Assert.assertNotNull(readWriteDataSource.determineCurrentLookupKey());

		readWriteDataSource.putKey("r1", "r1");
		readWriteDataSource.putKey("r2", "r1");
		readWriteDataSource.putKey("r3", "r1");
		Assert.assertEquals("r1", readWriteDataSource.determineCurrentLookupKey());

		Assert.assertEquals(3, readWriteDataSource.getReadKeys().size());
		Assert.assertEquals("w", readWriteDataSource.getWriteKey());

		Assert.assertEquals(3, readWriteDataSource.getMarkDownKeys().size());
		readWriteDataSource.removeKey("r1");
		Assert.assertEquals(2, readWriteDataSource.getMarkDownKeys().size());
		readWriteDataSource.removeKey("r2");
		Assert.assertEquals(1, readWriteDataSource.getMarkDownKeys().size());
		readWriteDataSource.removeKey("r3");
		Assert.assertEquals(0, readWriteDataSource.getMarkDownKeys().size());

		readWriteDataSource.putKey("r1", "");

	}

	@Test(expected = RuntimeException.class)
	public void test4() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("r1", 10);
		map.put("r2", 20);
		map.put("r3", 10);
		RoundRobinLoadBalanceStrategy roundRobinLoadBalanceStrategy = new RoundRobinLoadBalanceStrategy(map);
		ReadWriteDataSourceKey readWriteDataSourceKey = new ReadWriteDataSourceKey();
		readWriteDataSourceKey.setStrategy(roundRobinLoadBalanceStrategy);
		readWriteDataSourceKey.setWriteKey("w");
		Map<String, String> m = new HashMap<String, String>();
		m.put("r1", "r1");
		m.put("r2", "r2");
		m.put("r3", "r3");
		readWriteDataSourceKey.setReadDateSources(m);

		ReadWriteDataSource readWriteDataSource = new ReadWriteDataSource();
		readWriteDataSource.setDataSourceKey(readWriteDataSourceKey);

		readWriteDataSource.putKey("r1", "");
		readWriteDataSource.putKey("r2", "");
		readWriteDataSource.putKey("r3", "");

		readWriteDataSource.determineCurrentLookupKey();
	}
}
