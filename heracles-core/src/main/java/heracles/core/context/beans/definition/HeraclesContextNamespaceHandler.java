package heracles.core.context.beans.definition;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 
 * @author kriswang
 *
 */
public class HeraclesContextNamespaceHandler extends NamespaceHandlerSupport {  
  
    public void init() {  
        registerBeanDefinitionParser("property-placeholder", new HeraclesPropertyPlaceholderBeanDefinitionParser());  
    }  
}
