package heracles.security.core.userdetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface HeraclesUserDetailsService extends UserDetailsService {

	UserDetails loadUserByAuthentication(Authentication authentication);
}
