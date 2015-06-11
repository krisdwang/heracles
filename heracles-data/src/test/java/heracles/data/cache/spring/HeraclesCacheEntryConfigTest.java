package heracles.data.cache.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import heracles.data.cache.service.DemoService;
import heracles.data.cache.service.OrderModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:cache/applicationContext-cache-test4.xml" }, inheritLocations = true)
public class HeraclesCacheEntryConfigTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DemoService demoService;

	@Test
	public void testHeraclesCacheGlobalConfig() {
		final String key1 = "4001";
		final String value1 = "tomhanks";
		final String key2 = "4002";
		final String value2 = "marycary";

		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");

		orderCache.put(key1, value1);
		orderCache.put(key2, value2);

		assertEquals(value1, orderCache.get(key1).get());
		assertEquals(value1, orderCache.get(key1, String.class));
		assertEquals(value2, orderCache.get(key2).get());
		assertEquals(value2, orderCache.get(key2, String.class));
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to sleep and validate expiration", e);
		}
		
		assertNull(orderCache.get(key1));
		assertNull(orderCache.get(key2));

		orderCache.evict(key1);
		orderCache.evict(key2);

		assertNull(orderCache.get(key1));
		assertNull(orderCache.get(key2));
	}

	@Test
	public void testHeraclesCacheAnnotation() {
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");

		String key1 = "4101";
		String key2 = "4102";
		String key3 = "4103";

		demoService.deleteOrderObject(key1);
		demoService.deleteOrderObject(key2);
		demoService.deleteOrderObject(key3);
		demoService.findOrderObject(key1);
		demoService.findOrderObject(key2);
		demoService.findOrderObject(key3);
		
		JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
		
//		OrderModel aa = demoService.getOrderObjectMap().get(key1);
//		OrderModel bb = orderCache.get(key1, OrderModel.class);
//		byte[] cc = serializer.serialize(aa);
//		byte[] dd = serializer.serialize(bb);
		
		assertArrayEquals(serializer.serialize(demoService.getOrderObjectMap().get(key1)),
				serializer.serialize(orderCache.get(key1, OrderModel.class)));
		assertArrayEquals(serializer.serialize(demoService.getOrderObjectMap().get(key2)),
				serializer.serialize(orderCache.get(key2, OrderModel.class)));
		assertArrayEquals(serializer.serialize(demoService.getOrderObjectMap().get(key3)),
				serializer.serialize(orderCache.get(key3, OrderModel.class)));
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to sleep and validate expiration", e);
		}
		assertNull(orderCache.get(key1));
		assertNull(orderCache.get(key2));
		assertNull(orderCache.get(key3));
		
		demoService.deleteOrderObject(key1);
		demoService.deleteOrderObject(key2);
		demoService.deleteOrderObject(key3);
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
