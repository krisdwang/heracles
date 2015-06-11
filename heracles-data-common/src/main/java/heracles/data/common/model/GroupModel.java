package heracles.data.common.model;

import java.util.List;

import lombok.Data;

@Data
public class GroupModel {
	private String groupName;
	private String state;
	private String loadBalance;
	private List<AtomModel> atoms;
}
