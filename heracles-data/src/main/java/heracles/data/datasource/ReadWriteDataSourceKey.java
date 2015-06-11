package heracles.data.datasource;

import heracles.data.common.holder.StrategyHolder;
import heracles.data.datasource.strategy.LoadBalanceStrategy;
import heracles.data.datasource.strategy.RandomLoadBalanceStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

public class ReadWriteDataSourceKey implements InitializingBean {

	//private static final Logger log = LoggerFactory.getLogger(ReadWriteDataSourceKey.class);

	
	private Map<String, String> readKey2DateSourceMap = new HashMap<String, String>();
	private Map<String, String> failedKey2DataSourceMap = new ConcurrentHashMap<String, String>();
	private boolean alwaysReplaceExist = false;
	private LoadBalanceStrategy<String> lbStrategy;
	private String writeKey;

	public void setReadDateSources(Map<String, String> readDateSourceMap) {
		this.readKey2DateSourceMap = Collections.synchronizedMap(readDateSourceMap);
	}

	public String getReadKey(String key) {
		return readKey2DateSourceMap.get(key);
	}

	public Map<String, String> getReadKeys() {
		return readKey2DateSourceMap;
	}

	public String getWriteKey() {
		return writeKey;
	}

	public void setWriteKey(String writeKey) {
		this.writeKey = writeKey;
	}

	public void setWriteKey() {
		StrategyHolder.setDataSourceKey(writeKey);
	}

	public void setReadKey() {
		if (!alwaysReplaceExist && StrategyHolder.getDataSourceKey() != null) {		
			return;
		} 
		StrategyHolder.setDataSourceKey(lbStrategy.elect());
	}

	public void setKey(String key) {
		if (!alwaysReplaceExist && StrategyHolder.getDataSourceKey() != null && !writeKey.equals(key)) {
			return;
		}

		StrategyHolder.setDataSourceKey(readKey2DateSourceMap.get(key));
	}

	public String getKey() {
		if (StrategyHolder.getDataSourceKey() == null) {
			// default key is read key
			setReadKey();
		}
		String key = StrategyHolder.getDataSourceKey();
		return key;
	}

	public boolean isAlwaysReplaceExist() {
		return alwaysReplaceExist;
	}

	public void setAlwaysReplaceExist(boolean alwaysReplaceExist) {
		this.alwaysReplaceExist = alwaysReplaceExist;
	}

	public LoadBalanceStrategy<String> getStrategy() {
		return lbStrategy;
	}

	public void setStrategy(LoadBalanceStrategy<String> strategy) {
		this.lbStrategy = strategy;
	}

	public synchronized void removeDataSourceKey(String key) {
		if (readKey2DateSourceMap.containsKey(key)) {
			failedKey2DataSourceMap.put(key, readKey2DateSourceMap.get(key));
			readKey2DateSourceMap.remove(key);
			lbStrategy.removeTarget(key);
			StrategyHolder.clearDataSourceKey();
		}
	}

	public synchronized void recoverDateSourceKey(String key) {
		if (failedKey2DataSourceMap.containsKey(key)) {
			readKey2DateSourceMap.put(key, failedKey2DataSourceMap.get(key));
			failedKey2DataSourceMap.remove(key);
			lbStrategy.recoverTarget(key);
		}
	}

	public void resetKey() {
		String key = getKey();
		if (!writeKey.equals(key)) {
			setReadKey();
		}
	}

	public boolean isCurrentWriteKey() {
		String key = StrategyHolder.getDataSourceKey();
		return writeKey.equals(key);
	}

	public boolean hasReadKey() {
		return !readKey2DateSourceMap.isEmpty();
	}

	public synchronized boolean hasFailedDataSource() {
		return !failedKey2DataSourceMap.isEmpty();
	}

	public Map<String, String> getFailedDataSourceKeys() {
		return failedKey2DataSourceMap;
	}

	public void afterPropertiesSet() throws Exception {
		if (lbStrategy == null) {
			List<String> list = new ArrayList<String>(readKey2DateSourceMap.values());
			lbStrategy = new RandomLoadBalanceStrategy(list);
		}
	}
}
