package heracles.data.cache.config.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RedisPoolConfigModel extends PoolConfigModel {

	private String maxTotal;
	
	private String maxIdle;
	
	private String timeBetweenEvictionRunsMillis;
	
	private String minEvictableIdleTimeMillis;
	
	private String maxWaitMillis;
}
