package heracles.core.zookeeper;

import heracles.core.context.model.CfgdefModel;
import heracles.core.context.model.ItemModel;
import heracles.core.context.model.ItemgroupModel;
import heracles.core.context.property.PropertyHolder;
import heracles.core.context.util.Constants;
import heracles.core.context.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PropertiesUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

	private static final Boolean DEFAULT_IGNORE_UNRESOLVABLE = true;

	/**
	 * location默认值
	 */
	private static final String SYSTEM_CONFIG_DEFAULT = "classpath:/properties/application.properties,classpath:/properties/${spring.profiles.default}/application-${spring.profiles.default}.properties,classpath:/properties/${spring.profiles.active}/application-${spring.profiles.active}.properties,file:${appPropertiesFile}";
	/**
	 * 默认配置
	 */
	private static final String SYSTEM_CONFIG_DEFAULT_LOCATION = "/ApplicationCfgDef.xml";
	/**
	 * 预定义配置
	 */
	private static final String SYSTEM_CONFIG_PREDEFINED_LOCATION = "/ApplicationPredefinedCfgDef.xml";
	/**
	 * 配置中心默认值
	 */
	private static final String SYSTEM_CONFIG_ARTIFACT_PATH = "zk:/${heracles.cfgcenter.partition}/artifact/${cfgdef.group}/${cfgdef.name}/${cfgdef.version}/config";

	private static ZookeeperClient zkClient = null;

	/**
	 * 是否解析过 heracles:context
	 */
	@Setter
	private static volatile boolean haveResloveElement = false;

	/**
	 * zk paths
	 */
	@Getter
	private static List<String> zkPaths = new ArrayList<String>();

	/**
	 * 环境变量和系统变量
	 */
	private static Properties sysProps = new Properties();

	/**
	 * cfg def map
	 */
	private static Map<String, Object> cfgdefMap = new HashMap<String, Object>();

	/**
	 * Predefined CfgDef map
	 */
	private static Map<String, Object> cfgdefPreMap = new HashMap<String, Object>();

	private static PropertyPlaceholderHelper propertyPlaceholderHelper;

	public static Properties getPropsConfig(Element element) {
		try {
			init();
			PropertyHolder.addProperties(getPropsByElement(element));
		} catch (Exception e) {
			LOGGER.error("getPropsConfig error : " + e.getMessage());
		}

		return PropertyHolder.getProperties();
	}

	private static Properties getPropsByElement(Element element) throws IOException {
		Element heraclesContextElement = getHeraclesContextElement(element);
		if (heraclesContextElement != null) {
			if (!haveResloveElement) {
				return resloveElement(heraclesContextElement);
			} else {
				return new Properties();
			}
		} else {
			throw new RuntimeException("");
		}
	}

	private static Properties resloveElement(Element heraclesContextElement) throws IOException {
		haveResloveElement = true;
		Properties props = new Properties();
		Boolean ignoreResourceNotFound = Boolean.valueOf(heraclesContextElement.getAttribute("ignore-resource-not-found"));

		loadPreCfgdefProps(props);

		CfgdefModel cfgdefModel = loadCfgdefPropsl(props);

		String zkClientClassName = StringUtils.hasText(heraclesContextElement.getAttribute("zookeeper-client-class")) ? heraclesContextElement
				.getAttribute("zookeeper-client-class") : ZnodeConstants.DEFAULT_ZKClIENT_NAME;

		String location = heraclesContextElement.getAttribute("location");
		if (!StringUtils.hasLength(location)) {
			location = SYSTEM_CONFIG_DEFAULT;
		}

		location = propertyPlaceholderHelper.replacePlaceholders(location, sysProps);
		String[] locations = StringUtils.tokenizeToStringArray(location,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		if (locations != null) {
			loadLocationProperties(props, ignoreResourceNotFound, locations, zkClientClassName);
		}

		/**
		 * 解析local-override 默认为true
		 */
		Boolean localOverride = Boolean.valueOf(heraclesContextElement.getAttribute("local-override"));

		/**
		 * Load sys props to override
		 */
		if (localOverride) {
			props.putAll(sysProps);
		}

		/**
		 * 解析use-cfgcenter 默认为true
		 */
		Boolean useCfgcenter = Boolean
				.valueOf(StringUtils.hasText(heraclesContextElement.getAttribute("use-cfgcenter")) ? heraclesContextElement
						.getAttribute("use-cfgcenter") : "true");
		if (useCfgcenter) {
			loadCfgCenterProps(props, zkClientClassName, cfgdefModel);
		}

		/**
		 * 检查配置项
		 */
		if (!checkProperties(props)) {
			LOGGER.error("cfgdef not contains all of the key of the properties: ");
		}

		return props;
	}

	/**
	 * 配置中心相关配置
	 * 
	 * @param props
	 * @param zkClientClassName
	 */
	private static void loadCfgCenterProps(Properties props, String zkClientClassName, CfgdefModel cfgdefModel) {
		try {
			/**
			 * 读配置中心配置
			 */
			String artifactPath = replaceDefKey(SYSTEM_CONFIG_ARTIFACT_PATH, cfgdefModel);
			artifactPath = propertyPlaceholderHelper.replacePlaceholders(
					artifactPath.replace(Constants.ZOOKEEPER_PREFIX, Constants.EMPTY_STRING), props);
			artifactPath = artifactPath.replace(Constants.ARTIFACT_ALL_PREFIX + Constants.ZOOKEEPER_PARTITION
					+ Constants.ARTIFACT_ALL_SUFFIX, Constants.ZOOKEEPER_PARTITION_DEFAULT);
			if (StringUtils.hasText(artifactPath)) {
				loadRemoteProperties(props, artifactPath, zkClientClassName);
			}

		} catch (Exception e) {
			LOGGER.error("can not resolve map [" + SYSTEM_CONFIG_ARTIFACT_PATH + ":" + e.getMessage() + "]");
		}
	}

	private static void loadPreCfgdefProps(Properties props) {
		/**
		 * 解析preCfgDef
		 */
		PropertiesUtils.cfgdefPreMap = resolveCfgdefPre();
		props.putAll(PropertiesUtils.cfgdefPreMap);
	}

	private static CfgdefModel loadCfgdefPropsl(Properties props) {
		CfgdefModel cfgdefModel = null;
		try {
			String cfgdef = Utils.getXmlData(SYSTEM_CONFIG_DEFAULT_LOCATION);
			LOGGER.info("cfgdef values [" + cfgdef + "]");
			cfgdefModel = (CfgdefModel) Utils.xmlToBean(CfgdefModel.class, cfgdef);
			if (cfgdefModel != null) {
				PropertiesUtils.cfgdefMap = resolveCfgdefMap(cfgdefModel);
				props.putAll(PropertiesUtils.cfgdefMap);
			}
		} catch (Exception e) {
			LOGGER.error("can not resolve map [" + SYSTEM_CONFIG_DEFAULT_LOCATION + ":" + e.getMessage() + "]");
		}

		return cfgdefModel;
	}

	/**
	 * 读取location中配置的数据
	 * 
	 * @param props
	 * @param ignoreResourceNotFound
	 * @param locations
	 * @param zkClientClassName
	 * @throws IOException
	 */
	private static void loadLocationProperties(Properties props, boolean ignoreResourceNotFound, String[] locations,
			String zkClientClassName) throws IOException {
		for (final String location : locations) {
			if (!StringUtils.hasText(location)) {
				continue;
			}

			if (location.contains(Constants.CLASSPATH_PREFIX)) {
				try {
					PropertiesLoaderUtils.fillProperties(
							props,
							new EncodedResource(new ClassPathResource(location.replace(Constants.CLASSPATH_PREFIX,
									Constants.EMPTY_STRING)), Constants.DEFAULT_ENCODING));
				} catch (IOException ex) {
					if (ignoreResourceNotFound) {
						if (LOGGER.isWarnEnabled()) {
							LOGGER.warn("Could not load properties from " + location + ": " + ex.getMessage());
						}
					} else {
						throw ex;
					}
				}
			} else if (location.contains(Constants.FILE_PREFIX)) {
				try {
					PropertiesLoaderUtils.fillProperties(
							props,
							new EncodedResource(new FileSystemResource(location.replace(Constants.FILE_PREFIX,
									Constants.EMPTY_STRING))));
				} catch (IOException ex) {
					if (ignoreResourceNotFound) {
						if (LOGGER.isWarnEnabled()) {
							LOGGER.warn("Could not load properties from " + location + ": " + ex.getMessage());
						}
					} else {
						throw ex;
					}
				}
			} else if (location.contains(Constants.ZOOKEEPER_PREFIX)) {
				/**
				 * load sys props before zk
				 */
				props.putAll(sysProps);

				String zkPath = propertyPlaceholderHelper.replacePlaceholders(
						location.replace(Constants.ZOOKEEPER_PREFIX, Constants.EMPTY_STRING), props);
				zkPath = zkPath.replace(Constants.ARTIFACT_ALL_PREFIX + Constants.ZOOKEEPER_PARTITION
						+ Constants.ARTIFACT_ALL_SUFFIX, Constants.ZOOKEEPER_PARTITION_DEFAULT);
				if (StringUtils.hasText(zkPath)) {
					loadRemoteProperties(props, zkPath, zkClientClassName);
				}
			}
		}
	}

	/**
	 * 读zookeeper的配置
	 * 
	 * @param props Properties
	 * @param path String
	 * @param zkClientClassName
	 */
	private static void loadRemoteProperties(Properties props, String path, String zkClientClassName) {
		String data = Constants.EMPTY_STRING;
		try {
			addZkPath(path);
			zkClient = (ZookeeperClient) ZookeeperClientFactory.getClient(zkClientClassName, path, props);
			data = zkClient.findChildData(path);
			LOGGER.info("cfgcenter [" + path + ":" + data + "]");
		} catch (Exception e) {
			LOGGER.error("loadRemoteProperties fail [" + e.getMessage() + "]");
		} finally {
			if (null != zkClient) {
				zkClient.closeClient();
			}
		}

		String configurationBackupPath = (String) props.get(Constants.CONFIGURATION_BACKUP_PATH);
		loadZkAndBackup(props, path, data, configurationBackupPath);
	}

	/**
	 * load zk data, load backup file,write backup file
	 * 
	 * @param props
	 * @param path
	 * @param data
	 * @param configurationBackupPath
	 */
	public static void loadZkAndBackup(Properties props, String path, String data, String configurationBackupPath) {
		try {
			Properties currentZKProperties = new Properties();
			if (StringUtils.hasText(data)) {
				currentZKProperties.load(new ByteArrayInputStream(data.getBytes(Constants.DEFAULT_ENCODING)));
			}

			if (StringUtils.hasText(configurationBackupPath)) {
				String backupFilePath = configurationBackupPath + path;
				try {
					/**
					 * load backup file
					 */
					PropertiesLoaderUtils.fillProperties(props, new EncodedResource(new FileSystemResource(
							backupFilePath)));
				} catch (IOException ex) {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("Could not load properties from " + backupFilePath + ": " + ex.getMessage());
					}
					FileUtils.touch(new File(backupFilePath));
				}

				/**
				 * write backup file
				 */
				Utils.writeFile(new File(backupFilePath), Utils.propertiesToString(currentZKProperties));
			}

			/**
			 * load current zk data
			 */
			props.putAll(currentZKProperties);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addZkPath(String path) {
		PropertiesUtils.zkPaths.add(path);
	}

	private static Element getHeraclesContextElement(Element element) {
		Node parentNode = element.getParentNode();
		NodeList childNodes = parentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node != null && node instanceof Element) {
				Element ele = (Element) node;
				if (ele.getTagName().endsWith("property-placeholder")) {
					return ele;
				}
			}
		}
		return null;
	}

	private static Map<String, Object> resolveCfgdefMap(CfgdefModel model) {
		Map<String, Object> cfgdefMap = new HashMap<String, Object>();
		if (CollectionUtils.isNotEmpty(model.getCpsList())) {
			for (ItemgroupModel itemgroup : model.getCpsList()) {
				if (itemgroup != null && CollectionUtils.isNotEmpty(itemgroup.getConfList())) {
					for (ItemModel item : itemgroup.getConfList()) {
						cfgdefMap.put(item.getKey(), item.getValue());
					}
				}
			}
		}
		return cfgdefMap;
	}

	private static Map<String, Object> resolveCfgdefPre() {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			String cfgdefPre = Utils.getXmlData(SYSTEM_CONFIG_PREDEFINED_LOCATION);
			LOGGER.info("cfgdefPre values [" + cfgdefPre + "]");

			CfgdefModel model = (CfgdefModel) Utils.xmlToBean(CfgdefModel.class, cfgdefPre);
			if (model != null && CollectionUtils.isNotEmpty(model.getCpsList())) {
				for (ItemgroupModel itemgroup : model.getCpsList()) {
					if (itemgroup != null && CollectionUtils.isNotEmpty(itemgroup.getConfList())) {
						for (ItemModel item : itemgroup.getConfList()) {
							result.put(item.getKey(), item.getValue());
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("can not resolve map [" + SYSTEM_CONFIG_PREDEFINED_LOCATION + ":" + e.getMessage() + "]");
		}
		return result;
	}

	private static String replaceDefKey(String str, CfgdefModel model) {
		if (model.getPartition() != null) {
			str = str.replace(Constants.ARTIFACT_ALL_PREFIX + Constants.ZOOKEEPER_PARTITION
					+ Constants.ARTIFACT_ALL_SUFFIX, model.getPartition());
		}
		str = str.replace(Constants.ARTIFACT_ALL_PREFIX + Constants.ARTIFACT_GROUP + Constants.ARTIFACT_ALL_SUFFIX,
				model.getGroup() == null ? Constants.EMPTY_STRING : model.getGroup());
		str = str.replace(Constants.ARTIFACT_ALL_PREFIX + Constants.ARTIFACT_NAME + Constants.ARTIFACT_ALL_SUFFIX,
				model.getName() == null ? Constants.EMPTY_STRING : model.getName());
		str = str.replace(Constants.ARTIFACT_ALL_PREFIX + Constants.ARTIFACT_VERSION + Constants.ARTIFACT_ALL_SUFFIX,
				model.getVersion() == null ? Constants.EMPTY_STRING : model.getVersion());
		return str;
	}

	/**
	 * 检测配置项
	 */
	private static Boolean checkProperties(Properties props) {
		Boolean result = true;
		String logInfo = "";

		if (props != null && CollectionUtils.isNotEmpty(props.keySet())) {
			for (Object key : props.keySet()) {
				if (!PropertiesUtils.cfgdefMap.containsKey(key) && !PropertiesUtils.cfgdefPreMap.containsKey(key)
						&& !PropertiesUtils.sysProps.containsKey(key)
						&& !StringUtils.startsWithIgnoreCase(key.toString(), Constants.IGNORE_CHECK_KEY)) {
					logInfo += key.toString() + ",";
					result = false;
				}
			}
		}
		if (StringUtils.hasText(logInfo)) {
			LOGGER.info(logInfo + "no definition in " + Constants.CFG_DEF_FILENAME);
		}

		return result;
	}

	private static void init() {
		if (sysProps.size() == 0) {
			sysProps.putAll(System.getenv());
			sysProps.putAll(System.getProperties());
		}

		propertyPlaceholderHelper = new PropertyPlaceholderHelper(
				PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
				PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
				PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, DEFAULT_IGNORE_UNRESOLVABLE);
	}

}