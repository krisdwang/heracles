package heracles.data.cache.resource.model;

import heracles.data.cache.config.model.CacheType;
import heracles.data.cache.config.model.LoadBalanceType;
import heracles.data.cache.config.model.ShardingType;

import java.util.List;

import lombok.Data;

@Data
public class CacheModel {

	private String name;
	
	private CacheType type;

	private boolean isProxy;
	
	private LoadBalanceType loadBalance;
	
	private ShardingType sharding;
	
	private List<CacheNodeModel> nodes;

}
