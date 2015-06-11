package heracles.data.common.strategy.table;

import heracles.data.common.vo.ShardingParameter;

import org.springframework.util.Assert;

/**
 * 分表策略抽象类
 * 
 * @author kriswang
 * 
 */
public abstract class TableShardingStrategy {

	private static final ThreadLocal<ShardingParameter> shardingParameter = new ThreadLocal<ShardingParameter>();

	public ShardingParameter getShardingParameter() {
		ShardingParameter sp = shardingParameter.get();
		Assert.notNull(sp, "sharding parameter is null");
		Assert.notNull(sp.getValue(), "sharding parameter value is null");
		return sp;
	}

	public Object getShardingParameterValue() {
		return getShardingParameter().getValue();
	}

	public void setShardingParameter(ShardingParameter shardingParameter) {
		TableShardingStrategy.shardingParameter.set(shardingParameter);
	}

	public abstract String getTargetSql(String sql);
}
