package heracles.core.zookeeper;

import heracles.core.context.property.PropertyHolder;
import heracles.core.context.util.Constants;
import heracles.core.exception.InitZKException;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZookeeperClientFactory
 * 
 * @author kriswang
 * 
 */
public class ZookeeperClientFactory {

	private static Logger LOGGER = LoggerFactory.getLogger(ZookeeperClientFactory.class);

	private static final String CONNECTION = "heracles.cfgcenter.zkclient.connection";
	private static final String BASE_SLEEP_TIME_MS = "heracles.cfgcenter.zkclient.baseSleepTimeMs";
	private static final String MAX_RETRIES = "heracles.cfgcenter.zkclient.maxRetries";
	private static final String CONNECTION_TIMEOUT_MS = "heracles.cfgcenter.zkclient.connectionTimeoutMs";
	private static final String SESSION_TIMEOUT_MS = "heracles.cfgcenter.zkclient.sessionTimeoutMs";

	@Deprecated
	public static Object getClient(String className, String requestPath, String propertyFile) throws InitZKException {
		return getClient(className, requestPath, PropertyHolder.getProperties());
	}

	public static Object getClient(String className, String requestPath, Properties localProps) throws InitZKException {
		ZookeeperClient zkClient = initZkClient(className, localProps);
		String data = "";
		try {
			data = zkClient.findChildData("/" + ZnodeUtils.getPartitionPath(requestPath) + "/bootstrap");
		} catch (Exception e) {
			LOGGER.error("zk read fail [" + e.getMessage() + "]");
		}
		Map<String, Object> zkPropsMap = ZnodeUtils.propertiesToMap(data);
		if (zkPropsMap != null) {
			for (Map.Entry<String, Object> entry : zkPropsMap.entrySet()) {
				if (ZnodeUtils.isMatch(entry.getKey(), ZnodeUtils.convert(requestPath))) {
					zkClient.closeClient();
					Properties props = new Properties();
					props.setProperty(CONNECTION, entry.getValue().toString());
					return initZkClient(className, props);
				}
			}
		}

		return zkClient;
	}

	public static Object getClient(String className, String requestPath, String propertyFile, Integer baseSleepTimeMs,
			Integer maxRetries, Integer connectionTimeoutMs, Integer sessionTimeoutMs) throws InitZKException {

		ZookeeperClient zkClient = initZkClient(className, PropertyHolder.getProperties());
		String data = "";
		try {
			data = zkClient.findChildData("/" + ZnodeUtils.getPartitionPath(requestPath) + "/bootstrap");
		} catch (Exception e) {
			LOGGER.error("zk read fail [" + e.getMessage() + "]");
		}
		Map<String, Object> zkPropsMap = ZnodeUtils.propertiesToMap(data);
		if (zkPropsMap != null) {
			for (Map.Entry<String, Object> entry : zkPropsMap.entrySet()) {
				if (ZnodeUtils.isMatch(entry.getKey(), ZnodeUtils.convert(requestPath))) {
					zkClient.closeClient();
					Properties props = new Properties();
					props.setProperty(CONNECTION, entry.getValue().toString());
					props.setProperty(BASE_SLEEP_TIME_MS, baseSleepTimeMs + "");
					props.setProperty(MAX_RETRIES, maxRetries + "");
					props.setProperty(CONNECTION_TIMEOUT_MS, connectionTimeoutMs + "");
					props.setProperty(SESSION_TIMEOUT_MS, sessionTimeoutMs + "");
					return initZkClient(className, props);
				}
			}
		}

		return zkClient;
	}

	public static Object getClient(String className, String requestPath) throws InitZKException {
		return getClient(className, requestPath, "");
	}

	public static Object getClientFromPropertyFile(String requestPath, String propertyFile) throws InitZKException {
		return getClient(ZnodeConstants.DEFAULT_ZKClIENT_NAME, requestPath, propertyFile);
	}

	public static Object getClient(String requestPath) throws InitZKException {
		return getClient(ZnodeConstants.DEFAULT_ZKClIENT_NAME, requestPath);
	}

	public static Object getDefaultClient() throws InitZKException {
		ZookeeperClient zkClient = initZkClient(ZnodeConstants.DEFAULT_ZKClIENT_NAME, PropertyHolder.getProperties());
		return zkClient;

	}

	public static Object getSpecifyClient(String connection) throws InitZKException {
		return getSpecifyClient(connection, Constants.HERACLES_CFGCENTER_ZK_SLEEPTIME_MS_DEFAULT,
				Constants.HERACLES_CFGCENTER_ZK_MAXRETRIES_DEFAULT,
				Constants.HERACLES_CFGCENTER_ZK_TIMEOUT_MS_CONNECTION_DEFAULT,
				Constants.HERACLES_CFGCENTER_ZK_TIMEOUT_MS_SESSION_DEFAULT);
	}

	public static Object getSpecifyClient(String connection, Integer baseSleepTimeMs, Integer maxRetries,
			Integer connectionTimeoutMs, Integer sessionTimeoutMs) throws InitZKException {
		if (StringUtils.isBlank(connection)) {
			return null;
		}
		Properties props = new Properties();
		props.setProperty(CONNECTION, connection);
		props.setProperty(BASE_SLEEP_TIME_MS, baseSleepTimeMs + "");
		props.setProperty(MAX_RETRIES, maxRetries + "");
		props.setProperty(CONNECTION_TIMEOUT_MS, connectionTimeoutMs + "");
		props.setProperty(SESSION_TIMEOUT_MS, sessionTimeoutMs + "");
		ZookeeperClient zkClient = initZkClient(ZnodeConstants.DEFAULT_ZKClIENT_NAME, props);

		return zkClient;
	}

	/**
	 * 初始化zkClient
	 */
	private static ZookeeperClient initZkClient(String zkClientName, Properties props) throws InitZKException {
		if (StringUtils.isBlank((String) props.get(CONNECTION))) {
			throw new InitZKException("Initialize zk failed, please config property :" + CONNECTION);
		}

		try {
			Class<?> c = Class.forName(zkClientName);
			Class<?>[] parameterTypes = { String.class, Integer.class, Integer.class, Integer.class, Integer.class };
			/**
			 * 根据参数类型获取相应的构造函数
			 */
			Constructor<?> constructor = c.getConstructor(parameterTypes);

			/**
			 * 参数数组
			 */
			Object[] parameters = {
					props.get(CONNECTION),
					(props.get(BASE_SLEEP_TIME_MS) == null) ? Constants.HERACLES_CFGCENTER_ZK_SLEEPTIME_MS_DEFAULT : Integer
							.valueOf(props.get(BASE_SLEEP_TIME_MS).toString()),
					(props.get(MAX_RETRIES) == null) ? Constants.HERACLES_CFGCENTER_ZK_MAXRETRIES_DEFAULT : Integer
							.valueOf(props.get(MAX_RETRIES).toString()),
					(props.get(CONNECTION_TIMEOUT_MS) == null) ? Constants.HERACLES_CFGCENTER_ZK_TIMEOUT_MS_CONNECTION_DEFAULT
							: Integer.valueOf(props.get(CONNECTION_TIMEOUT_MS).toString()),
					(props.get(SESSION_TIMEOUT_MS) == null) ? Constants.HERACLES_CFGCENTER_ZK_TIMEOUT_MS_SESSION_DEFAULT
							: Integer.valueOf(props.get(SESSION_TIMEOUT_MS).toString()) };
			/**
			 * 根据获取的构造函数和参数，创建实例
			 */
			ZookeeperClient zkClient = (ZookeeperClient) constructor.newInstance(parameters);
			return zkClient;
		} catch (Exception e) {
			LOGGER.error("initZkClient fail***please ignore the error when you are in [development] environment*** :"
					+ e.getMessage());
			throw new InitZKException("Initialize zk failed, please config property :" + CONNECTION);
		}
	}
}
