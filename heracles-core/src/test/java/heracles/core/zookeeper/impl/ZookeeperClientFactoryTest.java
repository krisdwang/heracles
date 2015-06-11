package heracles.core.zookeeper.impl;

import heracles.core.context.util.Constants;
import heracles.core.zookeeper.ZnodeConstants;
import heracles.core.zookeeper.ZookeeperClientFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-context.xml" })
@ActiveProfiles(value = "test")
public class ZookeeperClientFactoryTest {

	private static CuratorZookeeperClient curatorZookeeperClient;

	@BeforeClass
	public static void beforeClassSetup() {
		curatorZookeeperClient = new CuratorZookeeperClient("10.128.17.6:2181");
		try {
			curatorZookeeperClient.createOrUpdZnode("/default/bootstrap",
					"/**=10.128.17.6:2181");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClassSetup() {
		curatorZookeeperClient.closeClient();
	}

	@Test
	public void testGetClient() {
		try {
			ZookeeperClientFactory.getClient("");
			ZookeeperClientFactory.getClientFromPropertyFile("",
					"properties/test/application-test.properties");
			ZookeeperClientFactory
					.getClient(
							ZnodeConstants.DEFAULT_ZKClIENT_NAME,
							"/default",
							"",
							Constants.HERACLES_CFGCENTER_ZK_SLEEPTIME_MS_DEFAULT,
							Constants.HERACLES_CFGCENTER_ZK_MAXRETRIES_DEFAULT,
							Constants.HERACLES_CFGCENTER_ZK_TIMEOUT_MS_CONNECTION_DEFAULT,
							Constants.HERACLES_CFGCENTER_ZK_TIMEOUT_MS_SESSION_DEFAULT);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
