package heracles.data.cache.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class RedisTwemproxyCacheManager extends AbstractCacheManager implements CacheManager {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(RedisTwemproxyCacheManager.class);
	
	@SuppressWarnings("rawtypes")//
	private final RedisTemplate template;

	private boolean usePrefix = false;
	private RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();
	@SuppressWarnings("unused")
	private boolean loadRemoteCachesOnStartup = false;
	private boolean dynamic = true;

	// 0 - never expire
	private long defaultExpiration = 0;
	private Map<String, Long> expires = null;
	
	private String keyPrefix;
	
	@SuppressWarnings("rawtypes")
	public RedisTwemproxyCacheManager(RedisTemplate template) {
		this(template, Collections.<String> emptyList());
	}
	
	@SuppressWarnings("rawtypes")
	public RedisTwemproxyCacheManager(RedisTemplate template, Collection<String> cacheNames) {
		this.template = template;
		setCacheNames(cacheNames);
	}
	
	@SuppressWarnings("rawtypes")
	public RedisTwemproxyCacheManager(RedisTemplate template, Collection<String> cacheNames, boolean usePrefix,
			String keyPrefix, long defaultExpiration) {
		this.template = template;
		this.usePrefix = usePrefix;
		this.keyPrefix = keyPrefix;
		this.defaultExpiration = defaultExpiration;
		setCacheNames(cacheNames);
	}
	
	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null && this.dynamic) {
			return createAndAddCache(name);
		}

		return cache;
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {
		Assert.notNull(this.template, "A redis template is required in order to interact with data store");
		return Collections.emptyList();
	}
	
	@Override
	public void afterPropertiesSet() {
		// do not clear cache map
	}

	public void setCacheNames(Collection<String> cacheNames) {
		if (!CollectionUtils.isEmpty(cacheNames)) {
			for (String cacheName : cacheNames) {
				createAndAddCache(cacheName);
			}
			this.dynamic = false;
		}
	}
	
	private Cache createAndAddCache(String cacheName) {
		addCache(createCache(cacheName));
		return super.getCache(cacheName);
	}
	
	@SuppressWarnings("unchecked")
	private RedisTwemproxyCache createCache(String cacheName) {
		long expiration = computeExpiration(cacheName);
		return new RedisTwemproxyCache(cacheName, (usePrefix ? cachePrefix.prefix(keyPrefix) : null), template,
				expiration);
	}
	
	private long computeExpiration(String name) {
		Long expiration = null;
		if (expires != null) {
			expiration = expires.get(name);
		}
		return (expiration != null ? expiration.longValue() : defaultExpiration);
	}
	
	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}
	
	public void setCachePrefix(RedisCachePrefix cachePrefix) {
		this.cachePrefix = cachePrefix;
	}
	
	public void setDefaultExpiration(long defaultExpireTime) {
		this.defaultExpiration = defaultExpireTime;
	}
	
	public void setExpires(Map<String, Long> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Long>(expires) : null);
	}
	
	public void setLoadRemoteCachesOnStartup(boolean loadRemoteCachesOnStartup) {
		this.loadRemoteCachesOnStartup = loadRemoteCachesOnStartup;
	}
	

}
