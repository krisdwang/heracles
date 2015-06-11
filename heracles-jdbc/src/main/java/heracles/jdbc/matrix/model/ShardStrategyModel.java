package heracles.jdbc.matrix.model;

import java.util.List;

import lombok.Data;

@Data
public class ShardStrategyModel {
	private List<RepoShardStrategyModel> repoShardStrategies;
	private List<TableShardStrategyModel> tableShardStrategies;
}
