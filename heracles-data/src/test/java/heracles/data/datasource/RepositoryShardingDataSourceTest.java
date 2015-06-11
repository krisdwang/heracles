package heracles.data.datasource;

import org.junit.Assert;
import org.junit.Test;

public class RepositoryShardingDataSourceTest {

	//@Test(expected = RuntimeException.class)
	public void test1() {
		
		RepositoryShardingDataSource repositoryShardingDataSource = new RepositoryShardingDataSource();
		
		repositoryShardingDataSource.putKey("rwds2");
		Assert.assertEquals(1, repositoryShardingDataSource.getMarkDownKeys().size());
		repositoryShardingDataSource.putKey("rwds1");
		Assert.assertEquals(2, repositoryShardingDataSource.getMarkDownKeys().size());
		repositoryShardingDataSource.removeKey("rwds2");
		Assert.assertEquals(1, repositoryShardingDataSource.getMarkDownKeys().size());
		
		repositoryShardingDataSource.determineCurrentLookupKey();
	}
}
