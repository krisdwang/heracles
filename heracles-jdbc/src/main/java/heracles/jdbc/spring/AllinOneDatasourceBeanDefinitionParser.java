package heracles.jdbc.spring;

import heracles.core.context.property.PropertyHolder;
import heracles.core.zookeeper.PropertiesUtils;
import heracles.core.zookeeper.ZookeeperClient;
import heracles.core.zookeeper.ZookeeperClientFactory;
import heracles.jdbc.atom.AtomDataSource;
import heracles.jdbc.group.GroupDataSource;
import heracles.jdbc.matrix.MatrixDataSource;
import heracles.jdbc.matrix.model.AtomModel;
import heracles.jdbc.matrix.model.GroupModel;
import heracles.jdbc.matrix.model.MatrixDatasourceModel;
import heracles.jdbc.matrix.model.RuleModel;
import heracles.jdbc.spring.util.Constants;
import heracles.jdbc.spring.util.Utils;
import heracles.jdbc.spring.vo.AllInOneAtomDataSourceVO;
import heracles.jdbc.spring.vo.AllInOneDataSourceVO;
import heracles.jdbc.spring.vo.AllInOnePoolConfigVO;
import heracles.jdbc.spring.vo.AllInOnePoolConfigsVO;
import heracles.jdbc.spring.vo.AllInOneReadWriteDataSourceVO;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSON;

public class AllinOneDatasourceBeanDefinitionParser implements BeanDefinitionParser, Constants {

	private static final Logger LOGGER = LoggerFactory.getLogger(AllinOneDatasourceBeanDefinitionParser.class);

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

		AllInOneDataSourceVO dataSourceMetaVO = parseDataSource(element, matrixDatasourceModel);
		buildBeanDefinitions(element, parserContext, dataSourceMetaVO, matrixDatasourceModel);

		return null;
	}

	private void buildBeanDefinitions(Element element, ParserContext parserContext,
			AllInOneDataSourceVO dataSourceMetaVO, MatrixDatasourceModel matrixDatasourceModel) {
		List<AllInOneReadWriteDataSourceVO> readWriteDataSourceMetaVOs = dataSourceMetaVO
				.getReadWriteDataSourceMetaVOs();
		if (CollectionUtils.isNotEmpty(readWriteDataSourceMetaVOs)) {
			ManagedMap<String, RuntimeBeanReference> dsMap = new ManagedMap<String, RuntimeBeanReference>();

			List<RuleModel> rules = matrixDatasourceModel.getRules();
			if (CollectionUtils.isEmpty(rules)) {
				String errMsg = "Rules is empty.";
				LOGGER.error(errMsg);
				parserContext.getReaderContext().error(errMsg, null);
			}

			// rule definition
			ManagedList<Object> ruleRefMgmrs = new ManagedList<Object>(rules.size());
			for (int ruleIndex = 0; ruleIndex < rules.size(); ruleIndex++) {
				RootBeanDefinition ruleDefinition = new RootBeanDefinition(
						heracles.jdbc.matrix.model.RuleModel.class);
				ruleDefinition.getPropertyValues().addPropertyValues(getRuleProps(rules.get(ruleIndex)));
				parserContext.getRegistry().registerBeanDefinition("rule" + ruleIndex, ruleDefinition);

				ruleRefMgmrs.add(new RuntimeBeanReference("rule" + ruleIndex));
			}

			// ruleList definition
			RootBeanDefinition ruleListDefinition = new RootBeanDefinition(
					heracles.jdbc.matrix.model.RuleListModel.class);
			ruleListDefinition.getPropertyValues().addPropertyValue(RULE_LIST, ruleRefMgmrs);
			parserContext.getRegistry().registerBeanDefinition(RULE_LIST, ruleListDefinition);

			for (AllInOneReadWriteDataSourceVO readWriteDataSourceMetaVO : readWriteDataSourceMetaVOs) {
				ManagedMap<String, RuntimeBeanReference> rwdsMap = new ManagedMap<String, RuntimeBeanReference>();

				// 写原子数据源
				AllInOneAtomDataSourceVO writeDataSourceMetaVO = readWriteDataSourceMetaVO.getWriteDataSourceMetaVO();
				RootBeanDefinition writeDataSource = new RootBeanDefinition(dataSourceMetaVO.getDataSourceClass());
				writeDataSource.getPropertyValues().addPropertyValues(dataSourceMetaVO.getProperties());
				writeDataSource.getPropertyValues().addPropertyValues(writeDataSourceMetaVO.getProperties());
				writeDataSource.getPropertyValues().addPropertyValues(
						Utils.getDbLoginPropertiesFromAtom(writeDataSourceMetaVO.getAtomModel(),
								dataSourceMetaVO.getDsType()));
				// 注册写原子数据源
				parserContext.getRegistry().registerBeanDefinition(
						readWriteDataSourceMetaVO.getWriteBeanDefinitionName(), writeDataSource);

				RootBeanDefinition atomWriteDataSource = new RootBeanDefinition(AtomDataSource.class);
				atomWriteDataSource.getPropertyValues().add(TARGET_DATASOURCE,
						new RuntimeBeanReference(readWriteDataSourceMetaVO.getWriteBeanDefinitionName()));
				atomWriteDataSource.getPropertyValues().add(RULES, new RuntimeBeanReference(RULE_LIST));
				parserContext.getRegistry().registerBeanDefinition(
						readWriteDataSourceMetaVO.getAtomWriteBeanDefinitionName(), atomWriteDataSource);

				rwdsMap.put(WRITE, new RuntimeBeanReference(readWriteDataSourceMetaVO.getAtomWriteBeanDefinitionName()));

				// 负载均衡
				RootBeanDefinition loadBalance = new RootBeanDefinition(
						readWriteDataSourceMetaVO.getJdbcLoadBalanceClass());
				loadBalance.getConstructorArgumentValues().addGenericArgumentValue(
						readWriteDataSourceMetaVO.getMatrixLoadBalanceArgs());
				parserContext.getRegistry().registerBeanDefinition(
						readWriteDataSourceMetaVO.getLoadBalanceBeanDefinitionName(), loadBalance);

				// 读原子数据源
				List<AllInOneAtomDataSourceVO> readDataSourceMetaVOs = readWriteDataSourceMetaVO
						.getReadDataSourceMetaVOs();
				if (CollectionUtils.isNotEmpty(readDataSourceMetaVOs)) {
					int i = 1;
					for (AllInOneAtomDataSourceVO readDataSourceMetaVO : readDataSourceMetaVOs) {
						RootBeanDefinition readDataSource = new RootBeanDefinition(
								dataSourceMetaVO.getDataSourceClass());
						readDataSource.getPropertyValues().addPropertyValues(dataSourceMetaVO.getProperties());
						readDataSource.getPropertyValues().addPropertyValues(readDataSourceMetaVO.getProperties());
						readDataSource.getPropertyValues().addPropertyValues(
								Utils.getDbLoginPropertiesFromAtom(readDataSourceMetaVO.getAtomModel(),
										dataSourceMetaVO.getDsType()));

						int readDataSourceIndex = i++;
						String readDataSourceName = readWriteDataSourceMetaVO
								.getReadBeanDefinitionName(readDataSourceIndex);
						parserContext.getRegistry().registerBeanDefinition(readDataSourceName, readDataSource);

						RootBeanDefinition atomReadDataSource = new RootBeanDefinition(AtomDataSource.class);
						atomReadDataSource.getPropertyValues().add(TARGET_DATASOURCE,
								new RuntimeBeanReference(readDataSourceName));
						atomReadDataSource.getPropertyValues().add(RULES, new RuntimeBeanReference(RULE_LIST));
						String atomReadDataSourceName = readWriteDataSourceMetaVO
								.getAtomReadBeanDefinitionName(readDataSourceIndex);
						parserContext.getRegistry().registerBeanDefinition(atomReadDataSourceName, atomReadDataSource);

						rwdsMap.put(getReadBeanDefinitionNameKey(readDataSourceIndex), new RuntimeBeanReference(
								atomReadDataSourceName));
					}

					// GroupDataSource
					RootBeanDefinition readWriteDataSource = new RootBeanDefinition(GroupDataSource.class);
					readWriteDataSource.getPropertyValues().add(TARGET_DATASOURCES, rwdsMap);
					readWriteDataSource.getPropertyValues().add(RULES, new RuntimeBeanReference(RULE_LIST));
					readWriteDataSource.getPropertyValues().add(LB_STRATEGY,
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getLoadBalanceBeanDefinitionName()));
					parserContext.getRegistry().registerBeanDefinition(readWriteDataSourceMetaVO.getName(),
							readWriteDataSource);
					dsMap.put(readWriteDataSourceMetaVO.getName(),
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getName()));
				} else {
					// GroupDataSource
					RootBeanDefinition readWriteDataSource = new RootBeanDefinition(GroupDataSource.class);
					readWriteDataSource.getPropertyValues().add(TARGET_DATASOURCES, rwdsMap);
					readWriteDataSource.getPropertyValues().add(RULES, new RuntimeBeanReference(RULE_LIST));
					readWriteDataSource.getPropertyValues().add(LB_STRATEGY,
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getLoadBalanceBeanDefinitionName()));
					parserContext.getRegistry().registerBeanDefinition(readWriteDataSourceMetaVO.getName(),
							readWriteDataSource);

					dsMap.put(readWriteDataSourceMetaVO.getName(),
							new RuntimeBeanReference(readWriteDataSourceMetaVO.getName()));
				}
			}

			// 数据源
			RootBeanDefinition dataSource = new RootBeanDefinition(MatrixDataSource.class);
			dataSource.getPropertyValues().add(DATASOURCES, dsMap);
			dataSource.getPropertyValues().add(RULES, new RuntimeBeanReference(RULE_LIST));
			parserContext.getRegistry().registerBeanDefinition(DEFAULT_DATASOURCE_ID, dataSource);
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
			LOGGER.info("matrix cfgcenter [" + matrixConfigZkPath + ", data.length:" + data.length() + "]");
		} catch (Exception e) {
			LOGGER.error("loadRemoteMatrixData fail [" + e.getMessage() + "]");
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
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("Could not load data from " + backupFilePath + ": " + ex.getMessage());
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
	 * 解析数据源
	 */
	private AllInOneDataSourceVO parseDataSource(Element allInOneElement, MatrixDatasourceModel matrixDatasourceModel) {
		AllInOneDataSourceVO dataSourceMetaVO = new AllInOneDataSourceVO();
		dataSourceMetaVO.setId(allInOneElement.getAttribute(XSD_MATRIX_NAME));
		dataSourceMetaVO.setDbType(matrixDatasourceModel.getType());

		NodeList nodeList = allInOneElement.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith(XSD_PROPERTY)) {
						dataSourceMetaVO.addProperty(ele.getAttribute(XSD_NAME), ele.getAttribute(XSD_VALUE));
					} else if (ele.getTagName().endsWith(XSD_MATRIX_POOL_CONFIGS)) {
						AllInOnePoolConfigsVO matrixPoolConfigsMetaVO = parseMatrixPoolConfigsMetaVo(ele);
						dataSourceMetaVO.setDsType(matrixPoolConfigsMetaVO.getPoolType());

						Map<String, AllInOnePoolConfigVO> atomPoolConfigMap = getAtomPoolConfigMap(matrixPoolConfigsMetaVO);
						List<AllInOneReadWriteDataSourceVO> readWriteDataSource = parseReadWriteDataSource(
								matrixDatasourceModel, atomPoolConfigMap);
						dataSourceMetaVO.addReadWriteDataSourceMetaVOList(readWriteDataSource);
					}
				}
			}

			// 未配置pool-configs，加入默认读写datasource
			if (CollectionUtils.isEmpty(dataSourceMetaVO.getReadWriteDataSourceMetaVOs())) {
				dataSourceMetaVO.addReadWriteDataSourceMetaVOList(parseReadWriteDataSource(matrixDatasourceModel,
						new HashMap<String, AllInOnePoolConfigVO>()));
			}
		}

		return dataSourceMetaVO;
	}

	/**
	 * 解析读写数据源
	 */
	private List<AllInOneReadWriteDataSourceVO> parseReadWriteDataSource(MatrixDatasourceModel matrixDatasourceModel,
			Map<String, AllInOnePoolConfigVO> atomPoolConfigMap) {
		List<AllInOneReadWriteDataSourceVO> list = new ArrayList<AllInOneReadWriteDataSourceVO>();

		List<GroupModel> groups = matrixDatasourceModel.getGroups();
		if (groups != null && groups.size() > 0) {
			for (GroupModel groupModel : groups) {
				AllInOneReadWriteDataSourceVO readWriteDataSourceMetaVO = new AllInOneReadWriteDataSourceVO();
				readWriteDataSourceMetaVO.setName(groupModel.getGroupName());
				readWriteDataSourceMetaVO.setLoadBalance(groupModel.getLoadBalance());

				List<AtomModel> atoms = groupModel.getAtoms();
				for (AtomModel atomModel : atoms) {
					AllInOneAtomDataSourceVO atomDataSourceMetaVO = new AllInOneAtomDataSourceVO();
					atomDataSourceMetaVO.setAtomModel(atomModel);

					AllInOnePoolConfigVO matrixPoolConfigMetaVO = getMatchPoolConfig(atomPoolConfigMap,
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

	private Map<String, AllInOnePoolConfigVO> getAtomPoolConfigMap(AllInOnePoolConfigsVO matrixPoolConfigsMetaVO) {
		Map<String, AllInOnePoolConfigVO> atomPoolConfigMap = new LinkedHashMap<String, AllInOnePoolConfigVO>();
		List<AllInOnePoolConfigVO> poolConfigMetaVos = matrixPoolConfigsMetaVO.getPoolConfigMetaVos();
		for (AllInOnePoolConfigVO matrixPoolConfigMetaVO : poolConfigMetaVos) {
			String[] atomNameArray = matrixPoolConfigMetaVO.getAtomNames().split(",");
			for (String poolConfigAtomName : atomNameArray) {
				atomPoolConfigMap.put(poolConfigAtomName.trim(), matrixPoolConfigMetaVO);
			}
		}
		return atomPoolConfigMap;
	}

	private AllInOnePoolConfigVO getMatchPoolConfig(Map<String, AllInOnePoolConfigVO> atomPoolConfigMap, String atomName) {
		for (Entry<String, AllInOnePoolConfigVO> entry : atomPoolConfigMap.entrySet()) {
			if (Utils.isMatch(entry.getKey(), atomName)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 解析pool configs
	 */
	private AllInOnePoolConfigsVO parseMatrixPoolConfigsMetaVo(Element element) {
		AllInOnePoolConfigsVO matrixPoolConfigMetaVO = new AllInOnePoolConfigsVO();
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
	private AllInOnePoolConfigVO parsePoolConfig(Element element) {
		AllInOnePoolConfigVO matrixPoolConfigMetaVO = new AllInOnePoolConfigVO();
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

	private static Map<String, String> getRuleProps(RuleModel ruleModel) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(TABLE_NAMES, ruleModel.getTableNames());
		properties.put(GROUP_SHARD_RULE, ruleModel.getGroupShardRule());
		properties.put(GROUP_INDEX, ruleModel.getGroupIndex());
		properties.put(TABLE_SHARD_RULE, ruleModel.getTableShardRule());
		properties.put(TABLE_SUFFIX, ruleModel.getTableSuffix());
		return properties;
	}

	private static String getReadBeanDefinitionNameKey(int i) {
		if (i > 9) {
			return "read" + i;
		} else {
			return "read0" + i;
		}
	}

}
