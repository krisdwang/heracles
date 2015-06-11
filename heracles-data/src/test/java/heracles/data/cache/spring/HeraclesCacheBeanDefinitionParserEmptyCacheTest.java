package heracles.data.cache.spring;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:cache/applicationContext-cache-test1.xml" }, inheritLocations = true)
public class HeraclesCacheBeanDefinitionParserEmptyCacheTest {

	@Autowired
	private ApplicationContext applicationContext;


	@Test
	public void testHeraclesCacheEmpty() {
		CacheManager cacheManager = (CacheManager) applicationContext.getBean("cacheManager");
		Cache orderCache = cacheManager.getCache("orderCacheCluster");
		assertNull(orderCache);
	}



}
