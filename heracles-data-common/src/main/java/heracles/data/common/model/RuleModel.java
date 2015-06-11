package heracles.data.common.model;

import lombok.Data;

@Data
public class RuleModel {
	private String tableNames;
	private String groupShardRule;
	private String groupIndex;
	private String tableShardRule;
	private String tableSuffix;
}
