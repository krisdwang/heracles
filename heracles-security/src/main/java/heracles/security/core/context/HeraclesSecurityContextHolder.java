package heracles.security.core.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class HeraclesSecurityContextHolder {

	public static void clearContext() {
		SecurityContextHolder.clearContext();
	}

	public static HeraclesSecurityContext getContext() {		
		SecurityContext sc = SecurityContextHolder.getContext();
		return new HeraclesSecurityContext(sc);
	}

	public static void setContext(SecurityContext sc) {
		SecurityContextHolder.setContext(sc);
	}

	public static void setContext(HeraclesSecurityContext vsc) {
		SecurityContextHolder.setContext(vsc.getSecurityContext());
	}
}
