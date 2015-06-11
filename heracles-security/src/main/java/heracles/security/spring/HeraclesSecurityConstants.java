package heracles.security.spring;

public interface HeraclesSecurityConstants {
	
	static final String SPRING_SECURITY_NAMESPACE = "http://www.springframework.org/schema/security";
	
	static final String HEALTH_CHECK_PATTERN = "/_health_check";
	static final String REST_DOC_PATTERN = "/restdoc/**";


	static final String CAS_SERVER_URL = "casServerUrl";
	static final String CAS_LOGIN_URL = "casLoginUrl";

	static final String CLIENT_SERVER_URL = "clientServerUrl";
	static final String CLIENT_SERVICE_URL = "clientServiceUrl";

	static final String DEFAULT_TARGET_URL = "defaultTargetUrl";
	static final String DEFAULT_FAILURE_URL = "defaultFailureUrl";
	static final String ACCESS_DENIED_URL = "accessDeniedUrl";
	static final String LOGOUT_SUCCESS_URL = "logoutSuccessUrl";

	static final String PROXY_RECEPTOR_URL = "proxyReceptorUrl";
	static final String PROXY_CALLBACK_URL = "proxyCallbackUrl";

	static final String CAS_AUTH_PROVIDER_KEY = "casAuthProviderKey";
	static final String CAS_SEND_RENEW = "casSendRenew";

	static final String AUTHENTICATION_MANAGER = "authenticationManager";

	static final String CAS_AUTHENTICATION_FILTER = "casAuthenticationFilter";
	static final String CAS_AUTHENTICATION_FILTER_CLAZZ = "org.springframework.security.cas.web.CasAuthenticationFilter";
	
	static final String AUTHENTICATION_FAILURE_HANDLER = "heracles.security.web.authentication.MultiMediaTypesAuthenticationFailureHandler";
	static final String HTML_AUTHENTICATION_FAILURE_HANDLER = "org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler";
	static final String AUTHENTICATION_SUCC_HANDLER = "heracles.security.web.authentication.MultiMediaTypesAuthenticationSuccessHandler";
	static final String HTML_AUTHENTICATION_SUCC_HANDLER = "org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler";
	static final String ACCESS_DENIED_HANDLER = "accessDeniedHandler";
	static final String ACCESS_DENIED_HANDLER_CLAZZ = "heracles.security.web.authentication.MultiMediaTypesAccessDeniedHandler";
	static final String HTML_ACCESS_DENIED_HANDLER_CLAZZ = "org.springframework.security.web.access.AccessDeniedHandlerImpl";

	static final String CAS_PROCESSING_FILTER_ENTRY_POINT = "casProcessingFilterEntryPoint";
	static final String CAS_AUTHENTICATION_ENTRY_POINT_CLAZZ = "org.springframework.security.cas.web.CasAuthenticationEntryPoint";
	static final String LOGIN_URL = "loginUrl";

	static final String CAS_AUTHENTICATION_PROVIDER = "casAuthenticationProvider";
	static final String CAS_AUTHENTICATION_PROVIDER_CLAZZ = "org.springframework.security.cas.authentication.CasAuthenticationProvider";
	static final String TICKET_VALIDATOR = "ticketValidator";
	static final String TICKET_VALIDATOR_CLAZZ = "org.jasig.cas.client.validation.Cas20ServiceTicketValidator";

	static final String PROXY_GRANTING_TICKET_STORAGE = "proxyGrantingTicketStorage";
	static final String PROXY_GRANTING_TICKET_STORAGE_CLAZZ = "org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl";

	static final String SERVICE_PROPERTIES = "serviceProperties";
	static final String SERVICE_PROPERTIES_CLAZZ = "org.springframework.security.cas.ServiceProperties";
	static final String SERVICE = "service";
	static final String SEND_RENEW = "sendRenew";

	static final String FILTER_SECURITY_INTERCEPTROR = "filterSecurityInterceptor";
	static final String FILTER_SECURITY_INTERCEPTROR_CLAZZ = "heracles.security.access.intercept.MetaSourceFilterSecurityIntercept";

	static final String SECURITY_METADATA_SOURCE = "securityMetadataSource";
	static final String SECURITY_METADATA_SOURCE_CLAZZ = "org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource";
	static final String DEFAULT_METADATA_SOURCE_MAP_CLAZZ = "heracles.security.access.intercept.RequestToAttributesMapMetaSource";
	static final String IN_MEMORY_REALM_CLAZZ = "heracles.security.access.intercept.InMemoryRequestToAttributesMapRealm";
	static final String SIMPLE_REALM = "simpleRealm";
	static final String SIMPLE_REALM_CLAZZ = "heracles.security.access.intercept.SimpleRequestToAttributesRealm";

	static final String REQUEST_MAP = "requestMap";

	static final String ACCESS_DECISION_MANAGER = "accessDecisionManager";
	static final String DEFAULT_ACCESS_DECISION_MANAGER_CLAZZ = "org.springframework.security.access.vote.AffirmativeBased";
	static final String AUTH_SYS_ROLE_VOTER_CLAZZ = "heracles.plugin.security.authsys.AuthSysRoleVoter";
	
	static final String USER_DETAILS_MANAGER_CLAZZ = "heracles.security.core.userdetails.HeraclesUserDetailsByNameServiceWrapper";
	static final String USER_DETAILS_CLAZZ = "heracles.plugin.security.oa.OAUserDetailsManager";
	static final String AUTHENTICATION_USER_DETAILS_SERVICE = "authenticationUserDetailsService";
	static final String USER_DETAILS_SERVICE = "userDetailsService";
	
	

}
