package heracles.security.config.model;

import lombok.Data;

@Data
public class CustomFilterModel {

	private String ref;
	
	private String after;
	
	private String before;
	
	private String position;
}
