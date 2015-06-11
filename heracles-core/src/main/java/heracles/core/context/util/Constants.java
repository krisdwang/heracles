package heracles.core.context.util;

/**
 * 
 * @author kriswang
 * 
 */
public class Constants {

	/**
	 * 默认配置文件名
	 */
	public static final String CFG_DEF_FILENAME = "applicationCfgDef.xml";

	/**
	 * 忽略检查的配置中心配置前缀
	 */
	public static final String IGNORE_CHECK_KEY = "resource.";

	public static final String ZOOKEEPER_PARTITION = "heracles.cfgcenter.partition";
	public static final String ARTIFACT_GROUP = "cfgdef.group";
	public static final String ARTIFACT_NAME = "cfgdef.name";
	public static final String ARTIFACT_VERSION = "cfgdef.version";

	public static final String FILE_PREFIX = "file:";
	public static final String ZOOKEEPER_PREFIX = "zk:";
	public static final String CLASSPATH_PREFIX = "classpath:";
	public static final String EMPTY_STRING = "";
	public static final String DEFAULT_ENCODING = "utf-8";

	/**
	 * artifact配置前缀
	 */
	public static final String ARTIFACT_ALL_PREFIX = "${";
	/**
	 * artifact配置后缀
	 */
	public static final String ARTIFACT_ALL_SUFFIX = "}";

	/**
	 * 配置中心默认connection
	 * FIXME:zk hosts
	 */
	public static final String HERACLES_CFGCENTER_ZK_CONNECTION_DEFAULT = "10.128.17.6:2181,10.128.17.5:2181,10.128.17.8:2181";
	/**
	 * 配置中心默认重试间隔时间
	 */
	public static final Integer HERACLES_CFGCENTER_ZK_SLEEPTIME_MS_DEFAULT = 1000;
	/**
	 * 配置中心默认最大重试次数
	 */
	public static final Integer HERACLES_CFGCENTER_ZK_MAXRETRIES_DEFAULT = 3;
	/**
	 * 配置中心默认连接超时时间
	 */
	public static final Integer HERACLES_CFGCENTER_ZK_TIMEOUT_MS_CONNECTION_DEFAULT = 50000;
	/**
	 * 配置中心默认session超时时间
	 */
	public static final Integer HERACLES_CFGCENTER_ZK_TIMEOUT_MS_SESSION_DEFAULT = 10000;
	/**
	 * 默认partition
	 */
	public static final String ZOOKEEPER_PARTITION_DEFAULT = "default";

	public static final String CONFIGURATION_BACKUP_PATH = "configurationBackupPath";
}
