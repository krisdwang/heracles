package heracles.data.spring;

import heracles.core.context.property.PropertyHolder;
import heracles.core.zookeeper.PropertiesUtils;
import heracles.core.zookeeper.ZookeeperClient;
import heracles.core.zookeeper.ZookeeperClientFactory;
import heracles.data.common.annotation.Strategy;
import heracles.data.common.model.AtomModel;
import heracles.data.common.model.GroupModel;
import heracles.data.common.model.MatrixDatasourceModel;
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
import heracles.data.spring.vo.MatrixPoolConfigMetaVO;
import heracles.data.spring.vo.MatrixPoolConfigsMetaVO;
import heracles.data.spring.vo.ReadWriteDataSourceMetaVO;
import heracles.data.spring.vo.RepositoryShardingMetaVO;
import heracles.data.spring.vo.TableShardingMetaVO;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSON;

public class MatrixDatasourceBeanDefinitionParser implements BeanDefinitionParser, Constants {

	private static final Logger log = LoggerFactory.getLogger(MatrixDatasourceBeanDefinitionParser.class);

	private static String matrixDataKey;

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		/**
		 * init all props
		 */
		PropertiesUtils.getPropsConfig(element);

		String matrixName = element.getAttribute(XSD_MATRIX_NAME);
		Assert.notNull(matrixName);
		buidMatrixDataKey(matrixName);

		/**
		 * 读取zk上的matrix配置到PropertyHolder
		 */
		loadRemoteMatrixData();

		MatrixDatasourceModel matrixDatasourceModel = JSON.parseObject(
				PropertyHolder.getProperties().getProperty(matrixDataKey), MatrixDatasourceModel.class);

		DataSourceMetaVO dataSourceMetaVO = parseDataSource(element, matrixDatasourceModel);
		processRWDataSourceMetaVOs(element, parserContext, dataSourceMetaVO);

		return null;
	}

	private void processRWDataSourceMetaVOs(Element element, ParserContext parserContext,
			DataSourceMetaVO dataSourceMetaVO) {
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
						Utils.getDbLoginPropertiesFromAtom(writeDataSourceMetaVO.getAtomModel(),
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
								Utils.getDbLoginPropertiesFromAtom(readDataSourceMetaVO.getAtomModel(),
										dataSourceMetaVO.getDsType()));
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

			Document appDoc = element.getOwnerDocument();
			BeanDefinitionParserDelegate beanDefDelegate = parserContext.getDelegate();

			RepositoryShardingMetaVO repositoryShardingMetaVO = dataSourceMetaVO.getRepositoryShardingMetaVO();
			if (repositoryShardingMetaVO != null) {
				// 分库拦截器
				if (StringUtils.isNotBlank(repositoryShardingMetaVO.getStrategiesPackage())) {
					RootBeanDefinition repositoryShardingDataSourceInterceptor = new RootBeanDefinition(
							RepositoryShardingDataSourceInterceptor.class);
					ManagedMap<String, BeanDefinitionHolder> repositoryStrategiesManagedMap = scanRepositoryStrategies(
							scan, repositoryShardingMetaVO.getStrategiesPackage(), defaultReadWrite);
					repositoryShardingDataSourceInterceptor.getPropertyValues().add(REPOSITORY_SHARDING_STRATEGIES,
							repositoryStrategiesManagedMap);
					parserContext.getRegistry().registerBeanDefinition(REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR,
							repositoryShardingDataSourceInterceptor);

					// 分库aop:config
					beanDefDelegate.parseCustomElement(buildRepositoryShardingAopConfigElmt(appDoc,
							repositoryShardingMetaVO));
				}

				RootBeanDefinition annotationReadWriteDataSourceInterceptor = new RootBeanDefinition(
						AnnotationReadWriteDataSourceInterceptor.class);
				annotationReadWriteDataSourceInterceptor.getPropertyValues().add(READ_WRITE_DATASOURCE_KEYS, dsKeyMap);
				parserContext.getRegistry().registerBeanDefinition(ANNOTATION_READ_WRITE_DATASOURCE_INTERCEPTOR,
						annotationReadWriteDataSourceInterceptor);

				// 读写分离aop:config
				beanDefDelegate.parseCustomElement(buildAnnotationReadWriteAopConfigElmt(appDoc));

				if (StringUtils.isNotBlank(dataSourceMetaVO.getTransactionManager())) {
					// annotationTransactionAttributeSource
					RootBeanDefinition annotationTransactionAttributeSource = new RootBeanDefinition(
							AnnotationTransactionAttributeSource.class);
					annotationTransactionAttributeSource.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE,
							annotationTransactionAttributeSource);

					// 事务拦截器
					RootBeanDefinition transactionInterceptor = new RootBeanDefinition(TransactionInterceptor.class);
					transactionInterceptor.getPropertyValues().add(TRANSACTION_MANAGER,
							new RuntimeBeanReference(dataSourceMetaVO.getTransactionManager()));
					transactionInterceptor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE,
							new RuntimeBeanReference(TRANSACTION_ATTRIBUTE_SOURCE));
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_INTERCEPTOR, transactionInterceptor);

					Object eleSource = parserContext.extractSource(element);

					// transactionAttributeSourceAdvisor
					RootBeanDefinition transactionAttributeSourceAdvisor = new RootBeanDefinition(
							TransactionAttributeSourceAdvisor.class);
					transactionAttributeSourceAdvisor.getPropertyValues().add(TRANSACTION_INTERCEPTOR,
							new RuntimeBeanReference(TRANSACTION_INTERCEPTOR));
					transactionAttributeSourceAdvisor.setSource(eleSource);
					transactionAttributeSourceAdvisor.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
					transactionAttributeSourceAdvisor.getPropertyValues().add(XSD_MATRIX_ORDER,
							TRANSACTION_ADVISOR_ORDER);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR,
							transactionAttributeSourceAdvisor);

					String sourceName = parserContext.getReaderContext().registerWithGeneratedName(
							annotationTransactionAttributeSource);
					CompositeComponentDefinition transcationCompositeDef = new CompositeComponentDefinition(
							element.getTagName(), eleSource);
					transcationCompositeDef.addNestedComponent(new BeanComponentDefinition(
							annotationTransactionAttributeSource, sourceName));
					transcationCompositeDef.addNestedComponent(new BeanComponentDefinition(transactionInterceptor,
							TRANSACTION_INTERCEPTOR));
					transcationCompositeDef.addNestedComponent(new BeanComponentDefinition(
							transactionAttributeSourceAdvisor, TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR));
					parserContext.registerComponent(transcationCompositeDef);
				}
			} else {
				if (StringUtils.isNotBlank(dataSourceMetaVO.getTransactionManager())) {
					RootBeanDefinition annotationTransactionAttributeSource = new RootBeanDefinition(
							AnnotationTransactionAttributeSource.class);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE,
							annotationTransactionAttributeSource);

					// 事务拦截器
					RootBeanDefinition transactionInterceptor = new RootBeanDefinition(TransactionInterceptor.class);
					transactionInterceptor.getPropertyValues().add(TRANSACTION_MANAGER_BEAN_NAME,
							dataSourceMetaVO.getTransactionManager());
					transactionInterceptor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE,
							new RuntimeBeanReference(TRANSACTION_ATTRIBUTE_SOURCE));
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_INTERCEPTOR, transactionInterceptor);

					RootBeanDefinition transactionAttributeSourceAdvisor = new RootBeanDefinition(
							BeanFactoryTransactionAttributeSourceAdvisor.class);
					transactionAttributeSourceAdvisor.getPropertyValues().add(TRANSACTION_ATTRIBUTE_SOURCE,
							new RuntimeBeanReference(TRANSACTION_ATTRIBUTE_SOURCE));
					transactionAttributeSourceAdvisor.getPropertyValues()
							.add(ADVICE_BEAN_NAME, TRANSACTION_INTERCEPTOR);
					parserContext.getRegistry().registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR,
							transactionAttributeSourceAdvisor);

					RootBeanDefinition beanDefinition = new RootBeanDefinition(
							AnnotationAwareAspectJAutoProxyCreator.class);
					parserContext.getRegistry().registerBeanDefinition(ANNOTATION_AWARE_ASPECTJ_AUTO_PROXY_CREATOR,
							beanDefinition);
				}
			}

			TableShardingMetaVO tableShardingMetaVO = dataSourceMetaVO.getTableShardingMetaVO();
			if (tableShardingMetaVO != null) {
				// 分表拦截器
				RootBeanDefinition tableShardingDataSourceInterceptor = new RootBeanDefinition(
						TableShardingDataSourceInterceptor.class);
				ManagedMap<String, BeanDefinitionHolder> tableStrategies = scanTableStrategies(scan,
						tableShardingMetaVO.getStrategiesPackage());
				tableShardingDataSourceInterceptor.getPropertyValues().add(TABLE_SHARDING_STRATEGIES, tableStrategies);
				parserContext.getRegistry().registerBeanDefinition(TABLE_SHARDING_DATASOURCE_INTERCEPTOR,
						tableShardingDataSourceInterceptor);

				// 读写分离aop:config
				beanDefDelegate.parseCustomElement(buildTableShardingAopConfigElmt(appDoc, tableShardingMetaVO));

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
	 * 读zookeeper上matrix的配置
	 * 
	 * @param matrixConfigZkPath String
	 */
	private static void loadRemoteMatrixData() {
		String matrixConfigZkPath = getMatrixZkPath();

		String data = "";
		ZookeeperClient zkClient = null;
		try {
			zkClient = (ZookeeperClient) ZookeeperClientFactory.getClient(DEFAULT_ZKClIENT_NAME, matrixConfigZkPath,
					PropertyHolder.getProperties());
			data = zkClient.findChildData(matrixConfigZkPath);
			log.info("matrix cfgcenter [" + matrixConfigZkPath + ", data.length:" + data.length() + "]");
		} catch (Exception e) {
			log.error("loadRemoteMatrixData fail [" + e.getMessage() + "]");
		} finally {
			if (null != zkClient) {
				zkClient.closeClient();
			}
		}

		loadMatrixAndBackup(matrixConfigZkPath, data);
	}

	private static void buidMatrixDataKey(String matrixName) {
		matrixDataKey = RESOURCE_RDBMS_MATRIX_PREFIX + "/" + matrixName;
	}

	private static String getMatrixZkPath() {
		String zkPartition = PropertyHolder.getProperties().getProperty(ZOOKEEPER_DEFAULT_PARTITION_KEY);
		String matrixConfigZkPath = "/" + (zkPartition != null ? zkPartition : ZOOKEEPER_PARTITION_DEFAULT)
				+ matrixDataKey;
		return matrixConfigZkPath;
	}

	/**
	 * load zk data, load backup file,write backup file
	 * 
	 * @param matrixConfigZkPath
	 * @param data
	 */
	private static void loadMatrixAndBackup(String matrixConfigZkPath, String data) {
		try {
			String configurationBackupPath = (String) PropertyHolder.getProperties().get(CONFIGURATION_BACKUP_PATH);
			if (StringUtils.isNotBlank(configurationBackupPath)) {
				String backupFilePath = configurationBackupPath + matrixConfigZkPath;
				try {
					/**
					 * load backup file
					 */
					String backupData = Utils.readFile(new File(backupFilePath));
					Properties properties = new Properties();
					properties.setProperty(matrixDataKey, backupData);
					PropertyHolder.addProperties(properties);
				} catch (IOException ex) {
					if (log.isWarnEnabled()) {
						log.warn("Could not load data from " + backupFilePath + ": " + ex.getMessage());
					}
					FileUtils.touch(new File(backupFilePath));
				}

				if (StringUtils.isNotBlank(data)) {
					/**
					 * write backup file
					 */
					Utils.writeFile(new File(backupFilePath), data);
				}
			}

			if (StringUtils.isNotBlank(data)) {
				/**
				 * load current zk data
				 */
				Properties properties = new Properties();
				properties.setProperty(matrixDataKey, data);
				PropertyHolder.addProperties(properties);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * build TableSharding aop config element
	 * 
	 * @param appDoc
	 * @param tableShardingMetaVO
	 * @return
	 */
	private Element buildTableShardingAopConfigElmt(Document appDoc, TableShardingMetaVO tableShardingMetaVO) {
		Element tableShardingAopConfigElmt = appDoc.createElementNS(AOP_NAMESPACE_URI, CONFIG);

		Element tableShardingPointcutChild = appDoc.createElementNS(AOP_NAMESPACE_URI, POINTCUT);
		tableShardingPointcutChild.setAttribute(XSD_ID, TABLE_SHARDING_DATA_SOURCE_POINTCUT);
		tableShardingPointcutChild.setAttribute(EXPRESSION, TABLE_SHARDING_POINTCUT_EXPRESSION);
		tableShardingAopConfigElmt.appendChild(tableShardingPointcutChild);

		Element tableShardingAdvisorChild = appDoc.createElementNS(AOP_NAMESPACE_URI, ADVISOR);
		tableShardingAdvisorChild.setAttribute(POINTCUT_REF, TABLE_SHARDING_DATA_SOURCE_POINTCUT);
		tableShardingAdvisorChild.setAttribute(ADVICE_REF, TABLE_SHARDING_DATASOURCE_INTERCEPTOR);
		tableShardingAdvisorChild.setAttribute(XSD_MATRIX_ORDER, TABLE_SHARDING_DATA_SOURCE_POINTCUT_ORDER);
		tableShardingAopConfigElmt.appendChild(tableShardingAdvisorChild);
		return tableShardingAopConfigElmt;
	}

	/**
	 * build RepositorySharding aop config element
	 * 
	 * @param appDoc
	 * @param repositoryShardingMetaVO
	 * @return
	 */
	private Element buildRepositoryShardingAopConfigElmt(Document appDoc,
			RepositoryShardingMetaVO repositoryShardingMetaVO) {
		Element repositoryShardingAopConfigElmt = appDoc.createElementNS(AOP_NAMESPACE_URI, CONFIG);

		Element repositoryShardingPointcutChild = appDoc.createElementNS(AOP_NAMESPACE_URI, POINTCUT);
		repositoryShardingPointcutChild.setAttribute(XSD_ID, REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT);
		repositoryShardingPointcutChild.setAttribute(EXPRESSION, REPOSITORY_SHARDING_POINTCUT_EXPRESSION);
		repositoryShardingAopConfigElmt.appendChild(repositoryShardingPointcutChild);

		Element repositoryShardingAdvisorChild = appDoc.createElementNS(AOP_NAMESPACE_URI, ADVISOR);
		repositoryShardingAdvisorChild.setAttribute(POINTCUT_REF, REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT);
		repositoryShardingAdvisorChild.setAttribute(ADVICE_REF, REPOSITORY_SHARDING_DATASOURCE_INTERCEPTOR);
		repositoryShardingAdvisorChild.setAttribute(XSD_MATRIX_ORDER, REPOSITORY_SHARDING_DATA_SOURCE_POINTCUT_ORDER);
		repositoryShardingAopConfigElmt.appendChild(repositoryShardingAdvisorChild);
		return repositoryShardingAopConfigElmt;
	}

	/**
	 * build annotationReadWrite AopConfig Elmt
	 * 
	 * @param appDoc
	 * @return
	 */
	private Element buildAnnotationReadWriteAopConfigElmt(Document appDoc) {
		Element annotationReadWriteAopConfigElmt = appDoc.createElementNS(AOP_NAMESPACE_URI, CONFIG);

		Element annotationReadWritePointcutChild = appDoc.createElementNS(AOP_NAMESPACE_URI, POINTCUT);
		annotationReadWritePointcutChild.setAttribute(XSD_ID, ANNOTATION_READ_WRITE_DATA_SOURCE_POINTCUT);
		annotationReadWritePointcutChild.setAttribute(EXPRESSION, READWRITE_POINTCUT_EXPRESSION);
		annotationReadWriteAopConfigElmt.appendChild(annotationReadWritePointcutChild);

		Element annotationReadWriteAdvisorChild = appDoc.createElementNS(AOP_NAMESPACE_URI, ADVISOR);
		annotationReadWriteAdvisorChild.setAttribute(POINTCUT_REF, ANNOTATION_READ_WRITE_DATA_SOURCE_POINTCUT);
		annotationReadWriteAdvisorChild.setAttribute(ADVICE_REF, ANNOTATION_READ_WRITE_DATASOURCE_INTERCEPTOR);
		annotationReadWriteAdvisorChild.setAttribute(XSD_MATRIX_ORDER, ANNOTATION_READWRITE_POINTCUT_ORDER);
		annotationReadWriteAopConfigElmt.appendChild(annotationReadWriteAdvisorChild);
		return annotationReadWriteAopConfigElmt;
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
				// TODO kriswang 下面方式不太好
				clazz = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage());
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
				// TODO kriswang 下面方式不太好
				BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
				clazz = Class.forName(beanDefinition.getBeanClassName());
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage());
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
	private DataSourceMetaVO parseDataSource(Element matrixElement, MatrixDatasourceModel matrixDatasourceModel) {
		DataSourceMetaVO dataSourceMetaVO = new DataSourceMetaVO();
		dataSourceMetaVO.setId(matrixElement.getAttribute(XSD_MATRIX_NAME));
		dataSourceMetaVO.setDbType(matrixDatasourceModel.getType());
		if (StringUtils.isNotBlank(matrixElement.getAttribute(XSD_TRANSACTION_MANAGER))) {
			dataSourceMetaVO.setTransactionManager(matrixElement.getAttribute(XSD_TRANSACTION_MANAGER));
		}
		if (StringUtils.isNotBlank(matrixElement.getAttribute(XSD_MYBATIS_SQL_SESSION_FACTORY))) {
			dataSourceMetaVO.setMyBatisSqlSessionFactory(matrixElement.getAttribute(XSD_MYBATIS_SQL_SESSION_FACTORY));
		}

		NodeList nodeList = matrixElement.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_PROPERTY)) {
						dataSourceMetaVO.addProperty(ele.getAttribute(XSD_NAME), ele.getAttribute(XSD_VALUE));
					} else if (ele.getTagName().endsWith(XSD_MATRIX_POOL_CONFIGS)) {
						MatrixPoolConfigsMetaVO matrixPoolConfigsMetaVO = parseMatrixPoolConfigsMetaVo(ele);
						dataSourceMetaVO.setDsType(matrixPoolConfigsMetaVO.getPoolType());

						Map<String, MatrixPoolConfigMetaVO> atomPoolConfigMap = getAtomPoolConfigMap(matrixPoolConfigsMetaVO);
						List<ReadWriteDataSourceMetaVO> readWriteDataSource = parseReadWriteDataSource(
								matrixDatasourceModel, atomPoolConfigMap);
						dataSourceMetaVO.addReadWriteDataSourceMetaVOList(readWriteDataSource);
					} else if (ele.getTagName().endsWith(XSD_MATRIX_TABLE_SHARDING)) {
						dataSourceMetaVO.setTableShardingMetaVO(parseTableSharding(ele));
					} else if (ele.getTagName().endsWith(XSD_MATRIX_REPOSITORY_SHARDING)) {
						dataSourceMetaVO.setRepositoryShardingMetaVO(parseRepositorySharding(ele));
					}
				}
			}

			// 未配置pool-configs，加入默认读写datasource
			if (CollectionUtils.isEmpty(dataSourceMetaVO.getReadWriteDataSourceMetaVOs())) {
				dataSourceMetaVO.addReadWriteDataSourceMetaVOList(parseReadWriteDataSource(matrixDatasourceModel,
						new HashMap<String, MatrixPoolConfigMetaVO>()));
			}

		}

		return dataSourceMetaVO;
	}

	/**
	 * 解析读写数据源
	 */
	private List<ReadWriteDataSourceMetaVO> parseReadWriteDataSource(MatrixDatasourceModel matrixDatasourceModel,
			Map<String, MatrixPoolConfigMetaVO> atomPoolConfigMap) {
		List<ReadWriteDataSourceMetaVO> list = new ArrayList<ReadWriteDataSourceMetaVO>();

		List<GroupModel> groups = matrixDatasourceModel.getGroups();
		if (groups != null && groups.size() > 0) {
			for (GroupModel groupModel : groups) {
				ReadWriteDataSourceMetaVO readWriteDataSourceMetaVO = new ReadWriteDataSourceMetaVO();
				readWriteDataSourceMetaVO.setName(groupModel.getGroupName());
				readWriteDataSourceMetaVO.setLoadBalance(groupModel.getLoadBalance());

				List<AtomModel> atoms = groupModel.getAtoms();
				for (AtomModel atomModel : atoms) {
					AtomDataSourceMetaVO atomDataSourceMetaVO = new AtomDataSourceMetaVO();
					atomDataSourceMetaVO.setLogicName(atomModel.getAtomName());
					atomDataSourceMetaVO.setAtomModel(atomModel);

					MatrixPoolConfigMetaVO matrixPoolConfigMetaVO = getMatchPoolConfig(atomPoolConfigMap,
							atomModel.getAtomName());
					if (matrixPoolConfigMetaVO != null) {
						// add pool config properties to AtomDataSource properties
						atomDataSourceMetaVO.addProperties(matrixPoolConfigMetaVO.getProperties());
					}

					if (atomModel.getIsMaster()) {
						readWriteDataSourceMetaVO.setWriteDataSourceMetaVO(atomDataSourceMetaVO);
					} else {
						readWriteDataSourceMetaVO.addReadDataSourceMetaVO(atomDataSourceMetaVO);
					}
				}

				list.add(readWriteDataSourceMetaVO);
			}
		}

		return list;
	}

	private Map<String, MatrixPoolConfigMetaVO> getAtomPoolConfigMap(MatrixPoolConfigsMetaVO matrixPoolConfigsMetaVO) {
		Map<String, MatrixPoolConfigMetaVO> atomPoolConfigMap = new LinkedHashMap<String, MatrixPoolConfigMetaVO>();
		List<MatrixPoolConfigMetaVO> poolConfigMetaVos = matrixPoolConfigsMetaVO.getPoolConfigMetaVos();
		for (MatrixPoolConfigMetaVO matrixPoolConfigMetaVO : poolConfigMetaVos) {
			String[] atomNameArray = matrixPoolConfigMetaVO.getAtomNames().split(",");
			for (String poolConfigAtomName : atomNameArray) {
				atomPoolConfigMap.put(poolConfigAtomName.trim(), matrixPoolConfigMetaVO);
			}
		}
		return atomPoolConfigMap;
	}

	private MatrixPoolConfigMetaVO getMatchPoolConfig(Map<String, MatrixPoolConfigMetaVO> atomPoolConfigMap,
			String atomName) {
		for (Entry<String, MatrixPoolConfigMetaVO> entry : atomPoolConfigMap.entrySet()) {
			if (Utils.isMatch(entry.getKey(), atomName)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 解析pool configs
	 */
	private MatrixPoolConfigsMetaVO parseMatrixPoolConfigsMetaVo(Element element) {
		MatrixPoolConfigsMetaVO matrixPoolConfigMetaVO = new MatrixPoolConfigsMetaVO();
		matrixPoolConfigMetaVO.setPoolType(element.getAttribute(XSD_MATRIX_POOL_TYPE));

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_MATRIX_POOL_CONFIG)) {
						matrixPoolConfigMetaVO.addMatrixPoolConfigMetaVO(parsePoolConfig(ele));
					}
				}
			}
		}

		return matrixPoolConfigMetaVO;
	}

	/**
	 * 解析PoolConfig
	 */
	private MatrixPoolConfigMetaVO parsePoolConfig(Element element) {
		MatrixPoolConfigMetaVO matrixPoolConfigMetaVO = new MatrixPoolConfigMetaVO();
		matrixPoolConfigMetaVO.setAtomNames(element.getAttribute(XSD_MATRIX_ATOM_NAMES));

		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					matrixPoolConfigMetaVO.addProperty(ele.getAttribute(XSD_NAME), ele.getAttribute(XSD_VALUE));
				}
			}
		}

		return matrixPoolConfigMetaVO;
	}

	/**
	 * 解析分表配置
	 */
	private TableShardingMetaVO parseTableSharding(Element element) {
		TableShardingMetaVO tableShardingMetaVO = new TableShardingMetaVO();
		if (StringUtils.isNotBlank(element.getAttribute(XSD_MATRIX_STRATEGIES_PACKAGE))) {
			tableShardingMetaVO.setStrategiesPackage(element.getAttribute(XSD_MATRIX_STRATEGIES_PACKAGE));
		}

		return tableShardingMetaVO;
	}

	/**
	 * 解析分库配置
	 */
	private RepositoryShardingMetaVO parseRepositorySharding(Element element) {
		RepositoryShardingMetaVO repositoryShardingMetaVO = new RepositoryShardingMetaVO();
		if (StringUtils.isNotBlank(element.getAttribute(XSD_MATRIX_STRATEGIES_PACKAGE))) {
			repositoryShardingMetaVO.setStrategiesPackage(element.getAttribute(XSD_MATRIX_STRATEGIES_PACKAGE));
		}

		return repositoryShardingMetaVO;
	}
}
