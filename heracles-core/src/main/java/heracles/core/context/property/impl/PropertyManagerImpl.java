package heracles.core.context.property.impl;

import heracles.core.context.property.PropertyHolder;
import heracles.core.context.property.PropertyManager;
import heracles.core.context.util.Constants;
import heracles.core.exception.InitZKException;
import heracles.core.zookeeper.PropertyChangedHandler;
import heracles.core.zookeeper.ZnodeUtils;
import heracles.core.zookeeper.ZookeeperClient;
import heracles.core.zookeeper.ZookeeperClientFactory;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

public class PropertyManagerImpl implements PropertyManager, InitializingBean {

	private Logger LOGGER = LoggerFactory.getLogger(PropertyManagerImpl.class);

	@Getter
	@Setter
	private List<PropertyChangedHandler> handlers;

	@Getter
	@Setter
	private List<String> paths;

	/**
	 * 备份文件地址
	 */
	@Getter
	@Setter
	private String fileName;

	@Getter
	@Setter
	private Map<String, Object> zkClientMap = new LinkedHashMap<String, Object>();

	@Getter
	@Setter
	private Object defaultZkClient;

	private Object getClient(String path) {
		if (null == defaultZkClient) {
			try {
				defaultZkClient = ZookeeperClientFactory.getDefaultClient();
			} catch (InitZKException e) {
				LOGGER.error("getDefaultClient error:" + e.getMessage());
			}
		}

		if (null == defaultZkClient) {
			return null;
		}

		String bootstrapData = "";
		try {
			String partitionPath = ZnodeUtils.getPartitionPath(path);
			bootstrapData = ((ZookeeperClient) defaultZkClient).findChildData("/" + partitionPath + "/bootstrap");
		} catch (Exception e) {
			LOGGER.error("Do operation failed for getClient : " + e.getMessage());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("znode bootstrapData :" + "{" + bootstrapData + "}");
		}

		Map<String, Object> currentDataMap = ZnodeUtils.propertiesToMap(bootstrapData);
		if (currentDataMap != null) {
			for (Map.Entry<String, Object> entry : currentDataMap.entrySet()) {
				if (ZnodeUtils.isMatch(entry.getKey(), ZnodeUtils.convert(path))) {
					if (zkClientMap.get(entry.getValue()) == null) {
						try {
							Object specifyClient = ZookeeperClientFactory.getSpecifyClient(entry.getValue().toString());
							zkClientMap.put(entry.getValue().toString(), specifyClient);
						} catch (InitZKException e) {
							LOGGER.error("getSpecifyClient error:" + e.getMessage());
						}
					}

					if (LOGGER.isDebugEnabled()) {
						this.log();
					}

					return zkClientMap.get(entry.getValue());
				}
			}
		}

		return defaultZkClient;
	}

	private void log() {
		String zkClientLog = "";
		try {
			if (zkClientMap != null) {
				for (Map.Entry<String, Object> entry : zkClientMap.entrySet()) {
					zkClientLog += ("["
							+ entry.getKey()
							+ "=="
							+ (entry.getValue() != null ? ((ZookeeperClient) entry.getValue()).getCurrenConnectionStr()
									: "null") + "];");
				}
			}
			if (defaultZkClient != null) {
				zkClientLog += ("defaultClient :" + "[" + ((ZookeeperClient) defaultZkClient).getCurrenConnectionStr() + "];");
			}
		} catch (Exception e) {
			String errMsg = "Do operation failed for log : " + e.getMessage();
			LOGGER.error(errMsg, e);
		}
		LOGGER.info("znode zkClientData :" + "{" + zkClientLog + "}");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (CollectionUtils.isNotEmpty(paths)) {
			String configurationBackupPath = (String) PropertyHolder.getProperties().get(
					Constants.CONFIGURATION_BACKUP_PATH);

			for (String path : paths) {
				String data = "";
				try {
					ZookeeperClient client = (ZookeeperClient) getClient(path);
					if (client != null) {
						String backupFile = configurationBackupPath + path;
						if (!StringUtils.hasText(configurationBackupPath)) {
							backupFile = null;
						}

						data = client.watch(path, handlers, backupFile);
						LOGGER.info("cfgcenter [" + path + ":" + data + "]");
					} else {
						LOGGER.error("afterPropertiesSet fail [ get null client ]");
					}

				} catch (Exception e) {
					LOGGER.error("loadRemoteResourceProperties fail [" + e.getMessage() + "]");
				}
			}
		}
	}

	@Override
	public String getProperty(String key) {
		if (PropertyHolder.getProperties() != null) {
			return PropertyHolder.getProperties().getProperty(key);
		}
		return null;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		if (PropertyHolder.getProperties() != null) {
			return PropertyHolder.getProperties().getProperty(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Enumeration<Object> keys() {
		if (PropertyHolder.getProperties() != null) {
			return PropertyHolder.getProperties().keys();
		}
		return null;
	}

	@Override
	public Collection<Object> values() {
		if (PropertyHolder.getProperties() != null) {
			return PropertyHolder.getProperties().values();
		}
		return null;
	}

}
