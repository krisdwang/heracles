package heracles.jdbc.rule;

public interface RuleEngine {
	String eval(Object key);

	String[] getIndexes();

	void setIndexes(String[] indexes);

	String getScript();

	void setScript(String script);

	String getShardingKey();

	void setShardingKey(String shardingKey);
}
