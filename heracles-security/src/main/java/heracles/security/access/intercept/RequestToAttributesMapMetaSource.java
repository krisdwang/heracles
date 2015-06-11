package heracles.security.access.intercept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * get RequestToAttributesMap by different realms
 * <p>
 * The realm is the final source of RequestToAttributesMap
 * </p>
 * 
 * @author kriswang
 * 
 */
public class RequestToAttributesMapMetaSource implements
		FactoryBean<Map<RequestMatcher, Collection<ConfigAttribute>>> {
	private Collection<RequestToAttributesMapRealm> realms;

	@Setter
	private SimpleRequestToAttributesRealm simpleRealm;

	public Map<RequestMatcher, Collection<ConfigAttribute>> getObject()
			throws Exception {
		if (null == realms || realms.isEmpty()) {
			throw new IllegalArgumentException("Realms must be not empty");
		}
		Map<RequestMatcher, Collection<ConfigAttribute>> requestToAttributesMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();
		// Combine different realm's data
		buildRealmsMap(requestToAttributesMap);

		buildSimpleRealmMap(requestToAttributesMap);
		return requestToAttributesMap;
	}

	private void buildRealmsMap(
			Map<RequestMatcher, Collection<ConfigAttribute>> requestToAttributesMap) {
		for (RequestToAttributesMapRealm realm : realms) {
			Map<String, Map<String, List<String>>> originalMap = realm
					.getRequestToAttributesMap();
			for (Entry<String, Map<String, List<String>>> urlMap : originalMap
					.entrySet()) {
				String url = urlMap.getKey();
				for (Entry<String, List<String>> methodEntry : urlMap
						.getValue().entrySet()) {
					RequestMatcher matcher = new AntPathRequestMatcher(url,
							methodEntry.getKey().toUpperCase());
					List<ConfigAttribute> configAttributeList = new LinkedList<ConfigAttribute>();
					for (String configAttributeValue : methodEntry.getValue()) {
						configAttributeList.add(new SecurityConfig(
								configAttributeValue));
					}
					requestToAttributesMap.put(matcher, configAttributeList);
				}
			}
		}
	}

	private void buildSimpleRealmMap(
			Map<RequestMatcher, Collection<ConfigAttribute>> requestToAttributesMap) {
		List<InterceptUrlModel> simpleRealms = simpleRealm
				.getSimpleRequestToAttributesRealm();
		if (CollectionUtils.isNotEmpty(simpleRealms)) {
			for (InterceptUrlModel smpRlmMdl : simpleRealms) {
				RequestMatcher matcher;
				String pattern = smpRlmMdl.getPattern();
				String method = smpRlmMdl.getMethod();
				String access = smpRlmMdl.getAccess();

				if (StringUtils.isEmpty(method)) {
					matcher = new AntPathRequestMatcher(pattern);
				} else {
					matcher = new AntPathRequestMatcher(pattern,
							method.toUpperCase());
				}

				List<ConfigAttribute> configAttributeList = new LinkedList<ConfigAttribute>();
				SecurityConfig securityConfig = new SecurityConfig(access);
				configAttributeList.add(securityConfig);
				requestToAttributesMap.put(matcher, configAttributeList);
			}
		}
	}

	/**
	 * set one realm
	 * 
	 * @param realm
	 */
	public void setRealm(RequestToAttributesMapRealm realm) {
		if (realm == null) {
			throw new IllegalArgumentException("Realm argument cannot be null");
		}
		Collection<RequestToAttributesMapRealm> realms = new ArrayList<RequestToAttributesMapRealm>(
				1);
		realms.add(realm);
		setRealms(realms);
	}

	/**
	 * set realms
	 * 
	 * @param realms
	 */
	public void setRealms(Collection<RequestToAttributesMapRealm> realms) {
		if (realms == null) {
			throw new IllegalArgumentException(
					"Realms collection argument cannot be null.");
		}
		if (realms.isEmpty()) {
			throw new IllegalArgumentException(
					"Realms collection argument cannot be empty.");
		}
		this.realms = realms;
	}

	public Collection<RequestToAttributesMapRealm> getRealms() {
		return realms;
	}

	public Class<?> getObjectType() {
		return Map.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
