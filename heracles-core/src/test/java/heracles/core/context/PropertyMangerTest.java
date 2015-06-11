//package heracles.core.context;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.mockito.Matchers.anyListOf;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import heracles.core.context.property.PropertyHolder;
//import heracles.core.context.property.PropertyManager;
//import heracles.core.context.property.impl.PropertyManagerImpl;
//import heracles.core.zookeeper.PropertyChangedHandler;
//import heracles.core.zookeeper.impl.CuratorZookeeperClient;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:applicationContext-test-context.xml" })
//@ActiveProfiles(value="test")
//public class PropertyMangerTest {
//
//	@Autowired
//	private PropertyManager propertyManager;
//	
//	private PropertyManagerImpl propertyManagerImpl = new PropertyManagerImpl();
//	
//	private String testKey = "heracles";
//	private String testValue = "kris";
//	private String defaultValue = "default";
//	
//	private CuratorZookeeperClient curatorZookeeperClient;  
//	
//	private void initClient() {
//		curatorZookeeperClient = mock(CuratorZookeeperClient.class);
//		try {
//			when(curatorZookeeperClient.isExists(anyString())).thenReturn(true);
//			when(curatorZookeeperClient.getCurrenConnectionStr()).thenReturn("10.128.17.6:2181");
//			when(curatorZookeeperClient.findChildData(anyString())).thenReturn("/abc=def");
//			when(curatorZookeeperClient.watch(anyString(), anyListOf(PropertyChangedHandler.class), anyString())).thenReturn("/abc=def");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		List<String> paths = new ArrayList<String>();
//		paths.add("/default/abc");
//		propertyManagerImpl.setPaths(paths);
//		List<PropertyChangedHandler> handlers = new ArrayList<PropertyChangedHandler>();
//		handlers.add(new PropertyChangedHandler() {
//
//			@Override
//			public void execute(Properties oldProps, Properties newProps) {				
//				
//			}
//			
//		});
//		propertyManagerImpl.setHandlers(handlers);
//		Map<String, Object> zkClientMap = new LinkedHashMap<String, Object>();
//		zkClientMap.put("def", curatorZookeeperClient);
//		propertyManagerImpl.setZkClientMap(zkClientMap);
//		propertyManagerImpl.setDefaultZkClient(curatorZookeeperClient);
//	}
//	
//	private void initClient2() {
//		curatorZookeeperClient = mock(CuratorZookeeperClient.class);
//		try {
//			when(curatorZookeeperClient.isExists(anyString())).thenReturn(true);
//			when(curatorZookeeperClient.getCurrenConnectionStr()).thenReturn("10.128.17.6:2181");
//			when(curatorZookeeperClient.findChildData(anyString())).thenReturn("/abc=def");
//			when(curatorZookeeperClient.watch(anyString(), anyListOf(PropertyChangedHandler.class), anyString())).thenReturn("/abc=def");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		List<String> paths = new ArrayList<String>();
//		paths.add("/default/abc");
//		propertyManagerImpl.setPaths(paths);
//		List<PropertyChangedHandler> handlers = new ArrayList<PropertyChangedHandler>();
//		handlers.add(new PropertyChangedHandler() {
//
//			@Override
//			public void execute(Properties oldProps, Properties newProps) {
//				
//				
//			}
//			
//		});
//		propertyManagerImpl.setHandlers(handlers);
//		propertyManagerImpl.setDefaultZkClient(curatorZookeeperClient);
//	}
//	
//	@Test
//	public void testAfterPropertiesSet() {
//		initClient();
//		try {
//			propertyManagerImpl.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Test
//	public void testAfterPropertiesSet2() {
//		initClient2();
//		try {
//			propertyManagerImpl.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void init() {
//		Properties properties = new Properties();
//		properties.setProperty(testKey, testValue);
//		PropertyHolder.setProperties(properties);
//	}
//	
//	private void destroy() {
//		PropertyHolder.setProperties(null);
//	}
//	
//	@Test
//	public void testGetProperty() {
//		destroy();
//		assertThat(propertyManager.getProperty(testKey), equalTo(null));
//		init();
//		assertThat(propertyManager.getProperty(testKey), equalTo(testValue));
//	}
//	
//	@Test
//	public void testGetProperty2() {
//		destroy();
//		assertThat(propertyManager.getProperty(testKey, defaultValue), equalTo(defaultValue));
//		init();
//		assertThat(propertyManager.getProperty(testKey), equalTo(testValue));
//	}
//	
//	@Test
//	public void testKeys() {
//		destroy();
//		assertThat(propertyManager.keys(), equalTo(null));
//		init();
//		assertThat(propertyManager.keys().hasMoreElements(), equalTo(true));
//	}
//	
//	@Test
//	public void testValues() {
//		destroy();
//		assertThat(propertyManager.values(), equalTo(null));
//		init();
//		assertThat(propertyManager.values().size(), equalTo(1));
//	}
//	
//}
