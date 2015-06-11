package heracles.data.common.strategy.repository;

/**
 * 不分库策略
 * 
 * @author kriswang
 * 
 */
public final class NoRepositoryShardingStrategy extends RepositoryShardingStrategy {

	private static final NoRepositoryShardingStrategy INSTANCE = new NoRepositoryShardingStrategy();

	private NoRepositoryShardingStrategy() {
	}

	@Override
	public String getReadWriteDataSource(Object obj) {
//		return getDefaultDataSource();
		return null;
	}

	public static NoRepositoryShardingStrategy getInstance() {
		return INSTANCE;
	}
}
