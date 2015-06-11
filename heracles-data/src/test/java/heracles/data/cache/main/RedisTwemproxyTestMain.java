package heracles.data.cache.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;

public class RedisTwemproxyTestMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisTwemproxyTestMain.class);

	private static final String SERVER_ADDRESS = "10.101.18.71";
	
	private static final int PORT = 6379;

	public static void main(String[] args) {
		loadBalanceTest();

	}

	@SuppressWarnings("unused")
	private static void jedisTest() {
		Jedis jedis = new Jedis(SERVER_ADDRESS, PORT);
		jedis.set("foo", "bar");
		String value = jedis.get("foo");

		System.out.println(value);
		jedis.close();
	}

	@SuppressWarnings("unused")
	private static void springDataJedis() {
		@SuppressWarnings("resource")
		ApplicationContext appContext = new ClassPathXmlApplicationContext("cache/applicationContext-cache.xml");


		CacheManager heraclesCheMgmr = (CacheManager) appContext.getBean("rdsTmpxyCheMgmr");

		LOGGER.info("redisCacheManager class is {}", heraclesCheMgmr.getClass());
		System.out.println(heraclesCheMgmr.getClass());

		Cache bookCache = heraclesCheMgmr.getCache("book");
		bookCache.put("1001", "tomhanks");
		
		bookCache.put("1002", "marycary");
		
		bookCache.evict("1002");
		

		LOGGER.info("1001 name is {}", bookCache.get("1001"));
		System.out.println(bookCache.get("1001").get());
		
		//bookCache.clear();
		
		try {
			Thread.sleep(10*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadBalanceTest() {
		@SuppressWarnings("resource")
		ApplicationContext appContext = new ClassPathXmlApplicationContext("cache/applicationContext-cache.xml");

		CacheManager cacheManager = (CacheManager) appContext.getBean("rdsTmpxyCheMgmr");
				
		int startNum = 1000;
		int taskNum = 100;
		List<Thread> threads = new ArrayList<Thread>(taskNum);
		for (int i = startNum; i < startNum + taskNum; i++) {
			String thdName = "thread_" + i;
			String taskName = "task_" + i;
			CacheTask cheTask = new CacheTask(taskName, cacheManager);
			Thread thread = new Thread(cheTask, thdName);
			threads.add(thread);
		}
		
		System.out.println("\n==== loadBalanceTest begin!");
		for (Thread thread : threads) {
			thread.start();
		}
		
		// wait for all thread done
		for (Thread thread : threads) {
			try {
				// wait until thread is done.
				thread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
		// yield current use of processor
		Thread.yield();
		System.out.println("\n==== loadBalanceTest completed!");
	}
	
	static class CacheTask implements Runnable {
		
		private CacheManager cacheManager;
		
		private String cacheName;
		
		private static final int CACHE_MAX_NUM = 100;
		
		
		public CacheTask(String cacheName, CacheManager cacheManager) {
			this.cacheName = cacheName;
			this.cacheManager = cacheManager;
		}


		@Override
		public void run() {
			Cache cache = cacheManager.getCache(cacheName);
			
			for (int i = 0; i < CACHE_MAX_NUM; i++) {
				String key = cacheName + "_" + i;
				String value = key;
				cache.put(key, value);
				sleep();
			}
			//LOGGER.info("Cache:{} put completed!", cacheName);
			System.out.println("Cache:" + cacheName + " put completed!");
			
			for (int i = 0; i < CACHE_MAX_NUM; i++) {
				String key = cacheName + "_" + i;
				ValueWrapper valueWrapper = cache.get(key);
				if (null == valueWrapper) {
					System.err.println("Cache:" + cacheName + " get err, key: " + key);
				} else {
					String value = (String) valueWrapper.get();
					Assert.assertEquals(key, value);
				}
				sleep();
			}
			//LOGGER.info("Cache:{} get completed!", cacheName);
			System.out.println("Cache:" + cacheName + " get completed!");
			
			for (int i = 0; i < CACHE_MAX_NUM; i++) {
				String key = cacheName + "_" + i;
				cache.evict(key);
				sleep();
			}
			//LOGGER.info("Cache:{} evict completed!", cacheName);
			System.out.println("Cache:" + cacheName + " evict completed!");
			
		}


		private void sleep() {
			/*
			 */
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	
	
}
