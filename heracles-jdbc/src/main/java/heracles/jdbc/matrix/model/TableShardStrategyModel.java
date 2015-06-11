package heracles.jdbc.matrix.model;

import lombok.Data;

@Data
public class TableShardStrategyModel {
	private String strategyName;
	private String strategy;
	private String tableSuffix;
}
