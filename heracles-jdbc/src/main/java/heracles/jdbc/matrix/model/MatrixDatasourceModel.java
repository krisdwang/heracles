package heracles.jdbc.matrix.model;

import java.util.List;

import lombok.Data;

@Data
public class MatrixDatasourceModel {
	private String matrixName;
	private String state;
	private String type;
	private List<GroupModel> groups;
	private List<RuleModel> rules;
	private List<ShardStrategyModel> strategies;

}
