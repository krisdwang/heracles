package heracles.security.config.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CasAuthTypeModel extends BaseAuthTypeModel {

	private String casServerUrl;
	
	private String casLoginUrl;
	
	private String clientServerUrl;
	
	private String clientServiceUrl;
	
	private String defaultTargetUrl;
	
	private String defaultFailureUrl;
	
	private String accessDeniedUrl;
	
	private String logoutSuccessUrl;
	
	private String proxyReceptorUrl;
	
	private String proxyCallbackUrl;
	
	private String casAuthProviderKey;
	
	private String casSendRenew;
	
}
