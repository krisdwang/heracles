package heracles.data.common.model;

import lombok.Data;

@Data
public class TableShardStrategyModel {
	private String strategyName;
	private String strategy;
	private String tableSuffix;
}
