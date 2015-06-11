package heracles.core.zookeeper;

public class ZnodeConstants {
	/**
	 * partition配置默认key
	 */
	public static final String ZOOKEEPER_DEFAULT_PARTITION_KEY = "cfgcenter.partition";

	/**
	 * 默认zkClient
	 */
	public static final String DEFAULT_ZKClIENT_NAME = "heracles.core.zookeeper.impl.CuratorZookeeperClient";

	public static final Integer BASE_SLEEP_TIME_MS = 1000;
	public static final Integer MAX_RETRIES = 3;
	public static final Integer CONNECTION_TIMEOUT_MS = 50000;
	public static final Integer SESSION_TIMEOUT_MS = 10000;

	public static final String DEFAULT_CHARSET = "utf-8";
}
