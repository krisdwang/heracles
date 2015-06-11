package heracles.core.zookeeper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class ZnodeUtils {

	/**
	 * @param key
	 * @return
	 */
	public static String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null) {
				value = System.getenv(key);
			}
			if (value == null) {
				value = "";
			}
			return value;
		} catch (Throwable ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * 去partition
	 * 
	 * @param path
	 * @return
	 */
	public static String convert(String path) {
		if (StringUtils.isNotBlank(path)) {
			return "/" + StringUtils.substringAfter(StringUtils.substringAfter(path, "/"), "/");
		}
		return "";
	}

	/**
	 * @param patternPath
	 * @param requestPath
	 * @return
	 */
	public static boolean isMatch(String patternPath, String requestPath) {
		PathMatcher matcher = new AntPathMatcher();
		return matcher.match(StringUtils.lowerCase(patternPath), StringUtils.lowerCase(requestPath));
	}

	/**
	 * @return
	 */
	public static String getBootStrapPath() {
		String partition = ZnodeUtils.resolveSystemProperty(ZnodeConstants.ZOOKEEPER_DEFAULT_PARTITION_KEY);
		if (StringUtils.isBlank(partition)) {
			partition = "default";
		}
		/**
		 * FIXME kriswang 记得改回来
		 */
		return "/" + partition + "/bootstrap";
	}

	/**
	 * @return
	 */
	public static String getPartitionPath(String path) {
		if (StringUtils.isBlank(path)) {
			return "";
		}
		return StringUtils.substringBefore(StringUtils.substringAfter(path, "/"), "/");
	}

	/**
	 * @param pro
	 * @return
	 */
	public static Map<String, Object> propertiesToMap(String pro) {

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
