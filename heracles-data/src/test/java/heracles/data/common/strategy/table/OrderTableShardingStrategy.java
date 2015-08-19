package heracles.data.common.strategy.table;

import heracles.data.common.annotation.Strategy;
import heracles.data.common.util.Utils;

import org.springframework.util.Assert;

/**
 * order table sharding strategy
 * 
 * @author kriswang
 * 
 */
@Strategy("order")
public class OrderTableShardingStrategy extends TableShardingStrategy {

	@Override
	public String getTargetSql(String sql) {
		Long value = (Long) getShardingParameterValue();
		Assert.notNull(value);
		String realTableName = "order" + (value % 2);
		return Utils.getShardingTableName("order", realTableName, sql);
	}
}
