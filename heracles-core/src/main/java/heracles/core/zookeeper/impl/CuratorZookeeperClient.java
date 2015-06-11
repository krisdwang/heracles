package heracles.core.zookeeper.impl;

import heracles.core.zookeeper.PropertyChangedHandler;
import heracles.core.zookeeper.ZnodeConstants;
import heracles.core.zookeeper.ZookeeperClient;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 
 * @author kriswang
 * 
 */
public class CuratorZookeeperClient implements ZookeeperClient {

	@Getter
	@Setter
	private CuratorFramework client;

	public CuratorZookeeperClient(String connection, Integer baseSleepTimeMs, Integer maxRetries, Integer connectionTimeoutMs, Integer sessionTimeoutMs) {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
		client = CuratorFrameworkFactory.builder().connectString(connection).retryPolicy(retryPolicy).connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs).build();
		client.start();
	}

	public CuratorZookeeperClient(String connection) {
		this(connection, ZnodeConstants.BASE_SLEEP_TIME_MS, ZnodeConstants.MAX_RETRIES, ZnodeConstants.CONNECTION_TIMEOUT_MS, ZnodeConstants.SESSION_TIMEOUT_MS);
	}

	@Override
	public void startClient() {
		client.start();
	}

	@Override
	public void closeClient() {
		client.close();
		client = null;
	}

	@Override
	public Boolean isStarted() {
		return CuratorFrameworkState.STARTED.equals(client.getState());
	}

	@Override
	public void updZnode(String path, String content) throws Exception {
		updZnode(path, content.getBytes(ZnodeConstants.DEFAULT_CHARSET));
	}
	
	@Override
	public void updZnode(String path, byte[] content) throws Exception {
		client.setData().forPath(path, content);
	}

	@Override
	public void delZnode(String path) throws Exception {
		client.delete().forPath(path);
	}

	@Override
	public void createZnode(String path, String content) throws Exception {
		createZnode(path, content.getBytes(ZnodeConstants.DEFAULT_CHARSET));
	}
	
	@Override
	public void createZnode(String path, byte[] content) throws Exception {
		client.create().creatingParentsIfNeeded().forPath(path, content);
	}

	@Override
	public void createOrUpdZnode(String path, String content) throws Exception {
		createOrUpdZnode(path, content.getBytes(ZnodeConstants.DEFAULT_CHARSET));
	}
	
	@Override
	public void createOrUpdZnode(String path, byte[] content) throws Exception {
		if (isExists(path)) {
			updZnode(path, content);
		} else {
			createZnode(path, content);
		}
	}

	@Override
	public List<String> findChildren(String path) throws Exception {
		return client.getChildren().forPath(path);
	}

	@Override
	public String findChildData(String path) throws Exception {
		return new String(findChildByte(path), ZnodeConstants.DEFAULT_CHARSET);
	}
	
	@Override
	public byte[] findChildByte(String path) throws Exception {
		return client.getData().forPath(path);
	}

	@Override
	public Boolean isExists(String path) throws Exception {
		return client.checkExists().forPath(path) != null;
	}

	@Override
	public String findChildDataAndWatch(String path, List<PropertyChangedHandler> handlers, String file) throws Exception {
		if (client.checkExists().forPath(path) != null) {
			String result = new String(client.getData().forPath(path), ZnodeConstants.DEFAULT_CHARSET);
			final NodeCache cache = new NodeCache(client, path);
			cache.getListenable().addListener(new PropertyChangedDispatcher(handlers, result, cache, file));
			try {
				cache.start(true);
			} catch (Exception e) {
				cache.close();
			}
			return result;
		}
		return null;
	}

	@Override
	public String watch(String path, List<PropertyChangedHandler> handlers, String file) throws Exception {
		if (client.checkExists().forPath(path) != null) {
			String result = new String(client.getData().forPath(path), ZnodeConstants.DEFAULT_CHARSET);
			final NodeCache cache = new NodeCache(client, path);
			cache.getListenable().addListener(new PropertyChangedDispatcher(handlers, cache, file));
			try {
				cache.start(true);
			} catch (Exception e) {
				cache.close();
			}
			return result;
		}
		return null;
	}
	
	@Override
	public void watchNode(String path, List<PropertyChangedHandler> handlers, String file) throws Exception {
		if (client.checkExists().forPath(path) != null) {
			final NodeCache cache = new NodeCache(client, path);
			cache.getListenable().addListener(new PropertyChangedDispatcher(handlers, cache, file));
			try {
				cache.start(true);
			} catch (Exception e) {
				cache.close();
			}
		}
	}

	@Override
	public String getCurrenConnectionStr() throws Exception {
		return client.getZookeeperClient().getCurrentConnectionString();
	}

}
