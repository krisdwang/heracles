package heracles.data.cache.utils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.ParserContext;

public class Utils {

	private static final int ONE = 1;

	private static final String DEFAULT = "default";

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static String resolveSystemProperty(String key) {
		String value = null;
		try {
			value = System.getProperty(key);
		} catch (Throwable e) {
			LOGGER.warn("Failed to get value of property: " + key, e);
		}

		if (null == value) {
			try {
				value = System.getenv(key);
			} catch (Exception e) {
				LOGGER.warn("Failed to get value from env : " + key, e);
			}
			if (null == value) {
				value = DEFAULT;
			}
			return value;
		} else {
			return value;
		}
	}
	
	public static <T extends Enum<T>> T getEnumFromString(String strType, Class<T> clazz, ParserContext pc) {
		T enumType = null;
		try {
			enumType = Enum.valueOf(clazz, StringUtils.upperCase(strType));
		} catch (Exception e) {
			String errMsg = "Failed to get enum type, unsupported enum type:" + strType + " for enum:"
					+ clazz.getName();
			LOGGER.error(errMsg, e);
			pc.getReaderContext().error(errMsg, null);
		}
		return enumType;
	}
	
	public static boolean isMulitiple(Collection<?> cltn) {
		return cltn.size() > ONE;
	}
}
