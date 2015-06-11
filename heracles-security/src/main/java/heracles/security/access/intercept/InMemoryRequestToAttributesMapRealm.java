package heracles.security.access.intercept;

import heracles.security.access.intercept.RequestToAttributesMapRealm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author kriswang
 *
 */
public class InMemoryRequestToAttributesMapRealm implements RequestToAttributesMapRealm {

	private String name = "inMemoryRequestToAttributesMapRealm";

	private static final Logger log = LoggerFactory.getLogger(InMemoryRequestToAttributesMapRealm.class);

	@Setter
	private Map<String, Map<String, List<String>>> reqToAttrMap = new HashMap<String, Map<String, List<String>>>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, Map<String, List<String>>> getRequestToAttributesMap() {
		Map<String, Map<String, List<String>>> inMemReqToAttrMap = new HashMap<String, Map<String, List<String>>>();

		for (String url : reqToAttrMap.keySet()) {
			Map<String, List<String>> methodMap = reqToAttrMap.get(url);
			Map<String, List<String>> newMap = new HashMap<String, List<String>>();
			
			for (String method : methodMap.keySet()) {
				List<String> acesList = methodMap.get(method);
				List<String> newAcesList = new LinkedList<String>();
				for (String access : acesList) {
					// add authsys prefix.
					newAcesList.add(ROLE_PREFIX_AUTH_SYS + access);
				}
				newMap.put(method, newAcesList);
			}
			inMemReqToAttrMap.put(url, newMap);
			log.info("InMemoryRealm - url:{}, method:{}", url, methodMap);
		}

		return inMemReqToAttrMap;
	}

}
