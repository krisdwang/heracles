package heracles.jdbc.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AllinOneNamespaceHandlerSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("allinone-datasource", new AllinOneDatasourceBeanDefinitionParser());
	}
}
