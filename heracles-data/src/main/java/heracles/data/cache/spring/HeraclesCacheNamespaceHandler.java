package heracles.data.cache.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author kriswang
 *
 */
public class HeraclesCacheNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("cache-manager", new HeraclesCacheBeanDefinitionParser());
		
	}
	
}
