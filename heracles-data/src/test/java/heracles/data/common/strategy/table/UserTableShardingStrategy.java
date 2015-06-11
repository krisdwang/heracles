package heracles.data.common.strategy.table;

import heracles.data.common.annotation.Strategy;
import heracles.data.common.util.Utils;

import org.springframework.util.Assert;

/**
 * user table sharding strategy
 * 
 * @author Anders
 * 
 */
@Strategy("user")
public class UserTableShardingStrategy extends TableShardingStrategy {

	@Override
	public String getTargetSql(String sql) {
		Long value = (Long) getShardingParameterValue();
		Assert.notNull(value);
		String realTableName = "user" + (value % 2);
		return Utils.getShardingTableName("user", realTableName, sql);
	}

}
