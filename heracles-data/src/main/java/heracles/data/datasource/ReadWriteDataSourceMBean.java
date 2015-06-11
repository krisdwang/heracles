package heracles.data.datasource;

import java.util.Map;

public interface ReadWriteDataSourceMBean {

	void putKey(String key, String replaceKey);

	void removeKey(String key);
	
	String getWriteKey();
	
	Map<String, String> getReadKeys();
	
	Map<String, String> getMarkDownKeys();
}
