package heracles.security.spring;

import heracles.security.access.intercept.InterceptUrlModel;
import heracles.security.config.model.AccessDecisionManagerModel;
import heracles.security.config.model.CasAuthTypeModel;
import heracles.security.config.model.CustomFilterModel;
import heracles.security.config.model.MetadataSourceModel;
import heracles.security.config.model.UserDetailsManagerModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class HeraclesSecurityElementParser implements HeraclesSecurityConstants {

	private static final Logger log = LoggerFactory.getLogger(HeraclesSecurityElementParser.class);

	@Getter
	private Set<String> noneSecurities;

	@Getter
	private List<CustomFilterModel> custFilterModels;

	@Getter
	private CasAuthTypeModel casAuthTypeModel;

	@Getter
	private MetadataSourceModel metadataSourceModel;

	@Getter
	private AccessDecisionManagerModel accessDecisionManagerModel;
	
	@Getter
	private UserDetailsManagerModel userDetailsManagerModel;

	private ParserContext pc;

	private Element configElmt;

	public HeraclesSecurityElementParser(Element configElmt, ParserContext pc) {
		this.configElmt = configElmt;
		this.pc = pc;
	}

	public void parse() {
		// Parse none-security element
		noneSecurities = parseNoneSecurityElmt();

		// Parse custom-filter element
		custFilterModels = parseCustomFilterElmt();

		// Parse auth-type element
		casAuthTypeModel = parseAuthTypeElmt();

		// Parse metadata source element
		metadataSourceModel = parseMetadataSourceElmt();

		// Parse access decision manager element
		accessDecisionManagerModel = parseAccessDecisionManagerElmt();
		
		// Parse user details manager element
		userDetailsManagerModel = parseUserDetailsManagerElmt();
	}

	/**
	 * Parse none security element.
	 * 
	 * @param configElmt
	 * @param pc
	 * @return
	 */
	private Set<String> parseNoneSecurityElmt() {
		Set<String> patternValSet = defaultNoneSecurityPatterns();

		Element noneSecurityElmt = DomUtils.getChildElementByTagName(configElmt, "none-security");
		if (noneSecurityElmt != null) {
			List<Element> patternElts = DomUtils.getChildElementsByTagName(noneSecurityElmt, "pattern");
			for (Element patternElmt : patternElts) {
				String patternVal = patternElmt.getAttribute("value");
				if (StringUtils.isBlank(patternVal)) {
					String errMsg = "Failed to get none security pattern value.";
					pc.getReaderContext().error(errMsg, pc.extractSource(patternElmt));
				}
				patternValSet.add(patternVal);
			}
		}
		return patternValSet;
	}

	/**
	 * Create default none security pattern.
	 * 
	 * @return
	 */
	private Set<String> defaultNoneSecurityPatterns() {
		Set<String> patternValSet = new HashSet<String>();
		patternValSet.add(HEALTH_CHECK_PATTERN);
		patternValSet.add(REST_DOC_PATTERN);
		return patternValSet;
	}

	/**
	 * Parse custom filter element.
	 * 
	 * @param configElmt
	 * @param pc
	 * @return
	 */
	private List<CustomFilterModel> parseCustomFilterElmt() {
		Element custFltsElmt = DomUtils.getChildElementByTagName(configElmt, "custom-filters");
		if (null == custFltsElmt) {
			return Collections.emptyList();
		}
		
		List<Element> custFltrElts = DomUtils.getChildElementsByTagName(custFltsElmt, "custom-filter");
		if (CollectionUtils.isEmpty(custFltrElts)) {
			return Collections.emptyList();
		}

		List<CustomFilterModel> customFilterMdls = new LinkedList<CustomFilterModel>();
		for (Element custFltrElmt : custFltrElts) {
			String ref = custFltrElmt.getAttribute("ref");
			String after = custFltrElmt.getAttribute("after");
			String before = custFltrElmt.getAttribute("before");
			String position = custFltrElmt.getAttribute("position");
			if (StringUtils.isBlank(ref)) {
				String errMsg = "Failed to get ref value in heracles security custom filter.";
				pc.getReaderContext().error(errMsg, pc.extractSource(custFltrElmt));
			}

			CustomFilterModel custFilterMdl = new CustomFilterModel();
			custFilterMdl.setRef(ref);
			custFilterMdl.setAfter(StringUtils.isBlank(after) ? null : after);
			custFilterMdl.setBefore(StringUtils.isBlank(before) ? null : before);
			custFilterMdl.setPosition(StringUtils.isBlank(position) ? null : position);
			customFilterMdls.add(custFilterMdl);
		}
		return customFilterMdls;
	}

	/**
	 * Parse auth type element and construct cas auth type.
	 * 
	 * @param configElmt
	 * @param pc
	 * @return
	 */
	private CasAuthTypeModel parseAuthTypeElmt() {
		Element authTypeElmt = DomUtils.getChildElementByTagName(configElmt, "auth-type");
		if (null == authTypeElmt) {
			String errMsg = "Failed to get auth type, now cas is the only auth type.";
			pc.getReaderContext().error(errMsg, null);
		}

		Element casElmt = DomUtils.getChildElementByTagName(authTypeElmt, "cas");
		if (null == casElmt) {
			String errMsg = "Failed to get auth type, now cas is the only auth type.";
			pc.getReaderContext().error(errMsg, pc.extractSource(authTypeElmt));
		}

		BeanDefinition propBeanDef = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		pc.getDelegate().parsePropertyElements(casElmt, propBeanDef);
		MutablePropertyValues propVals = propBeanDef.getPropertyValues();
		log.info("cas auth bean properties: {}", propVals);

		if (null == propVals || propVals.isEmpty() || null == getPropertyValue(propVals, CAS_SERVER_URL)
				|| null == getPropertyValue(propVals, CLIENT_SERVER_URL)) {
			String errMsg = "Failed to get cas auth type config, cas server url and client server url are mandatory";
			pc.getReaderContext().error(errMsg, pc.extractSource(casElmt));
		}

		CasAuthTypeModel casAuthTypeMdl = createCasAuthTypeModel(propVals);
		return casAuthTypeMdl;
	}

	private CasAuthTypeModel createCasAuthTypeModel(MutablePropertyValues propVals) {
		CasAuthTypeModel casAuthTypeMdl = new CasAuthTypeModel();

		String casServerUrl = getPropertyValue(propVals, CAS_SERVER_URL);
		casAuthTypeMdl.setCasServerUrl(casServerUrl);

		String casLoginUrl = getPropertyValue(propVals, CAS_LOGIN_URL);
		if (null == casLoginUrl) {
			casAuthTypeMdl.setCasLoginUrl(casServerUrl + "/login");
		} else {
			casAuthTypeMdl.setCasLoginUrl(casLoginUrl);
		}

		String clientServerUrl = getPropertyValue(propVals, CLIENT_SERVER_URL);
		casAuthTypeMdl.setClientServerUrl(clientServerUrl);

		String clientServiceUrl = getPropertyValue(propVals, CLIENT_SERVICE_URL);
		if (null == clientServiceUrl) {
			casAuthTypeMdl.setClientServiceUrl(clientServerUrl + "/j_spring_cas_security_check");
		} else {
			casAuthTypeMdl.setClientServiceUrl(clientServiceUrl);
		}

		String defaultTargetUrl = getPropertyValue(propVals, DEFAULT_TARGET_URL);
		if (null == defaultTargetUrl) {
			casAuthTypeMdl.setDefaultTargetUrl("/");
		} else {
			casAuthTypeMdl.setDefaultTargetUrl(defaultTargetUrl);
		}

		String defaultFailureUrl = getPropertyValue(propVals, DEFAULT_FAILURE_URL);
		if (null == defaultFailureUrl) {
			casAuthTypeMdl.setDefaultFailureUrl("/casfailed.jsp");
		} else {
			casAuthTypeMdl.setDefaultFailureUrl(defaultFailureUrl);
		}

		String accessDeniedUrl = getPropertyValue(propVals, ACCESS_DENIED_URL);
		if (null == accessDeniedUrl) {
			casAuthTypeMdl.setAccessDeniedUrl("/403.html");
		} else {
			casAuthTypeMdl.setAccessDeniedUrl(accessDeniedUrl);
		}

		String logoutSuccessUrl = getPropertyValue(propVals, LOGOUT_SUCCESS_URL);
		if (null == logoutSuccessUrl) {
			casAuthTypeMdl.setLogoutSuccessUrl("/cas-logout.jsp");
		} else {
			casAuthTypeMdl.setLogoutSuccessUrl(logoutSuccessUrl);
		}

		String proxyReceptorUrl = getPropertyValue(propVals, PROXY_RECEPTOR_URL);
		if (null == proxyReceptorUrl) {
			casAuthTypeMdl.setProxyReceptorUrl("/secure/receptor");
		} else {
			casAuthTypeMdl.setProxyReceptorUrl(proxyReceptorUrl);
		}

		String proxyCallbackUrl = getPropertyValue(propVals, PROXY_CALLBACK_URL);
		if (null == proxyCallbackUrl) {
			casAuthTypeMdl.setProxyCallbackUrl(clientServerUrl + "/secure/receptor");
		} else {
			casAuthTypeMdl.setProxyCallbackUrl(proxyCallbackUrl);
		}

		String casAuthProviderKey = getPropertyValue(propVals, CAS_AUTH_PROVIDER_KEY);
		if (null == casAuthProviderKey) {
			casAuthTypeMdl.setCasAuthProviderKey("an_id_for_this_auth_provider_only");
		} else {
			casAuthTypeMdl.setCasAuthProviderKey(casAuthProviderKey);
		}

		String casSendRenew = getPropertyValue(propVals, CAS_SEND_RENEW);
		if (null == casSendRenew) {
			casAuthTypeMdl.setCasSendRenew("false");
		} else {
			casAuthTypeMdl.setCasSendRenew(casSendRenew);
		}

		return casAuthTypeMdl;
	}

	public static String getPropertyValue(PropertyValues propVals, String propertyName) {
		PropertyValue propertyValue = propVals.getPropertyValue(propertyName);
		if (null != propertyValue) {
			TypedStringValue strVal = (TypedStringValue) propertyValue.getValue();
			return strVal.getValue();
		}
		return null;
	}

	/**
	 * Parse metadata source element.
	 * 
	 * @param configElmt
	 * @param pc
	 * @return
	 */
	private MetadataSourceModel parseMetadataSourceElmt() {
		String metaClazzName = DEFAULT_METADATA_SOURCE_MAP_CLAZZ;
		Element realmsTagElmt = null;
		Element simpleRealmTagElmt = null;

		Element metadataElmt = DomUtils.getChildElementByTagName(configElmt, "metadata-source");
		if (null != metadataElmt) {
			String clazzName = metadataElmt.getAttribute("class");
			if (StringUtils.isNoneBlank(clazzName)) {
				metaClazzName = clazzName;
			}

			realmsTagElmt = DomUtils.getChildElementByTagName(metadataElmt, "realms");
		}

		ManagedList<Object> realmDefs = null;
		if (null != realmsTagElmt) {
			// get simple realm element
			simpleRealmTagElmt = DomUtils.getChildElementByTagName(realmsTagElmt, "simple-realm");
			
			// get  bean and bean ref elements
			//List<Element> realmsElts = DomUtils.getChildElements(realmsTagElmt);
			List<Element> realmsElts = DomUtils.getChildElementsByTagName(realmsTagElmt, "bean", "ref");
			if (CollectionUtils.isEmpty(realmsElts)) {
				realmDefs = defaultMetadataSourceRealmDefs();
				/*
				String errMsg = "Failed to get metadata source realms.";
				pc.getReaderContext().error(errMsg, pc.extractSource(realmsElts));
				*/
			} else {
				realmDefs = SpringBeanElementParser.parseSpringBeans(realmsElts, pc);
			}
		} else {
			realmDefs = defaultMetadataSourceRealmDefs();
		}

		MetadataSourceModel metadataSourceMdl = new MetadataSourceModel();
		metadataSourceMdl.setClazzName(metaClazzName);
		metadataSourceMdl.setSpringBeans(realmDefs);
		
		if (null != simpleRealmTagElmt) {
			List<Element> itcpUrlElts = DomUtils.getChildElementsByTagName(simpleRealmTagElmt, "intercept-url");
			if (CollectionUtils.isEmpty(itcpUrlElts)) {
				metadataSourceMdl.setInterceptUrls(null);
			} else {
				List<InterceptUrlModel> itctUrls = new LinkedList<InterceptUrlModel>();
				for (Element itcpUrlElmt : itcpUrlElts) {
					String pattern = itcpUrlElmt.getAttribute("pattern");
					if (StringUtils.isBlank(pattern)) {
						String errMsg = "Failed to get pattern value in heracles security simple realm.";
						pc.getReaderContext().error(errMsg, pc.extractSource(itcpUrlElmt));
					}
					
					String method = itcpUrlElmt.getAttribute("method");
					
					String access = itcpUrlElmt.getAttribute("access");
					if (StringUtils.isBlank(pattern)) {
						String errMsg = "Failed to get access value in heracles security simple realm.";
						pc.getReaderContext().error(errMsg, pc.extractSource(itcpUrlElmt));
					}
					
					InterceptUrlModel itctUrlMdl = new InterceptUrlModel();
					itctUrlMdl.setPattern(pattern);
					itctUrlMdl.setMethod(method);
					itctUrlMdl.setAccess(access);
					itctUrls.add(itctUrlMdl);
				}
				metadataSourceMdl.setInterceptUrls(itctUrls);
			}
		}
		
		return metadataSourceMdl;
	}

	private ManagedList<Object> defaultMetadataSourceRealmDefs() {
		ManagedList<Object> realmDefs = new ManagedList<Object>();
		BeanDefinitionBuilder inMemRealmBldr = BeanDefinitionBuilder.genericBeanDefinition(IN_MEMORY_REALM_CLAZZ);
		realmDefs.add(inMemRealmBldr.getBeanDefinition());
		return realmDefs;
	}

	/**
	 * Parse access decision manager element.
	 * 
	 * @param configElmt
	 * @param pc
	 * @return
	 */
	private AccessDecisionManagerModel parseAccessDecisionManagerElmt() {
		String accessDcsnMgmrClazz = DEFAULT_ACCESS_DECISION_MANAGER_CLAZZ;
		Element voterTagElmt = null;

		Element accessDcsnElmt = DomUtils.getChildElementByTagName(configElmt, "access-decision-manager");
		if (accessDcsnElmt != null) {
			String clazzName = accessDcsnElmt.getAttribute("class");
			if (StringUtils.isNotBlank(clazzName)) {
				accessDcsnMgmrClazz = clazzName;
			}

			// voter element
			voterTagElmt = DomUtils.getChildElementByTagName(accessDcsnElmt, "voters");
		}

		ManagedList<Object> voterDefs = null;
		if (voterTagElmt != null) {
			List<Element> voterElts = DomUtils.getChildElements(voterTagElmt);
			if (CollectionUtils.isEmpty(voterElts)) {
				String errMsg = "Failed to get access decision voter.";
				pc.getReaderContext().error(errMsg, pc.extractSource(voterElts));
			}
			voterDefs = SpringBeanElementParser.parseSpringBeans(voterElts, pc);
		} else {
			voterDefs = defaultDecisionVoterDefs();
		}

		AccessDecisionManagerModel accessDecisionManagerMdl = new AccessDecisionManagerModel();
		accessDecisionManagerMdl.setClazzName(accessDcsnMgmrClazz);
		accessDecisionManagerMdl.setSpringBeans(voterDefs);
		return accessDecisionManagerMdl;
	}

	private ManagedList<Object> defaultDecisionVoterDefs() {
		ManagedList<Object> decisionVoterDefs = new ManagedList<Object>();
		BeanDefinitionBuilder atVoterBuilder = BeanDefinitionBuilder.genericBeanDefinition(AuthenticatedVoter.class);
		BeanDefinitionBuilder authVoterBuilder = BeanDefinitionBuilder.genericBeanDefinition(AUTH_SYS_ROLE_VOTER_CLAZZ);
		decisionVoterDefs.add(atVoterBuilder.getBeanDefinition());
		decisionVoterDefs.add(authVoterBuilder.getBeanDefinition());
		return decisionVoterDefs;
	}

	/**
	 * Parse user details manager element.
	 * 
	 * @return
	 */
	private UserDetailsManagerModel parseUserDetailsManagerElmt() {
		String mgrClazz = USER_DETAILS_MANAGER_CLAZZ;
		String usrDtlClazz = USER_DETAILS_CLAZZ;
		Element usrDtlElmt = null;
		
		Element usrDtlMgrElmt = DomUtils.getChildElementByTagName(configElmt, "user-details-manager");
		if (null != usrDtlMgrElmt) {
			// Parse user details manager class name
			String clazzName = usrDtlMgrElmt.getAttribute("class");
			if (StringUtils.isNoneBlank(clazzName)) {
				mgrClazz = clazzName;
			}
			
			usrDtlElmt = DomUtils.getChildElementByTagName(usrDtlMgrElmt, "user-details");
		}
		
		if (null != usrDtlElmt) {
			// Parse user details class name
			String usrDtlclazzName = usrDtlElmt.getAttribute("class");
			if (StringUtils.isNoneBlank(usrDtlclazzName)) {
				usrDtlClazz = usrDtlclazzName;
			}
		}
		
		UserDetailsManagerModel usrDtlMgrMdl = new UserDetailsManagerModel();
		usrDtlMgrMdl.setClazzName(mgrClazz);
		usrDtlMgrMdl.setUserDetailsClazzName(usrDtlClazz);
		return usrDtlMgrMdl;
	}


}
