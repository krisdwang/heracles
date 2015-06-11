package heracles.data.common.strategy.table;


/**
 * 不分表策略
 * 
 * @author kriswang
 * 
 */
public class NoTableShardingStrategy extends TableShardingStrategy {

	private static final NoTableShardingStrategy INSTANCE = new NoTableShardingStrategy();

	private NoTableShardingStrategy() {
	}

	@Override
	public String getTargetSql(String sql) {
		return sql;
	}

	public static NoTableShardingStrategy getInstance() {
		return INSTANCE;
	}
}
