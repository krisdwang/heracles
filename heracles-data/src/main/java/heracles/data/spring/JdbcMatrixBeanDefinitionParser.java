package heracles.data.spring;

import heracles.core.context.property.PropertyHolder;
import heracles.core.zookeeper.PropertiesUtils;
import heracles.core.zookeeper.ZookeeperClient;
import heracles.core.zookeeper.ZookeeperClientFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import heracles.core.context.util.PredefinedCfgDefEnum;
import heracles.data.common.annotation.Strategy;
import heracles.data.datasource.ReadWriteDataSource;
import heracles.data.datasource.ReadWriteDataSourceKey;
import heracles.data.datasource.RepositoryShardingDataSource;
import heracles.data.datasource.interceptor.AnnotationReadWriteDataSourceInterceptor;
import heracles.data.datasource.interceptor.RepositoryShardingDataSourceInterceptor;
import heracles.data.datasource.interceptor.TableShardingDataSourceInterceptor;
import heracles.data.mybatis.converter.DefaultSqlConverter;
import heracles.data.mybatis.plugin.ShardingPlugin;
import heracles.data.spring.util.Constants;
import heracles.data.spring.util.Utils;
import heracles.data.spring.vo.AtomDataSourceMetaVO;
import heracles.data.spring.vo.DataSourceMetaVO;
import heracles.data.spring.vo.ReadWriteDataSourceMetaVO;
import heracles.data.spring.vo.RepositoryShardingMetaVO;
import heracles.data.spring.vo.TableShardingMetaVO;

public class JdbcMatrixBeanDefinitionParser implements BeanDefinitionParser, Constants {

	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcMatrixBeanDefinitionParser.class);

	private Map<String, String> logicnameMap = new HashMap<String, String>();

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		/**
		 * init all props
		 */
		PropertiesUtils.getPropsConfig(element);

		DataSourceMetaVO dataSourceMetaVO = parseDataSource(element);
		processRWDataSourceMetaVOs(parserContext, dataSourceMetaVO);

		List<String> dsZkPathList = getDsZkPath(PropertyHolder.getProperties());
		if (CollectionUtils.isNotEmpty(dsZkPathList)) {
			PropertyHolder.addProperties(loadRemoteResourceProperties(dsZkPathList));
		}

		// TODO DataSourceConfigManager watch zk by lusong

		return null;
	}

	/**
	 * 拿到ds 的zk path
	 */
	private List<String> getDsZkPath(Properties props) {
		List<String> dsZkPathList = new ArrayList<String>();
		if (logicnameMap != null) {
			Set<String> keySet = logicnameMap.keySet();
			for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
				String key = (String) it.next();
				Object value = logicnameMap.get(key);
				if (value != null && StringUtils.isNotBlank(value.toString())
						&& StringUtils.isNotBlank(PredefinedCfgDefEnum.getPath(value.toString()))) {
					String cfgPartition = (String) props.get(Constants.ZOOKEEPER_DEFAULT_PARTITION_KEY);
					String basePath = "/"
							+ (StringUtils.isEmpty(cfgPartition) ? Constants.ZOOKEEPER_PARTITION_DEFAULT : cfgPartition)
							+ PredefinedCfgDefEnum.getPath(value.toString()) + key;
					dsZkPathList.add(basePath);
					LOGGER.info("cfgcenter [" + basePath + "]");
				}
			}
		}
		return dsZkPathList;
	}

	/**
	 * 读zookeeper的resource配置
	 * 
	 * @param props Properties
	 * @param zkPathList List<String>
	 */
	private Properties loadRemoteResourceProperties(List<String> zkPathList) {
		Properties properties = new Properties();
		if (CollectionUtils.isNotEmpty(zkPathList)) {
			for (String path : zkPathList) {
				try {
					String data = "";
					String requestPath = path;
					if (path.toLowerCase().contains("/mysql") || path.toLowerCase().contains("/oracle")) {
						requestPath = StringUtils.substringBeforeLast(path, "/");
					}
					ZookeeperClient zkClient = (ZookeeperClient) ZookeeperClientFactory.getClient(
							Constants.DEFAULT_ZKClIENT_NAME, requestPath);
					data = zkClient.findChildData(path);

					// load zk data and backup
					String configurationBackupPath = (String) PropertyHolder.getProperties().get(
							Constants.CONFIGURATION_BACKUP_PATH);
					PropertiesUtils.loadZkAndBackup(properties, path, data, configurationBackupPath);

					LOGGER.info("datasource cfgcenter [" + path + ":" + data + "]");
				} catch (Exception e) {
					LOGGER.error("loadRemoteResourceProperties fail [" + e.getMessage() + "]");
				}
			}
		}
		return properties;
	}

	private void processRWDataSourceMetaVOs(ParserContext parserContext, DataSourceMetaVO dataSourceMetaVO) {
		List<ReadWriteDataSourceMetaVO> readWriteDataSourceMetaVOs = dataSourceMetaVO.getReadWriteDataSourceMetaVOs();
		if (CollectionUtils.isNotEmpty(readWriteDataSourceMetaVOs)) {
			ManagedMap<String, RuntimeBeanReference> dsMap = new ManagedMap<String, RuntimeBeanReference>();
			ManagedMap<String, RuntimeBeanReference> dsKeyMap = new ManagedMap<String, RuntimeBeanReference>();
			boolean isDefaultDataSource = true;
			String defaultReadWrite = null;
			for (ReadWriteDataSourceMetaVO readWriteDataSourceMetaVO : readWriteDataSourceMetaVOs) {
				ManagedMap<String, RuntimeBeanReference> rwdsMap = new ManagedMap<String, RuntimeBeanReference>();

				// 写原子数据源
				AtomDataSourceMetaVO writeDataSourceMetaVO = readWriteDataSourceMetaVO.getWriteDataSourceMetaVO();
				RootBeanDefinition writeDataSource = new RootBeanDefinition(dataSourceMetaVO.getDataSourceClass());
				writeDataSource.getPropertyValues().addPropertyValues(dataSourceMetaVO.getProperties());
				writeDataSource.getPropertyValues().addPropertyValues(writeDataSourceMetaVO.getProperties());
				writeDataSource.getPropertyValues().addPropertyValues(
						Utils.getDbLoginProperties(writeDataSourceMetaVO.getLogicName(), dataSourceMetaVO.getDbType(),
								dataSourceMetaVO.getDsType()));

				// 读原子数据源
				List<AtomDataSourceMetaVO> readDataSourceMetaVOs = readWriteDataSourceMetaVO.getReadDataSourceMetaVOs();
				if (CollectionUtils.isNotEmpty(readDataSourceMetaVOs)) {
					int i = 0;
					for (AtomDataSourceMetaVO readDataSourceMetaVO : readDataSourceMetaVOs) {
						RootBeanDefinition readDataSource = new RootBeanDefinition(
								dataSourceMetaVO.getDataSourceClass());
						readDataSource.getPropertyValues().addPropertyValues(dataSourceMetaVO.getProperties());
						readDataSource.getPropertyValues().addPropertyValues(readDataSourceMetaVO.getProperties());
						readDataSource.getPropertyValues().addPropertyValues(
								Utils.getDbLoginProperties(readDataSourceMetaVO.getLogicName(),
										dataSourceMetaVO.getDbType(), dataSourceMetaVO.getDsType()));
						String readDataSourceName = readWriteDataSourceMetaVO.getReadBeanDefinitionName(i++);
						parserContext.getRegistry().registerBeanDefinition(readDataSourceName, readDataSource);
						rwdsMap.put(readDataSourceName, new RuntimeBeanReference(readDataSourceName));
					}

					// 负载均衡
					RootBeanDefinition loadBalance = new RootBeanDefinition(
							readWriteDataSourceMetaVO.getLoadBalanceClass());
					loadBalance.getConstructorArgumentValues().addGenericArgumentValue(
							readWriteDataSourceMetaVO.getLoadBalanceArgs());
					parserContext.getRegistry().registerBeanDefinition(
							readWriteDataSourceMetaVO.getLoadBalanceBeanDefinitionName(), loadBalance);

					// 读写Key
					RootBeanDefinition readWriteDataSourceKey = new RootBeanDefinition(ReadWriteDataSourceKey.class);
					readWriteDataSourceKey.getPropertyValues().add(READ_DATESOURCES,
							readWriteDataSourceMetaVO.getReadKeys());
					readWriteDataSourceKey.getPropertyValues().add(WRITE_KEY, readWriteDataSourceMetaVO.getWriteKey());
					readWriteDataSourceKey.getPropertyValues().add(STRATEGY,
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getLoadBalanceBeanDefinitionName()));
					parserContext.getRegistry().registerBeanDefinition(
							readWriteDataSourceMetaVO.getReadWriteKeyBeanDefinitionName(), readWriteDataSourceKey);

					// 读写数据源
					RootBeanDefinition readWriteDataSource = new RootBeanDefinition(ReadWriteDataSource.class);
					readWriteDataSource.getPropertyValues().add(TARGET_DATASOURCES, rwdsMap);
					readWriteDataSource.getPropertyValues().add(DEFAULT_TARGET_DATASOURCE,
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getWriteBeanDefinitionName()));
					readWriteDataSource.getPropertyValues().add(DATASOURCE_KEY,
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getReadWriteKeyBeanDefinitionName()));
					parserContext.getRegistry().registerBeanDefinition(readWriteDataSourceMetaVO.getName(),
							readWriteDataSource);
					dsMap.put(readWriteDataSourceMetaVO.getName(),
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getName()));
					dsKeyMap.put(readWriteDataSourceMetaVO.getName(), new RuntimeBeanReference(
							readWriteDataSourceMetaVO.getReadWriteKeyBeanDefinitionName()));

					// 注册写原子数据源
					parserContext.getRegistry().registerBeanDefinition(
							readWriteDataSourceMetaVO.getWriteBeanDefinitionName(), writeDataSource);
					rwdsMap.put(readWriteDataSourceMetaVO.getWriteBeanDefinitionName(), new RuntimeBeanReference(
							readWriteDataSourceMetaVO.getWriteBeanDefinitionName()));
				} else {
					dsMap.put(readWriteDataSourceMetaVO.getName(),
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getName()));

					// 注册写原子数据源，伪装成读写数据源
					parserContext.getRegistry().registerBeanDefinition(readWriteDataSourceMetaVO.getName(),
							writeDataSource);
				}

				if (isDefaultDataSource) {
					isDefaultDataSource = false;
					defaultReadWrite = readWriteDataSourceMetaVO.getName();
				}
			}

			// 数据源
			RootBeanDefinition dataSource = new RootBeanDefinition(RepositoryShardingDataSource.class);
			dataSource.getPropertyValues().add(TARGET_DATASOURCES, dsMap);
			dataSource.getPropertyValues().add(DEFAULT_TARGET_DATASOURCE, dsMap.get(defaultReadWrite));
			parserContext.getRegistry().registerBeanDefinition(dataSourceMetaVO.getId(), dataSource);

			// 分库/分表策略扫描器
			StrategiesClassPathBeanDefinitionScanner scan = new StrategiesClassPathBeanDefinitionScanner(
					parserContext.getRegistry());
			scan.setAnnotationClass(Strategy.class);
			scan.registerFilters();

			RepositoryShardingMetaVO repositoryShardingMetaVO = dataSourceMetaVO.getRepositoryShardingMetaVO();
			if (repositoryShardingMetaVO != null) {
				// serviceBeanNameAutoProxyCreator
				List<String> interceptorNames = new ArrayList<String>();

				// 分库拦截器
				if (StringUtils.isNotBlank(repositoryShardingMetaVO.getStrategiesPackage())) {
					RootBeanDefinition repositoryShardingDataSourceInterceptor = new RootBeanDefinition(
							RepositoryShardingDataSourceInterceptor.class);
					repositoryShardingDataSourceInterceptor.getPropertyValues().add(
							REPOSITORY_SHARDING_STRATEGIES,
							scanRepositoryStrategies(scan, repositoryShardingMetaVO.getStrategiesPackage(),
									defaultReadWrite));
					// repositoryShardingDataSourceInterceptor.getPropertyValues().add(DEFAULT_DATASOURCE,
					// defaultReadWrite);
					parserContext.getRegistry().registerBeanDefinition(REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR,
							repositoryShardingDataSourceInterceptor);
					interceptorNames.add(REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR);
				}

				// 读写拦截器
				RootBeanDefinition annotationReadWriteDataSourceInterceptor = new RootBeanDefinition(
						AnnotationReadWriteDataSourceInterceptor.class);
				annotationReadWriteDataSourceInterceptor.getPropertyValues().add(READ_WRITE_DATASOURCE_KEYS, dsKeyMap);
				parserContext.getRegistry().registerBeanDefinition(ANNOTATION_READ_WRITE_DATASOURCE_INTERCEPTOR,
						annotationReadWriteDataSourceInterceptor);
				interceptorNames.add(ANNOTATION_READ_WRITE_DATASOURCE_INTERCEPTOR);

				if (StringUtils.isNotBlank(dataSourceMetaVO.getTransactionManager())) {
					// annotationTransactionAttributeSource
					RootBeanDefinition annotationTransactionAttributeSource = new RootBeanDefinition(
							AnnotationTransactionAttributeSource.class);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE,
							annotationTransactionAttributeSource);

					// 事务拦截器
					RootBeanDefinition transactionInterceptor = new RootBeanDefinition(TransactionInterceptor.class);
					transactionInterceptor.getPropertyValues().add(TRANSACTION_MANAGER,
							new RuntimeBeanReference(dataSourceMetaVO.getTransactionManager()));
					// transactionInterceptor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE, new
					// BeanDefinitionHolder(new RootBeanDefinition(AnnotationTransactionAttributeSource.class),
					// TRANSACTION_ATTRIBUTE_SOURCE));
					transactionInterceptor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE,
							new RuntimeBeanReference(TRANSACTION_ATTRIBUTE_SOURCE));
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_INTERCEPTOR, transactionInterceptor);
					interceptorNames.add(TRANSACTION_INTERCEPTOR);

					// transactionAttributeSourceAdvisor
					RootBeanDefinition transactionAttributeSourceAdvisor = new RootBeanDefinition(
							TransactionAttributeSourceAdvisor.class);
					transactionAttributeSourceAdvisor.getPropertyValues().add(TRANSACTION_INTERCEPTOR,
							new RuntimeBeanReference(TRANSACTION_INTERCEPTOR));
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR,
							transactionAttributeSourceAdvisor);
				}

				RootBeanDefinition servicebeanNameAutoProxyCreator = new RootBeanDefinition(
						BeanNameAutoProxyCreator.class);
				servicebeanNameAutoProxyCreator.getPropertyValues().add(BEAN_NAMES,
						repositoryShardingMetaVO.getBeanNames());
				servicebeanNameAutoProxyCreator.getPropertyValues().add(INTERCEPTOR_NAMES, interceptorNames);
				parserContext.getRegistry().registerBeanDefinition(SERVICE_BEAN_NAME_AUTO_PROXY_CREATOR,
						servicebeanNameAutoProxyCreator);
			} else {
				if (StringUtils.isNotBlank(dataSourceMetaVO.getTransactionManager())) {
					// annotationTransactionAttributeSource
					RootBeanDefinition annotationTransactionAttributeSource = new RootBeanDefinition(
							AnnotationTransactionAttributeSource.class);
					// TODO kriswang Zhu what's mean
					// annotationTransactionAttributeSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE,
							annotationTransactionAttributeSource);

					// 事务拦截器
					RootBeanDefinition transactionInterceptor = new RootBeanDefinition(TransactionInterceptor.class);
					transactionInterceptor.getPropertyValues().add(TRANSACTION_MANAGER_BEAN_NAME,
							dataSourceMetaVO.getTransactionManager());
					transactionInterceptor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE,
							new RuntimeBeanReference(TRANSACTION_ATTRIBUTE_SOURCE));
					// transactionInterceptor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_INTERCEPTOR, transactionInterceptor);

					// transactionAttributeSourceAdvisor
					RootBeanDefinition transactionAttributeSourceAdvisor = new RootBeanDefinition(
							BeanFactoryTransactionAttributeSourceAdvisor.class);
					transactionAttributeSourceAdvisor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE,
							new RuntimeBeanReference(TRANSACTION_ATTRIBUTE_SOURCE));
					transactionAttributeSourceAdvisor.getPropertyValues()
							.add(ADVICE_BEAN_NAME, TRANSACTION_INTERCEPTOR);
					// transactionAttributeSourceAdvisor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
					// String txAdvisorBeanName = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR,
							transactionAttributeSourceAdvisor);

					RootBeanDefinition beanDefinition = new RootBeanDefinition(
							AnnotationAwareAspectJAutoProxyCreator.class);
					// beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
					// beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
					parserContext.getRegistry().registerBeanDefinition(ANNOTATION_AWARE_ASPECTJ_AUTO_PROXY_CREATOR,
							beanDefinition);
				}
			}

			TableShardingMetaVO tableShardingMetaVO = dataSourceMetaVO.getTableShardingMetaVO();
			if (tableShardingMetaVO != null) {
				// 分表拦截器
				RootBeanDefinition tableShardingDataSourceInterceptor = new RootBeanDefinition(
						TableShardingDataSourceInterceptor.class);
				tableShardingDataSourceInterceptor.getPropertyValues().add(TABLE_SHARDING_STRATEGIES,
						scanTableStrategies(scan, tableShardingMetaVO.getStrategiesPackage()));
				parserContext.getRegistry().registerBeanDefinition(TABLE_SHARDING_DATASOURCE_INTERCEPTOR,
						tableShardingDataSourceInterceptor);

				// repositoryBeanNameAutoProxyCreator
				List<String> interceptorNames = new ArrayList<String>();
				interceptorNames.add(TABLE_SHARDING_DATASOURCE_INTERCEPTOR);
				RootBeanDefinition repositoryBeanNameAutoProxyCreator = new RootBeanDefinition(
						BeanNameAutoProxyCreator.class);
				repositoryBeanNameAutoProxyCreator.getPropertyValues().add(BEAN_NAMES,
						tableShardingMetaVO.getBeanNames());
				repositoryBeanNameAutoProxyCreator.getPropertyValues().add(INTERCEPTOR_NAMES, interceptorNames);
				parserContext.getRegistry().registerBeanDefinition(REPOSITORY_BEAN_NAME_AUTO_PROXY_CREATOR,
						repositoryBeanNameAutoProxyCreator);

				// 分表插件
				RootBeanDefinition shardingPlugin = new RootBeanDefinition(ShardingPlugin.class);
				shardingPlugin.getPropertyValues().add(SQL_CONVERTER,
						new BeanDefinitionHolder(new RootBeanDefinition(DefaultSqlConverter.class), SQL_CONVERTER));
				parserContext.getRegistry().registerBeanDefinition(SHARDING_PLUGIN, shardingPlugin);

				// 注入分表插件
				if (parserContext.getRegistry().containsBeanDefinition(dataSourceMetaVO.getMyBatisSqlSessionFactory())) {
					BeanDefinition sqlSessionFactory = parserContext.getRegistry().getBeanDefinition(
							dataSourceMetaVO.getMyBatisSqlSessionFactory());
					ManagedArray plugins = (ManagedArray) sqlSessionFactory.getPropertyValues().get(PLUGINS);
					if (plugins == null) {
						plugins = new ManagedArray(Interceptor.class.getName(), 1);
						sqlSessionFactory.getPropertyValues().add(PLUGINS, plugins);
					}
					plugins.add(0, new RuntimeBeanReference(SHARDING_PLUGIN));
				}
			}
		}
	}

	/**
	 * 扫描分表策略
	 */
	private ManagedMap<String, BeanDefinitionHolder> scanTableStrategies(StrategiesClassPathBeanDefinitionScanner scan,
			String strategiesPackage) {
		ManagedMap<String, BeanDefinitionHolder> strategyMap = new ManagedMap<String, BeanDefinitionHolder>();

		Set<BeanDefinitionHolder> strategySet = scan.doScan(strategiesPackage);
		for (Iterator<BeanDefinitionHolder> it = strategySet.iterator(); it.hasNext();) {
			BeanDefinitionHolder beanDefinitionHolder = it.next();
			Class<?> clazz = null;
			try {
				// TODO kriswang Zhu 下面方式不太好
				clazz = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
			} catch (ClassNotFoundException e) {
				LOGGER.error(e.getMessage());
				continue;
			}
			if (clazz != null) {
				Annotation annotation = clazz.getAnnotation(Strategy.class);
				if (annotation != null) {
					strategyMap.put(((Strategy) annotation).value(), beanDefinitionHolder);
				}
			}
		}
		return strategyMap;
	}

	/**
	 * 扫描分库策略
	 */
	private ManagedMap<String, BeanDefinitionHolder> scanRepositoryStrategies(
			StrategiesClassPathBeanDefinitionScanner scan, String strategiesPackage, String defaultDataSource) {
		ManagedMap<String, BeanDefinitionHolder> strategyMap = new ManagedMap<String, BeanDefinitionHolder>();

		Set<BeanDefinitionHolder> strategySet = scan.doScan(strategiesPackage);
		for (Iterator<BeanDefinitionHolder> it = strategySet.iterator(); it.hasNext();) {
			BeanDefinitionHolder beanDefinitionHolder = it.next();
			Class<?> clazz = null;
			try {
				// TODO kriswang Zhu 下面方式不太好
				BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
				// beanDefinition.getPropertyValues().add(DEFAULT_DATASOURCE, defaultDataSource);
				clazz = Class.forName(beanDefinition.getBeanClassName());
			} catch (ClassNotFoundException e) {
				LOGGER.error(e.getMessage());
				continue;
			}
			if (clazz != null) {
				Annotation annotation = clazz.getAnnotation(Strategy.class);
				if (annotation != null) {
					strategyMap.put(((Strategy) annotation).value(), beanDefinitionHolder);
				}
			}
		}
		return strategyMap;
	}

	/**
	 * 解析数据源
	 */
	private DataSourceMetaVO parseDataSource(Element element) {
		DataSourceMetaVO dataSourceMetaVO = new DataSourceMetaVO();
		dataSourceMetaVO.setId(element.getAttribute(XSD_ID));
		if (StringUtils.isNotBlank(element.getAttribute(XSD_DS_TYPE))) {
			dataSourceMetaVO.setDsType(element.getAttribute(XSD_DS_TYPE));
		}

		String dbType = element.getAttribute(XSD_DB_TYPE);
		if (StringUtils.isNotBlank(dbType)) {
			dataSourceMetaVO.setDbType(dbType);
		}
		if (StringUtils.isNotBlank(element.getAttribute(XSD_TRANSACTION_MANAGER))) {
			dataSourceMetaVO.setTransactionManager(element.getAttribute(XSD_TRANSACTION_MANAGER));
		}
		if (StringUtils.isNotBlank(element.getAttribute(XSD_MYBATIS_SQL_SESSION_FACTORY))) {
			dataSourceMetaVO.setMyBatisSqlSessionFactory(element.getAttribute(XSD_MYBATIS_SQL_SESSION_FACTORY));
		}

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_PROPERTY)) {
						dataSourceMetaVO.addProperty(ele.getAttribute(XSD_NAME), ele.getAttribute(XSD_VALUE));
					} else if (ele.getTagName().endsWith(XSD_READ_WRITE_DATASOURCE)) {
						dataSourceMetaVO.addReadWriteDataSourceMetaVO(parseReadWriteDataSource(ele, dbType));
					} else if (ele.getTagName().endsWith(XSD_TABLE_SHARDING)) {
						dataSourceMetaVO.setTableShardingMetaVO(parseTableSharding(ele));
					} else if (ele.getTagName().endsWith(XSD_REPOSITORY_SHARDING)) {
						dataSourceMetaVO.setRepositoryShardingMetaVO(parseRepositorySharding(ele));
					}
				}
			}
		}

		return dataSourceMetaVO;
	}

	/**
	 * 解析读写数据源
	 */
	private ReadWriteDataSourceMetaVO parseReadWriteDataSource(Element element, String dbType) {
		ReadWriteDataSourceMetaVO readWriteDataSourceMetaVO = new ReadWriteDataSourceMetaVO();
		readWriteDataSourceMetaVO.setName(element.getAttribute(XSD_NAME));
		if (StringUtils.isNotBlank(element.getAttribute(XSD_LOAD_BALANCE))) {
			readWriteDataSourceMetaVO.setLoadBalance(element.getAttribute(XSD_LOAD_BALANCE));
		}
		if (StringUtils.isNotBlank(element.getAttribute(XSD_WEIGHT))) {
			readWriteDataSourceMetaVO.setWeight(element.getAttribute(XSD_WEIGHT));
		}

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_WRITE_DATASOURCE)) {
						readWriteDataSourceMetaVO.setWriteDataSourceMetaVO(parseAtomDataSource(ele, dbType));
					} else if (ele.getTagName().endsWith(XSD_READ_DATASOURCE)) {
						readWriteDataSourceMetaVO.addReadDataSourceMetaVO(parseAtomDataSource(ele, dbType));
					}
				}
			}
		}

		return readWriteDataSourceMetaVO;
	}

	/**
	 * 解析原子数据源
	 */
	private AtomDataSourceMetaVO parseAtomDataSource(Element element, String dbType) {
		AtomDataSourceMetaVO atomDataSourceMetaVO = new AtomDataSourceMetaVO();
		String logicname = element.getAttribute(XSD_LOGIC_NAME);
		atomDataSourceMetaVO.setLogicName(logicname);

		logicnameMap.put(logicname, dbType);

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					atomDataSourceMetaVO.addProperty(ele.getAttribute(XSD_NAME), ele.getAttribute(XSD_VALUE));
				}
			}
		}

		return atomDataSourceMetaVO;
	}

	/**
	 * 解析分表配置
	 */
	private TableShardingMetaVO parseTableSharding(Element element) {
		TableShardingMetaVO tableShardingMetaVO = new TableShardingMetaVO();
		tableShardingMetaVO.setStrategiesPackage(element.getAttribute(XSD_STRATEGIES_PACKAGE));

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					tableShardingMetaVO.addBeanName(ele.getAttribute(XSD_VALUE));
				}
			}
		}

		return tableShardingMetaVO;
	}

	/**
	 * 解析分库配置
	 */
	private RepositoryShardingMetaVO parseRepositorySharding(Element element) {
		RepositoryShardingMetaVO repositoryShardingMetaVO = new RepositoryShardingMetaVO();
		repositoryShardingMetaVO.setStrategiesPackage(element.getAttribute(XSD_STRATEGIES_PACKAGE));

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					repositoryShardingMetaVO.addBeanName(ele.getAttribute(XSD_VALUE));
				}
			}
		}

		return repositoryShardingMetaVO;
	}
}
