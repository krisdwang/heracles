package heracles.data.mybatis.converter;

import heracles.data.common.holder.StrategyHolder;
import heracles.data.common.strategy.table.NoTableShardingStrategy;
import heracles.data.common.strategy.table.TableShardingStrategy;
import heracles.data.common.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlConverter implements SqlConverter {

	private final Logger LOGGER = LoggerFactory.getLogger(DefaultSqlConverter.class);

	public String convert(String sql, StatementHandler statementHandler) {
		// TODO kriswang Zhu 考虑是否要支持单表多个策略
		String name = Utils.getMatchTableName(sql);
		if (StringUtils.isBlank(name)) {
			LOGGER.debug("table sharding strategy name is blank");
			return Utils.trimSql(sql);
		}

		TableShardingStrategy tableShardingStrategy = StrategyHolder.getTableShardingStrategy(name);

		if (tableShardingStrategy == null || tableShardingStrategy instanceof NoTableShardingStrategy) {
			LOGGER.debug("no table sharding strategy");
			return Utils.trimSql(sql);
		}

		return Utils.trimSql(tableShardingStrategy.getTargetSql(sql));
	}
}
