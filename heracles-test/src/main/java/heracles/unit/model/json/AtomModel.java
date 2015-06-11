package heracles.unit.model.json;

import lombok.Data;

@Data
public class AtomModel {

	private String atomName;
	private String host;
	private int port;
	private String username;
	private String password;
	private String dbName;
	private String param;
	private Boolean isMaster;
	private String state;

}
