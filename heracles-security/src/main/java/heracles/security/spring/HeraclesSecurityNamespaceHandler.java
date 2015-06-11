package heracles.security.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class HeraclesSecurityNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("config", new HeraclesSecurityBeanDefinitionParser());
	}

}
