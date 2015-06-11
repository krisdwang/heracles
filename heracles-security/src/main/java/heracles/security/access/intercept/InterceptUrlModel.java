package heracles.security.access.intercept;

import lombok.Data;

@Data
public class InterceptUrlModel {

	private String pattern;

	private String method;
	
	private String access;
	
}
