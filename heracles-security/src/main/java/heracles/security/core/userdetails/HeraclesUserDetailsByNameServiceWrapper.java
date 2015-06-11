package heracles.security.core.userdetails;

import lombok.Setter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class HeraclesUserDetailsByNameServiceWrapper<T extends Authentication> extends
		UserDetailsByNameServiceWrapper<Authentication> {

	@Setter
	private HeraclesUserDetailsService heraclesUserDetailsService;

	@Override
	public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
		return heraclesUserDetailsService.loadUserByAuthentication(authentication);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.heraclesUserDetailsService, "HeraclesUserDetailsService must be set");
	}
	
	

}
