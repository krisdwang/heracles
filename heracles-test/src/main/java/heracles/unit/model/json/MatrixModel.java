package heracles.unit.model.json;

import java.util.List;

import lombok.Data;

@Data
public class MatrixModel {

	private String matrixName;
	private String state;
	private String type;
	private List<GroupModel> groups;
	private List<RuleModel> rules;
	private List<ShardStrategyModel> strategies;
}
