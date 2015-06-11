package heracles.data.datasource;

import java.util.Set;

public interface RepositoryShardingDataSourceMBean {

	void putKey(String key);

	void removeKey(String key);
	
	Set<String> getMarkDownKeys();
}
