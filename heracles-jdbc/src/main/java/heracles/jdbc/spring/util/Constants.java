package heracles.jdbc.spring.util;

public interface Constants {

	public static final String XSD_MATRIX_NAME = "matrix-name";
	public static final String XSD_MATRIX_POOL_CONFIGS = "pool-configs";
	public static final String XSD_MATRIX_POOL_CONFIG = "pool-config";
	public static final String XSD_MATRIX_POOL_TYPE = "pool-type";
	public static final String XSD_MATRIX_ATOM_NAMES = "atom-names";

	// xsd element name
	public static final String XSD_ID = "id";
	public static final String XSD_DS_TYPE = "dsType";
	public static final String XSD_DB_TYPE = "dbType";
	public static final String XSD_PROPERTY = "property";
	public static final String XSD_NAME = "name";
	public static final String XSD_VALUE = "value";

	public static final String TABLE_NAMES = "tableNames";
	public static final String GROUP_SHARD_RULE = "groupShardRule";
	public static final String GROUP_INDEX = "groupIndex";
	public static final String TABLE_SHARD_RULE = "tableShardRule";
	public static final String TABLE_SUFFIX = "tableSuffix";

	public static final String DEFAULT_WEIGHT = "10";
	public static final String DEFAULT_DATASOURCE_ID = "dataSource";
	public static final String DEFAULT_DATASOURCE_TYPE = "c3p0";
	public static final String DEFAULT_DATASOURCE_DB_TYPE = "mysql";

	public static final String RULES = "rules";
	public static final String RULE_LIST = "ruleList";

	// spring property name
	public static final String WRITE = "write";
	public static final String STRATEGY = "strategy";
	public static final String LB_STRATEGY = "lbStrategy";
	public static final String TARGET_DATASOURCES = "targetDataSources";
	public static final String TARGET_DATASOURCE = "targetDataSource";
	public static final String DATASOURCES = "dataSources";

	public static final String DATASOURCE_TYPE_DBCP = "dbcp";
	public static final String DATASOURCE_TYPE_DRUID = "druid";
	public static final String DATASOURCE_TYPE_C3P0 = "c3p0";

	/**
	 * partition配置默认key
	 */
	public static final String ZOOKEEPER_DEFAULT_PARTITION_KEY = "heracles.cfgcenter.partition";

	/**
	 * 默认zkClient
	 */
	public static final String DEFAULT_ZKClIENT_NAME = "heracles.core.zookeeper.impl.CuratorZookeeperClient";

	/**
	 * zk数据备份路径property
	 */
	public static final String CONFIGURATION_BACKUP_PATH = "configurationBackupPath";

	public static final String DEFAULT_ENCODING = "utf-8";

	// matrix config zk path
	public static final String RESOURCE_RDBMS_MATRIX_PREFIX = "/resource/RDBMS/matrix";

	/**
	 * 默认partition
	 */
	public static final String ZOOKEEPER_PARTITION_DEFAULT = "default";

}
