package heracles.data.cache.config.model;

import heracles.data.cache.resource.model.CacheModel;
import lombok.Data;

@Data
public class CacheClusterModel {

	private String name;
	
	private String clusterName;
	
	private CacheType type;
	
	private String keyPrefix;
	
	private Long expiration;
	
	private String templateName;
	
	private PoolConfigModel poolConfigModel;
	
	private CacheModel cacheModel;
	
	private SerializerConfigModel serializerConfigModel;
	
}
