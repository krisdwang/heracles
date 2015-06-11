package heracles.core.context.property;

import java.util.Collection;
import java.util.Enumeration;

public interface PropertyManager {
	public String getProperty(String key);

	public String getProperty(String key, String defaultValue);

	public Enumeration<Object> keys();

	public Collection<Object> values();
}
