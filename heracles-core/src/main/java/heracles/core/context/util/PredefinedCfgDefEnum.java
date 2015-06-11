package heracles.core.context.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author kriswang
 * 
 */
public enum PredefinedCfgDefEnum {
	RESOURCE_RDBMS_MYSQL_LOGICNAME("mysql", "/resource/RDBMS/MySQL/");

	private static Map<String, String> PRE_CFG_DEF_ENUM = new HashMap<String, String>();
	static {
		for (PredefinedCfgDefEnum preCfgDef : PredefinedCfgDefEnum.values()) {
			PRE_CFG_DEF_ENUM.put(preCfgDef.getKey(), preCfgDef.getPath());
		} 
	}

	private static List<String> PRE_CFG_DEF_KEY_LIST = new ArrayList<String>();
	static {
		for (PredefinedCfgDefEnum preCfgDef : PredefinedCfgDefEnum.values()) {
			PRE_CFG_DEF_KEY_LIST.add(preCfgDef.getKey());
		}
	}

	/**
	 * key
	 */
	@Getter
	@Setter
	private String key;
	
	/**
	 * path
	 */
	@Getter
	@Setter
	private String path;

	private PredefinedCfgDefEnum(String key, String path) {
		this.key = key;
		this.path = path;
	}

	public static Map<String, String> getPredefinedCfgDefKeyToPath() {
		return PRE_CFG_DEF_ENUM;
	}

	public static List<String> getPredefinedCfgDefKey() {
		return PRE_CFG_DEF_KEY_LIST;
	}

	/**
	 * 根据key获取path
	 * @param key
	 * @return String
	 */
	public static String getPath(String key) {
		return PRE_CFG_DEF_ENUM.get(key);
	}
}
