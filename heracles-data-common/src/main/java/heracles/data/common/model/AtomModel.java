package heracles.data.common.model;

import lombok.Data;

@Data
public class AtomModel {
	private String atomName;
	private String host;
	private String port;
	private String username;
	private String password;
	private String dbName;
	private String param;
	private Boolean isMaster;
	private String state;
}
