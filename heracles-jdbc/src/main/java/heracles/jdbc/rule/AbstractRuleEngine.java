package heracles.jdbc.rule;

public abstract class AbstractRuleEngine implements RuleEngine {
	private String[] indexes;
	private String script;
	private String shardingKey;

	@Override
	public String[] getIndexes() {
		return indexes;
	}

	@Override
	public void setIndexes(String[] indexes) {
		this.indexes = indexes;
	}

	@Override
	public String getScript() {
		return script;
	}

	@Override
	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public String getShardingKey() {
		return shardingKey;
	}

	@Override
	public void setShardingKey(String shardingKey) {
		this.shardingKey = shardingKey;
	}

}
