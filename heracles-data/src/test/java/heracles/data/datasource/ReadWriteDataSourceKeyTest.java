package heracles.data.datasource;

import heracles.data.common.holder.StrategyHolder;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" }, inheritLocations = true)
public class ReadWriteDataSourceKeyTest {

	@Resource(name = "readWriteDataSourceKey1")
	private ReadWriteDataSourceKey readWriteDataSourceKey;

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		Assert.assertTrue(readWriteDataSourceKey.hasReadKey());
		Assert.assertEquals("read01", readWriteDataSourceKey.getReadKey("read01"));
		readWriteDataSourceKey.setWriteKey("write");
		Assert.assertEquals("write", readWriteDataSourceKey.getWriteKey());
		//Assert.assertThat(readWriteDataSourceKey.getKey(), CoreMatchers.anyOf(equalTo("read01"), equalTo("read02")));

		readWriteDataSourceKey.setReadKey();
		//Assert.assertThat(readWriteDataSourceKey.getKey(), CoreMatchers.anyOf(equalTo("read01"), equalTo("read02")));
		;
		readWriteDataSourceKey.resetKey();
		//Assert.assertThat(readWriteDataSourceKey.getKey(), CoreMatchers.anyOf(equalTo("read01"), equalTo("read02")));
		readWriteDataSourceKey.setWriteKey();
		Assert.assertEquals("write", readWriteDataSourceKey.getKey());
		Assert.assertTrue(readWriteDataSourceKey.isCurrentWriteKey());

		//readWriteDataSourceKey.recoverDateSourceKey(readWriteDataSourceKey.getKey());

		readWriteDataSourceKey.setKey("read01");
		//Assert.assertEquals("read01", readWriteDataSourceKey.getKey());
		readWriteDataSourceKey.setKey("read02");
		//Assert.assertEquals("read01", readWriteDataSourceKey.getKey());
		readWriteDataSourceKey.setAlwaysReplaceExist(true);
		Assert.assertTrue(readWriteDataSourceKey.isAlwaysReplaceExist());
		readWriteDataSourceKey.setKey("read02");
		Assert.assertEquals("read02", readWriteDataSourceKey.getKey());

		readWriteDataSourceKey.removeDataSourceKey("read02");
		Assert.assertEquals("read01", readWriteDataSourceKey.getKey());
		readWriteDataSourceKey.recoverDateSourceKey("read02");
		readWriteDataSourceKey.removeDataSourceKey("read01");
		Assert.assertEquals("read02", readWriteDataSourceKey.getKey());
		Assert.assertTrue(readWriteDataSourceKey.hasFailedDataSource());
		Assert.assertEquals(1, readWriteDataSourceKey.getFailedDataSourceKeys().size());
		
		StrategyHolder.removeRepositoryShardingStrategy();
		StrategyHolder.clearDataSourceKey();
	}
}
