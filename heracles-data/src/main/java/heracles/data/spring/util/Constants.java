package heracles.data.spring.util;

public interface Constants {

	public static final String XSD_MATRIX_NAME = "matrix-name";
	public static final String XSD_MATRIX_TABLE_SHARDING = "table-sharding";
	public static final String XSD_MATRIX_REPOSITORY_SHARDING = "repository-sharding";
	public static final String XSD_MATRIX_POOL_CONFIGS = "pool-configs";
	public static final String XSD_MATRIX_POOL_CONFIG = "pool-config";
	public static final String XSD_MATRIX_POOL_TYPE = "pool-type";
	public static final String XSD_MATRIX_ATOM_NAMES = "atom-names";
	public static final String XSD_MATRIX_STRATEGIES_PACKAGE = "strategies-package";
	public static final String XSD_MATRIX_PROXY_TARGET_CLASS = "proxy-target-class";
	public static final String XSD_MATRIX_ORDER = "order";

	// aop config
	public static final String AOP_NAMESPACE_URI = "http://www.springframework.org/schema/aop";
	public static final String ANNOTATION_READ_WRITE_DATA_SOURCE_POINTCUT = "annotationReadWriteDataSourcePointcut";
	public static final String REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT = "repositoryShardingDataSourcePointcut";
	public static final String TABLE_SHARDING_DATA_SOURCE_POINTCUT = "tableShardingDataSourcePointcut";
	public static final String READWRITE_POINTCUT_EXPRESSION = "execution(* *..service*..*(..))";
	public static final String REPOSITORY_SHARDING_POINTCUT_EXPRESSION = "execution(* *..service*..*(..))";
	public static final String TABLE_SHARDING_POINTCUT_EXPRESSION = "execution(* *..repository*..*(..))";
	public static final String EXPRESSION = "expression";
	public static final String ADVICE_REF = "advice-ref";
	public static final String POINTCUT_REF = "pointcut-ref";
	public static final String ADVISOR = "advisor";
	public static final String CONFIG = "config";
	public static final String POINTCUT = "pointcut";

	// inteceptor order
	public static final String TABLE_SHARDING_DATA_SOURCE_POINTCUT_ORDER = "100";
	public static final String REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT_ORDER = "100";
	public static final String ANNOTATION_READWRITE_POINTCUT_ORDER = "200";
	public static final String TRANSACTION_ADVISOR_ORDER = "300";

	// xsd element name
	public static final String XSD_ID = "id";
	public static final String XSD_DS_TYPE = "dsType";
	public static final String XSD_DB_TYPE = "dbType";
	public static final String XSD_PROPERTY = "property";
	public static final String XSD_TRANSACTION_MANAGER = "transactionManager";
	public static final String XSD_MYBATIS_SQL_SESSION_FACTORY = "myBatisSqlSessionFactory";
	public static final String XSD_READ_WRITE_DATASOURCE = "readWriteDataSource";
	public static final String XSD_WRITE_DATASOURCE = "writeDataSource";
	public static final String XSD_READ_DATASOURCE = "readDataSource";
	public static final String XSD_TABLE_SHARDING = "tableSharding";
	public static final String XSD_REPOSITORY_SHARDING = "repositorySharding";
	public static final String XSD_NAME = "name";
	public static final String XSD_VALUE = "value";
	public static final String XSD_LOAD_BALANCE = "loadBalance";
	public static final String XSD_WEIGHT = "weight";
	public static final String XSD_LOGIC_NAME = "logicName";
	public static final String XSD_STRATEGIES_PACKAGE = "strategiesPackage";
	public static final String XSD_BEAN_NAME = "beanName";

	// default value
	public static final String DEFAULT_WEIGHT = "10";
	public static final String DEFAULT_TRANSACTION_MANAGER_NAME = "transactionManager";
	public static final String DEFAULT_DATASOURCE_ID = "dataSource";
	public static final String DEFAULT_DATASOURCE_TYPE = "c3p0";
	public static final String DEFAULT_DATASOURCE_DB_TYPE = "mysql";
	public static final String DEFAULT_MYBATIS_SQL_SESSION_FACTORY_NAME = "myBatisSqlSessionFactory";

	// spring property name
	public static final String READ_DATESOURCES = "readDateSources";
	public static final String WRITE_KEY = "writeKey";
	public static final String WRITE = "write";
	public static final String STRATEGY = "strategy";
	public static final String LB_STRATEGY = "lbStrategy";
	public static final String TARGET_DATASOURCES = "targetDataSources";
	public static final String TARGET_DATASOURCE = "targetDataSource";

	public static final String DATASOURCES = "dataSources";
	public static final String DEFAULT_TARGET_DATASOURCE = "defaultTargetDataSource";
	public static final String ADVICE_BEAN_NAME = "adviceBeanName";
	public static final String DATASOURCE_KEY = "dataSourceKey";
	public static final String DEFAULT_DATASOURCE = "defaultDataSource";
	public static final String TRANSACTION_ATTRIBUTE_SOURCE = "transactionAttributeSource";
	public static final String TRANSACTION_INTERCEPTOR = "transactionInterceptor";
	public static final String TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR = "transactionAttributeSourceAdvisor";
	public static final String ANNOTATION_AWARE_ASPECTJ_AUTO_PROXY_CREATOR = "annotationAwareAspectjAutoProxyCreator";
	public static final String ANNOTATION_READ_WRITE_DATASOURCE_INTERCEPTOR = "annotationReadWriteDataSourceInterceptor";
	public static final String READ_WRITE_DATASOURCE_KEYS = "readWriteDataSourceKeys";
	public static final String REPOSITORY_SHARDING_STRATEGIES = "repositoryShardingStrategies";
	public static final String SHARDING_DATASOURCE_KEYS = "shardingDataSourceKeys";
	public static final String REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR = "repositoryShardingDataSourceInterceptor";
	public static final String TABLE_SHARDING_STRATEGIES = "tableShardingStrategies";
	public static final String TABLE_SHARDING_DATASOURCE_INTERCEPTOR = "tableShardingDataSourceInterceptor";
	public static final String BEAN_NAMES = "beanNames";
	public static final String INTERCEPTOR_NAMES = "interceptorNames";
	public static final String SERVICE_BEAN_NAME_AUTO_PROXY_CREATOR = "serviceBeanNameAutoProxyCreator";
	public static final String REPOSITORY_BEAN_NAME_AUTO_PROXY_CREATOR = "repositoryBeanNameAutoProxyCreator";
	public static final String TRANSACTION_MANAGER = "transactionManager";
	public static final String TRANSACTION_MANAGER_BEAN_NAME = "transactionManagerBeanName";
	public static final String SQL_CONVERTER = "sqlConverter";
	public static final String SHARDING_PLUGIN = "shardingPlugin";
	public static final String PLUGINS = "plugins";

	public static final String DATASOURCE_TYPE_DBCP = "dbcp";
	public static final String DATASOURCE_TYPE_DRUID = "druid";
	public static final String DATASOURCE_TYPE_C3P0 = "c3p0";

	public static final String DEFAULT_SERVICE_BEAN_NAME = "*Service";
	public static final String DEFAULT_REPOSITORY_BEAN_NAME = "*Repository";

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
