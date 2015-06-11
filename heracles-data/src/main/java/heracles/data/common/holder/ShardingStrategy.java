package heracles.data.common.holder;

import lombok.Getter;
import lombok.Setter;


public class ShardingStrategy {
	/**
	 * sharding key
	 */
	@Getter
	@Setter
	private String repositoryShardingKey;
	
	/**
	 * datasource key
	 */
	@Getter
	@Setter
	private String dataSourceKey;	
}
