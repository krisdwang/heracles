package heracles.data.datasource;

import heracles.data.common.holder.StrategyHolder;
import heracles.data.datasource.strategy.RoundRobinLoadBalanceStrategy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;

public class ReadWriteDataSourceTest {

	// @Test
	// public void test1() {
	// ReadWriteDataSource readWriteDataSource = PowerMockito.mock(ReadWriteDataSource.class);
	// PowerMockito.when(readWriteDataSource.determineCurrentLookupKey()).thenReturn("read01");
	//
	// Assert.assertEquals("read01", readWriteDataSource.determineCurrentLookupKey());
	// }

	// @Test
	// public void test2() throws Exception {
	// Connection connection = PowerMockito.mock(Connection.class);
	// DataSource dataSource = PowerMockito.mock(DruidDataSource.class);
	//
	// ReadWriteDataSource readWriteDataSource = PowerMockito.spy(new ReadWriteDataSource());

	// Map<Object, Object> map = new HashMap<Object, Object>();
	// map.put("write", dataSource);
	// map.put("read01", dataSource);
	// map.put("read02", dataSource);
	//
	// readWriteDataSource.setTargetDataSources(map);

	// PowerMockito.when(readWriteDataSource, "determineTargetDataSource").thenReturn(dataSource);

	// ReadWriteDataSourceKey readWriteDataSourceKey = PowerMockito.mock(ReadWriteDataSourceKey.class);
	// PowerMockito.when(readWriteDataSourceKey.getKey()).thenReturn("read01");

	// readWriteDataSource.setDataSourceKey(readWriteDataSourceKey);

	// PowerMockito.when(readWriteDataSource.determineCurrentLookupKey()).thenReturn("read");
	//
	// PowerMockito.when(readWriteDataSource, "determineTargetDataSource").thenReturn(dataSource);

	// PowerMockito.when(readWriteDataSource, "getConnectionFromDataSource", "root", "123").thenReturn(connection);

	// Assert.assertEquals(3, spy.test_private_method(1));
	// PowerMockito.verifyPrivate(spy, Mockito.times(1)).invoke("private_method", 1);
	// }

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

	//@Test(expected = RuntimeException.class)
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

	// @Test(expected = SQLException.class)
	public void test5() throws SQLException {
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
		Map<Object, Object> ds = new HashMap<Object, Object>();
		ds.put("r1", new DruidDataSource());
		ds.put("r2", new DruidDataSource());
		ds.put("r3", new DruidDataSource());
		readWriteDataSource.setTargetDataSources(ds);

		readWriteDataSource.putKey("r1", "");
		readWriteDataSource.putKey("r2", "");
		readWriteDataSource.putKey("r3", "");

		readWriteDataSource.getConnection();
	}
}
