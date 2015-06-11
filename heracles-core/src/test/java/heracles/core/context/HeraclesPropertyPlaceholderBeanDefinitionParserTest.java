package heracles.core.context;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import heracles.core.context.property.PropertyManager;
import heracles.core.zookeeper.PropertiesUtils;
import heracles.core.zookeeper.PropertyChangedHandler;
import heracles.core.zookeeper.impl.CuratorZookeeperClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-beanMapper.xml" })
@ActiveProfiles(value = "test")
public class HeraclesPropertyPlaceholderBeanDefinitionParserTest {
	private CuratorZookeeperClient curatorZookeeperClient;

	@Before
	public void clearProps() {
		PropertiesUtils.setHaveResloveElement(false);
	}

	private void initClient() {
		curatorZookeeperClient = mock(CuratorZookeeperClient.class);
		try {
			when(curatorZookeeperClient.isExists(anyString())).thenReturn(true);
			when(curatorZookeeperClient.findChildData(anyString())).thenReturn("abc=def");
			when(curatorZookeeperClient.watch(anyString(), anyListOf(PropertyChangedHandler.class), anyString()))
					.thenReturn("abc=def");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	@Test
	public void test() {
		initClient();

		System.setProperty("spring.profiles.active", "test");
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext-test-context.xml");
		PropertyManager propertyManager = (PropertyManager) context.getBean("propertyManager");
		String value = propertyManager.getProperty("appname");
		assertThat(value, equalTo("unittest"));
	}
}
