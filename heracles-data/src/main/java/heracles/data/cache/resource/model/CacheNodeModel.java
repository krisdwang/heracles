package heracles.data.cache.resource.model;

import heracles.data.cache.config.model.NodeState;
import lombok.Data;

@Data
public class CacheNodeModel {

	private String logicName;

	private String host;

	private int port;
	
	private NodeState state;

}
