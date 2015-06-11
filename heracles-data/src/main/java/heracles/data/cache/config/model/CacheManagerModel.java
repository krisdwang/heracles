package heracles.data.cache.config.model;

import java.util.List;

import org.springframework.beans.factory.support.ManagedList;

import lombok.Data;

@Data
public class CacheManagerModel {

	private String name;
	
	private List<CacheClusterModel> cacheClusterModels;
	
	private ManagedList<Object> springBeans;
	
	private String keyPrefix;
	
	private Long expiration;
	
	private SerializerConfigModel serializerConfigModel;
}
