package heracles.security.access.intercept;

import java.util.List;
import java.util.Map;

/**
 * The realm which will get the RequestToAttributesMap
 * @author kriswang
 *
 */
public interface RequestToAttributesMapRealm
{
	public static final String ROLE_PREFIX_AUTH_SYS = "AuthSys_";

	/**
	 * get the unique-name of this realm, useful to identity the realm
	 * @return
	 */
	public String getName();
	
	/**
	 * get request map
	 * @return
	 */
	public Map<String, Map<String, List<String>>> getRequestToAttributesMap();
	
	
	// public Map<String, String> getRequestToAttributesMap();
}
