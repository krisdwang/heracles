package heracles.data.common.strategy.repository;

import heracles.data.common.util.Constants;

/**
 * 分库策略抽象类
 * 
 * @author kriswang
 * 
 */
public abstract class RepositoryShardingStrategy implements Constants {
	
	public abstract String getReadWriteDataSource(Object obj);

}
