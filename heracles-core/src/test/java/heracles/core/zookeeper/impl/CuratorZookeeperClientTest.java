package heracles.core.zookeeper.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-beanMapper.xml" })
@ActiveProfiles(value="test")
public class CuratorZookeeperClientTest {

	private CuratorZookeeperClient curatorZookeeperClient;
	
	private String path = "/test";
	
	private void initClient() {
		if(curatorZookeeperClient == null) {
			curatorZookeeperClient = new CuratorZookeeperClient("10.128.17.5:2181,10.128.17.6:2181,10.128.17.8:2181", 1000, 3, 5000, 10000);
		}	
	}
	
	private void closeClient() {
		if(curatorZookeeperClient != null) {
			curatorZookeeperClient.closeClient();
			curatorZookeeperClient = null;
		}
	}
	
	@Test
	public void testCreateOrUpdZnode() {
		
		initClient();
		try {
			if(curatorZookeeperClient.isExists(path)) {
				curatorZookeeperClient.delZnode(path);
			}
			curatorZookeeperClient.createOrUpdZnode(path, "测试专用");
			assertThat(curatorZookeeperClient.findChildData(path), equalTo("测试专用"));
			curatorZookeeperClient.delZnode(path);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeClient();
	}
	
	@Test
	public void testUpdZnode() {
		
		initClient();
		try {
			if(curatorZookeeperClient.isExists(path)) {
				curatorZookeeperClient.delZnode(path);
			}
			curatorZookeeperClient.createZnode(path, "新增测试专用");
			curatorZookeeperClient.updZnode(path, "修改测试专用");
			assertThat(curatorZookeeperClient.findChildData(path), equalTo("修改测试专用"));
			curatorZookeeperClient.delZnode(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeClient();
	}
}
