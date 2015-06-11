package heracles.security.core.context;

import lombok.Data;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

@Data
public class HeraclesSecurityContext implements SecurityContext {

	private static final long serialVersionUID = 76932315010990524L;

	private SecurityContext securityContext;

	public HeraclesSecurityContext(SecurityContext securityContext) {
		super();
		this.securityContext = securityContext;
	}

	@Override
	public Authentication getAuthentication() {
		return securityContext.getAuthentication();
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		securityContext.setAuthentication(authentication);
	}

}
