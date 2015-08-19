package heracles.data.common.strategy.repository;

import heracles.data.common.annotation.Strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user repository sharding strategy
 * 
 * @author kriswang
 * 
 */
@Strategy("user")
public class UserRepositoryShardingStrategy extends RepositoryShardingStrategy {

	private final Logger logger = LoggerFactory.getLogger(UserRepositoryShardingStrategy.class);

	@Override
	public String getReadWriteDataSource(Object obj) {
		Long value = (Long) obj;
		if (value != null && value > 100 && value <= 200) {
			logger.debug("sharding datasource switch to dataSource2");
			return "rwds2";
		}
		else if (value != null && value > 200) {
			logger.debug("sharding datasource switch to dataSource3");
			return "rwds3";
		}
		return null;
	}
}
