package heracles.data.common.strategy.repository;

import heracles.data.common.annotation.Strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cust repository sharding strategy
 * 
 * @author Anders
 * 
 */
@Strategy("depart")
public class DepartRepositoryShardingStrategy extends RepositoryShardingStrategy {

	private final Logger logger = LoggerFactory.getLogger(DepartRepositoryShardingStrategy.class);

	@Override
	public String getReadWriteDataSource(Object obj) {
		Long value = (Long) obj;
		if (value != null && value > 100 && value <= 200) {
			logger.debug("sharding datasource switch to dataSource2");
			return "rwds2";
		} else if (value != null && value > 200) {
			logger.debug("sharding datasource switch to dataSource3");
			return "rwds1";
		}

		return null;
	}
}
