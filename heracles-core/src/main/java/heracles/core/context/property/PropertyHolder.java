package heracles.core.context.property;

import heracles.core.context.util.Utils;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyHolder {
	private static Logger log = LoggerFactory.getLogger(PropertyHolder.class);

	private static Properties properties = new Properties();

	public static void setProperties(Properties properties) {
		Properties result = new Properties();
		try {
			result = (Properties) Utils.deepClone(properties);
		} catch (Throwable e) {
			log.error("Utils.deepClone is error" + e.getMessage());
		}
		PropertyHolder.properties = result;
	}

	public static void addProperties(Properties properties) {
		try {
			PropertyHolder.properties.putAll(properties);
		} catch (Throwable e) {
			log.error("addProperties is error" + e.getMessage());
		}
	}

	public static Properties getProperties() {
		Properties result = new Properties();
		try {
			result = (Properties) Utils.deepClone(properties);
		} catch (Throwable e) {
			log.error("Utils.deepClone is error" + e.getMessage());
		}
		return result;
	}
}
