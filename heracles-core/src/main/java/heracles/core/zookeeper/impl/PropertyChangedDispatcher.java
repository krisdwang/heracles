package heracles.core.zookeeper.impl;

import heracles.core.context.property.PropertyHolder;
import heracles.core.context.util.Utils;
import heracles.core.zookeeper.PropertyChangedHandler;
import heracles.core.zookeeper.ZookeeperListener;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.NodeCache;

/**
 * 
 * @author kriswang
 * 
 */
public class PropertyChangedDispatcher implements ZookeeperListener {

	@Getter
	@Setter
	private String oldData;
	@Getter
	@Setter
	private NodeCache nodeCache;
	@Getter
	@Setter
	private List<PropertyChangedHandler> handlers;
	@Getter
	@Setter
	private String backupFile;

	public PropertyChangedDispatcher(List<PropertyChangedHandler> handlers, NodeCache nodeCache, String backupFile) {
		this.handlers = handlers;
		this.nodeCache = nodeCache;
		this.backupFile = backupFile;
	}

	public PropertyChangedDispatcher(List<PropertyChangedHandler> handlers, String oldData, NodeCache nodeCache,
			String backupFile) {
		this.handlers = handlers;
		this.nodeCache = nodeCache;
		this.backupFile = backupFile;
	}

	@Override
	public void nodeChanged() throws Exception {

		String data = new String(nodeCache.getCurrentData().getData(), "utf-8");
		Map<String, Object> newMap = propertiesToMap(data);
		Properties oldProps = new Properties();
		Properties newProps = new Properties();
		oldProps.putAll((Map<Object, Object>) PropertyHolder.getProperties());
		newProps.putAll((Map<Object, Object>) PropertyHolder.getProperties());

		if (newMap != null) {
			Set<String> keySet = newMap.keySet();
			for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
				String key = (String) it.next();
				newProps.put(key, newMap.get(key));
			}
		}

		PropertyHolder.setProperties(newProps);

		try {
			/**
			 * 备份最新配置到指定文件
			 */
			if (StringUtils.isNotBlank(backupFile)) {
				if (new File(backupFile).exists()) {
					String oldPro = Utils.readFile(new File(backupFile));
					Map<String, Object> tempMap = Utils.propertiesToMap(oldPro);
					if (tempMap != null && newMap != null) {
						Set<String> keySet = newMap.keySet();
						for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
							String key = (String) it.next();
							tempMap.put(key, newMap.get(key));
						}
						Utils.writeFile(new File(backupFile), Utils.mapToProperties(tempMap));
					}
				}
			}
		} catch (Exception e) {
		}

		if (CollectionUtils.isNotEmpty(handlers)) {
			for (PropertyChangedHandler handler : handlers) {
				handler.execute(oldProps, newProps);
			}
		}
	}

	/**
	 * properties转map
	 * 
	 * @param pro
	 * @return Map<String, Object>
	 */
	private Map<String, Object> propertiesToMap(String pro) {

		if (StringUtils.isBlank(pro)) {
			return new LinkedHashMap<String, Object>();
		}
		String[] strArray = pro.split("\n");
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (strArray != null && strArray.length > 0) {

			for (String str : strArray) {
				result.put(StringUtils.substringBefore(str, "="), StringUtils.substringAfter(str, "="));
			}
		}

		return result;
	}
}
