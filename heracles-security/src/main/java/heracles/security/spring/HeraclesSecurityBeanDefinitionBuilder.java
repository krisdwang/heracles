package heracles.security.spring;

import heracles.security.access.intercept.InterceptUrlModel;
import heracles.security.config.model.AccessDecisionManagerModel;
import heracles.security.config.model.CasAuthTypeModel;
import heracles.security.config.model.MetadataSourceModel;
import heracles.security.config.model.UserDetailsManagerModel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;

public class HeraclesSecurityBeanDefinitionBuilder implements HeraclesSecurityConstants {

	private static final Logger log = LoggerFactory.getLogger(HeraclesSecurityBeanDefinitionBuilder.class);

	private HeraclesSecurityElementParser elmtParser;

	private ParserContext pc;

	public HeraclesSecurityBeanDefinitionBuilder(HeraclesSecurityElementParser elmtParser, ParserContext pc) {
		this.elmtParser = elmtParser;
		this.pc = pc;
	}

	public void buildBeanDefinition() {
		buildAuthTypeBeanDef(elmtParser.getCasAuthTypeModel(), pc);
		log.info("Heracles security - auth type bean definition build completed!");

		buildMetadataSourceBeanDef(elmtParser.getMetadataSourceModel(), pc);
		log.info("Heracles security - metadata source bean definition build completed!");

		buildAccessDecisionManagerDef(elmtParser.getAccessDecisionManagerModel(), pc);
		log.info("Heracles security - access decision bean definition build completed!");

		buildUserDetailsManagerDef(elmtParser.getUserDetailsManagerModel(), pc);
		log.info("Heracles security - user details bean definition build completed!");
	}

	private void buildAuthTypeBeanDef(CasAuthTypeModel casAuthTypeMdl, ParserContext pc) {
		buildCasAuthTypeBeanDef(casAuthTypeMdl, pc);
	}

	private void buildCasAuthTypeBeanDef(CasAuthTypeModel casAuthTypeMdl, ParserContext pc) {
		BeanDefinitionRegistry beanDefReg = pc.getRegistry();

		BeanDefinitionBuilder htmlAuthFailHdlrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(HTML_AUTHENTICATION_FAILURE_HANDLER);
		htmlAuthFailHdlrBldr.addPropertyValue(DEFAULT_FAILURE_URL, casAuthTypeMdl.getDefaultFailureUrl());
		BeanDefinitionBuilder authFailHdlrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(AUTHENTICATION_FAILURE_HANDLER);
		authFailHdlrBldr.addPropertyValue("splAuthFailHdlr", htmlAuthFailHdlrBldr.getBeanDefinition());
		AbstractBeanDefinition authFailHdlr = authFailHdlrBldr.getBeanDefinition();

		BeanDefinitionBuilder htmlAuthSuccHdlrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(HTML_AUTHENTICATION_SUCC_HANDLER);
		htmlAuthSuccHdlrBldr.addPropertyValue(DEFAULT_TARGET_URL, casAuthTypeMdl.getDefaultTargetUrl());
		BeanDefinitionBuilder authSuccHdlrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(AUTHENTICATION_SUCC_HANDLER);
		authSuccHdlrBldr.addPropertyValue("splAuthSuccHdlr", htmlAuthSuccHdlrBldr.getBeanDefinition());
		AbstractBeanDefinition authSuccHdlr = authSuccHdlrBldr.getBeanDefinition();

		BeanDefinitionBuilder htmlAcesDndHdlrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(HTML_ACCESS_DENIED_HANDLER_CLAZZ);
		htmlAcesDndHdlrBldr.addPropertyValue("errorPage", casAuthTypeMdl.getAccessDeniedUrl());
		BeanDefinitionBuilder acesDndHdlrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(ACCESS_DENIED_HANDLER_CLAZZ);
		acesDndHdlrBldr.addPropertyValue("acesDenHdlr", htmlAcesDndHdlrBldr.getBeanDefinition());
		beanDefReg.registerBeanDefinition(ACCESS_DENIED_HANDLER, acesDndHdlrBldr.getBeanDefinition());

		BeanDefinitionBuilder casAuthFilterBldr = BeanDefinitionBuilder
				.genericBeanDefinition(CAS_AUTHENTICATION_FILTER_CLAZZ);
		casAuthFilterBldr.addPropertyReference(AUTHENTICATION_MANAGER, AUTHENTICATION_MANAGER);
		casAuthFilterBldr.addPropertyValue("authenticationFailureHandler", authFailHdlr);
		casAuthFilterBldr.addPropertyValue("authenticationSuccessHandler", authSuccHdlr);
		casAuthFilterBldr.addPropertyReference(PROXY_GRANTING_TICKET_STORAGE, PROXY_GRANTING_TICKET_STORAGE);
		casAuthFilterBldr.addPropertyValue(PROXY_RECEPTOR_URL, casAuthTypeMdl.getProxyReceptorUrl());
		beanDefReg.registerBeanDefinition(CAS_AUTHENTICATION_FILTER, casAuthFilterBldr.getBeanDefinition());

		BeanDefinitionBuilder casAuthEntryBldr = BeanDefinitionBuilder
				.genericBeanDefinition(CAS_AUTHENTICATION_ENTRY_POINT_CLAZZ);
		casAuthEntryBldr.addPropertyValue(LOGIN_URL, casAuthTypeMdl.getCasLoginUrl());
		casAuthEntryBldr.addPropertyReference(SERVICE_PROPERTIES, SERVICE_PROPERTIES);
		beanDefReg.registerBeanDefinition(CAS_PROCESSING_FILTER_ENTRY_POINT, casAuthEntryBldr.getBeanDefinition());

		BeanDefinitionBuilder casAuthPrdrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(CAS_AUTHENTICATION_PROVIDER_CLAZZ);
		casAuthPrdrBldr.addPropertyReference(USER_DETAILS_SERVICE, USER_DETAILS_SERVICE);
		casAuthPrdrBldr.addPropertyReference(SERVICE_PROPERTIES, SERVICE_PROPERTIES);
		casAuthPrdrBldr.addPropertyReference(AUTHENTICATION_USER_DETAILS_SERVICE, AUTHENTICATION_USER_DETAILS_SERVICE);

		BeanDefinitionBuilder ticketValidBldr = BeanDefinitionBuilder.genericBeanDefinition(TICKET_VALIDATOR_CLAZZ);
		ticketValidBldr.addConstructorArgValue(casAuthTypeMdl.getCasServerUrl());
		ticketValidBldr.addPropertyReference(PROXY_GRANTING_TICKET_STORAGE, PROXY_GRANTING_TICKET_STORAGE);
		ticketValidBldr.addPropertyValue(PROXY_CALLBACK_URL, casAuthTypeMdl.getProxyCallbackUrl());

		casAuthPrdrBldr.addPropertyValue(TICKET_VALIDATOR, ticketValidBldr.getBeanDefinition());
		casAuthPrdrBldr.addPropertyValue("key", casAuthTypeMdl.getCasAuthProviderKey());
		beanDefReg.registerBeanDefinition(CAS_AUTHENTICATION_PROVIDER, casAuthPrdrBldr.getBeanDefinition());

		BeanDefinitionBuilder pgtStoeBldr = BeanDefinitionBuilder
				.genericBeanDefinition(PROXY_GRANTING_TICKET_STORAGE_CLAZZ);
		beanDefReg.registerBeanDefinition(PROXY_GRANTING_TICKET_STORAGE, pgtStoeBldr.getBeanDefinition());

		BeanDefinitionBuilder svcPropBldr = BeanDefinitionBuilder.genericBeanDefinition(SERVICE_PROPERTIES_CLAZZ);
		svcPropBldr.addPropertyValue(SERVICE, casAuthTypeMdl.getClientServiceUrl());
		svcPropBldr.addPropertyValue(SEND_RENEW, casAuthTypeMdl.getCasSendRenew());
		beanDefReg.registerBeanDefinition(SERVICE_PROPERTIES, svcPropBldr.getBeanDefinition());
	}

	private void buildMetadataSourceBeanDef(MetadataSourceModel metadataSreMdl, ParserContext pc) {
		BeanDefinitionRegistry beanDefReg = pc.getRegistry();

		BeanDefinitionBuilder fterSecuItcrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(FILTER_SECURITY_INTERCEPTROR_CLAZZ);
		fterSecuItcrBldr.addPropertyReference(AUTHENTICATION_MANAGER, AUTHENTICATION_MANAGER);
		fterSecuItcrBldr.addPropertyReference(ACCESS_DECISION_MANAGER, ACCESS_DECISION_MANAGER);
		fterSecuItcrBldr.addPropertyReference(SECURITY_METADATA_SOURCE, SECURITY_METADATA_SOURCE);
		beanDefReg.registerBeanDefinition(FILTER_SECURITY_INTERCEPTROR, fterSecuItcrBldr.getBeanDefinition());

		BeanDefinitionBuilder secuMetaBldr = BeanDefinitionBuilder
				.genericBeanDefinition(SECURITY_METADATA_SOURCE_CLAZZ);
		secuMetaBldr.addConstructorArgReference(REQUEST_MAP);
		beanDefReg.registerBeanDefinition(SECURITY_METADATA_SOURCE, secuMetaBldr.getBeanDefinition());

		BeanDefinitionBuilder reqMapBldr = BeanDefinitionBuilder.genericBeanDefinition(metadataSreMdl.getClazzName());
		reqMapBldr.addPropertyValue("realms", metadataSreMdl.getSpringBeans());
		reqMapBldr.addPropertyReference(SIMPLE_REALM, SIMPLE_REALM);
		beanDefReg.registerBeanDefinition(REQUEST_MAP, reqMapBldr.getBeanDefinition());

		List<InterceptUrlModel> itctUrls = metadataSreMdl.getInterceptUrls();
		BeanDefinitionBuilder smpRlmBldr = BeanDefinitionBuilder.genericBeanDefinition(SIMPLE_REALM_CLAZZ);
		smpRlmBldr.addPropertyValue("interceptUrlModels", itctUrls);
		reqMapBldr.addPropertyValue(SIMPLE_REALM, smpRlmBldr.getBeanDefinition());
	}

	/**
	 * Build access decision manager bean definition.
	 * 
	 * @param accessDecisionManagerMdl
	 * @param pc
	 */
	private void buildAccessDecisionManagerDef(AccessDecisionManagerModel accessDecisionManagerMdl, ParserContext pc) {
		BeanDefinitionBuilder accessDecisionBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(accessDecisionManagerMdl.getClazzName());
		accessDecisionBuilder.addPropertyValue("decisionVoters", accessDecisionManagerMdl.getSpringBeans());
		pc.getRegistry().registerBeanDefinition(ACCESS_DECISION_MANAGER, accessDecisionBuilder.getBeanDefinition());
	}

	/**
	 * Build user details manager bean definition.
	 * 
	 * @param usrDtlMgrMdl
	 * @param pc
	 */
	private void buildUserDetailsManagerDef(UserDetailsManagerModel usrDtlMgrMdl, ParserContext pc) {
		BeanDefinitionRegistry beanDefReg = pc.getRegistry();

		BeanDefinitionBuilder authUserSvcBldr = BeanDefinitionBuilder
				.genericBeanDefinition(usrDtlMgrMdl.getClazzName());
		authUserSvcBldr.addPropertyReference("heraclesUserDetailsService", USER_DETAILS_SERVICE);
		beanDefReg.registerBeanDefinition(AUTHENTICATION_USER_DETAILS_SERVICE, authUserSvcBldr.getBeanDefinition());

		BeanDefinitionBuilder userDtalBldr = BeanDefinitionBuilder.genericBeanDefinition(usrDtlMgrMdl
				.getUserDetailsClazzName());
		beanDefReg.registerBeanDefinition(USER_DETAILS_SERVICE, userDtalBldr.getBeanDefinition());
	}
}
