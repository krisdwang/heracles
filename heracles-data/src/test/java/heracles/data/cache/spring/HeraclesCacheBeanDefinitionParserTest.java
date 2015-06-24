package heracles.data.cache.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import heracles.data.cache.redis.RedisTwemproxyCache;
import heracles.data.cache.service.DemoService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:cache/applicationContext-cache-test2.xml" }, inheritLocations = true)
public class HeraclesCacheBeanDefinitionParserTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DemoService demoService;

	@Test
	public void testHeraclesCache() {
		final String key1 = "2001";
		final String value1 = "tomhanks";
		final String key2 = "2002";
		final String value2 = "marycary";

		CacheManager cacheManager = (CacheManager) applicationContext.getBean("redisCacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");

		orderCache.put(key1, value1);

		orderCache.put(key2, value2);

		assertEquals(value1, orderCache.get(key1).get());
		assertEquals(value1, orderCache.get(key1, String.class));
		assertEquals(value2, orderCache.get(key2).get());
		assertEquals(value2, orderCache.get(key2, String.class));

		orderCache.evict(key1);
		orderCache.evict(key2);

		assertNull(orderCache.get(key1));
		assertNull(orderCache.get(key2));
	}
	
	@Test
	public void testHeraclesCacheObjectKey() {
		Object key1 = new Object();
		Object value1 = Long.valueOf("120");
		
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");
		orderCache.put(key1, value1);
		
		assertEquals(value1, orderCache.get(key1).get());
		orderCache.evict(key1);
		assertNull(orderCache.get(key1));
	}
	
	@Test
	public void testHeraclesCachePrimitiveKey() {
		long key1 = 120L;
		Object value1 = Long.valueOf("120");
		
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");
		orderCache.put(key1, value1);
		
		assertEquals(value1, orderCache.get(key1).get());
		orderCache.evict(key1);
		assertNull(orderCache.get(key1));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testHeraclesCacheClear() {
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");
		orderCache.clear();
	}

	@Test
	public void testHeraclesCachePutIfAbsent() {
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		RedisTwemproxyCache orderCache = (RedisTwemproxyCache) cacheManager.getCache("orderCacheCluster");

		String key1 = "pia-key1";
		String value1 = "pia-value1";
		String value2 = "pia-value2";

		orderCache.putIfAbsent(key1, value1);
		assertEquals(value1, orderCache.get(key1, String.class));

		orderCache.putIfAbsent(key1, value2);
		assertEquals(value1, orderCache.get(key1, String.class));
		
		orderCache.evict(key1);
	}

	@Test
	public void testHeraclesCacheAnnotation() {
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");

		String key1 = "2101";
		String key2 = "2102";
		String key3 = "2103";

		String value1 = "order1";
		String value2 = "order2";
		String value3 = "order3";

		demoService.findOrder(key1);
		demoService.findOrder(key2);
		demoService.findOrder(key3);
		assertEquals(value1, orderCache.get(key1, String.class));
		assertEquals(value2, orderCache.get(key2, String.class));
		assertEquals(value3, orderCache.get(key3, String.class));

		demoService.deleteOrder(key1);
		demoService.deleteOrder(key2);
		demoService.deleteOrder(key3);
		assertNull(orderCache.get(key1));
		assertNull(orderCache.get(key2));
		assertNull(orderCache.get(key3));
	}
	
	@Test
	public void testHeraclesCacheTemplate() {
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");
		Object template = orderCache.getNativeCache();
		
		Object templateBean = applicationContext.getBean("orderCacheClusterRedisTemplate");
		assertEquals(templateBean, template);
	}

}
