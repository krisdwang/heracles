package heracles.data.common.strategy.repository;

import heracles.data.common.annotation.Strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Strategy("vlash")
public class VlashPermissionRepositoryShardingStrategy extends RepositoryShardingStrategy {
	private final Logger logger = LoggerFactory.getLogger(VlashPermissionRepositoryShardingStrategy.class);

	@Override
	public String getReadWriteDataSource(Object obj) {
		Integer value = (Integer) obj;
		if (value != null && value > 0 && value <= 200) {
			logger.debug("sharding datasource switch to dataSource2");
			return "rwds1";
		}
		else if (value != null && value > 200) {
			logger.debug("sharding datasource switch to dataSource3");
			return "rwds2";
		}
		return null;
	}
}
