package heracles.data.cache.spring;

/**
 * @author kriswang
 *
 */
public interface HeraclesCacheConstants {
	
	static final String SPRING_BEAN_NAMESPACE = "http://www.springframework.org/schema/beans";
	static final String SPRING_CACHE_NAMESPACE = "http://www.springframework.org/schema/cache";

	static final String NAME = "name";
	static final String CLUSTER_NAME = "cluster-name";
	static final String TYPE = "type";
	static final String TEMPLATE_NAME = "template-name";
	static final String REDIS = "redis";
	static final String MEMCACHED = "memcached";
	static final String CLAZZ = "class";
	
	static final String KEY_PREFIX = "key-prefix";
	static final String EXPIRATION = "expiration";
	
	static final String KEY_SERIALIZER = "key-serializer";
	static final String VALUE_SERIALIZER = "value-serializer";
	static final String HASH_KEY_SERIALIZER = "hashkey-serializer";
	static final String HASH_VALUE_SERIALIZER = "hashvalue-serializer";
	
	static final String CONNECTION_FACTORY_PROPERTY = "connectionFactory";
	static final String KEY_SERIALIZER_PROPERTY = "keySerializer";
	static final String VALUE_SERIALIZER_PROPERTY = "valueSerializer";
	static final String HASH_KEY_SERIALIZER_PROPERTY = "hashKeySerializer";
	static final String HASH_VALUE_SERIALIZER_PROPERTY = "hashValueSerializer";
	
	
	// redis pool config
	static final String MAX_TOTAL = "maxTotal";
	static final String MAX_IDLE = "maxIdle";
	static final String TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
	static final String MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
	static final String MAX_WAIT_MILLIS = "maxWaitMillis";

	static final String JEDIS_POOL_CONFIG = "jedisPoolConfig";
	static final String JEDIS_POOL_CONFIG_CLAZZ = "redis.clients.jedis.JedisPoolConfig";

	static final String JEDIS_CONNECTION_FACTORY = "jedisConnectionFactory";
	static final String JEDIS_CONNECTION_FACTORY_CLAZZ = "org.springframework.data.redis.connection.jedis.JedisConnectionFactory";

	static final String REDIS_TEMPLATE = "redisTemplate";
	static final String REDIS_TWEMPROXY_TEMPLATE_CLAZZ = "io.doeasy.data.cache.redis.core.RedisTwemproxyTemplate";
	
	static final String REDIS_TWEMPROXY_CACHE_MANAGER = "redisTwemproxyCacheManager";
	static final String REDIS_TWEMPROXY_CACHE_MANAGER_CLAZZ = "io.doeasy.data.cache.redis.RedisTwemproxyCacheManager";
	static final String DEFAULT_EXPIRATION = "defaultExpiration";
	
	static final long DEFAULT_EXPIRATION_VALUE = 0;
	
	static final String CACHE_MANAGER = "cacheManager";
	
	static final String FAILBACK_NO_OP_CACHE = "fallbackToNoOpCache";
	
	static final String COMPOSITE_CACHE_MANAGER = "org.springframework.cache.support.CompositeCacheManager";
	
	// Serializer class
	//static final String SERIALZER_STRING_CLAZZ = "org.springframework.data.redis.serializer.StringRedisSerializer";
	static final String SERIALZER_STRING_CLAZZ = "heracles.data.cache.redis.serializer.StringHeraclesRedisSerializer";
	static final String SERIALZER_JDK_CLAZZ = "org.springframework.data.redis.serializer.JdkSerializationRedisSerializer";
	static final String SERIALZER_XML_CLAZZ = "org.springframework.data.redis.serializer.OxmSerializer";
	static final String SERIALZER_JACKSON_CLAZZ = "org.springframework.data.redis.serializer.JacksonJsonRedisSerializer";
	static final String SERIALZER_JACKSON2_CLAZZ = "org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer";
	static final String SERIALZER_GENERIC2STRING_CLAZZ = "org.springframework.data.redis.serializer.GenericToStringSerializer";
	
	
	static final String USE_PREFIX_PROPERTY = "usePrefix";
	static final String CACHE_PREFIX_PROPERTY = "cachePrefix";
	static final String KEY_PREFIX_PROPERTY = "keyPrefix";
	
	
}
