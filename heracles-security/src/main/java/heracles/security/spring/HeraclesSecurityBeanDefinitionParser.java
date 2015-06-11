package heracles.security.spring;

import heracles.security.spring.configurer.HeraclesSecurityConfigurer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class HeraclesSecurityBeanDefinitionParser extends AbstractSingleBeanDefinitionParser implements
		BeanDefinitionParser, HeraclesSecurityConstants {

	private static final Logger log = LoggerFactory.getLogger(HeraclesSecurityBeanDefinitionParser.class);

	@Override
	protected Class<?> getBeanClass(Element element) {
		return HeraclesSecurityConfigurer.class;
	}

	@Override
	protected void doParse(Element configElmt, ParserContext pc, BeanDefinitionBuilder secConfigBuilder) {
		super.doParse(configElmt, secConfigBuilder);

		// Parse heracles security config element
		HeraclesSecurityElementParser elmtParser = new HeraclesSecurityElementParser(configElmt, pc);
		elmtParser.parse();
		log.info("Heracles security - element parse completed!");

		// Build Heracles security bean definition
		HeraclesSecurityBeanDefinitionBuilder beanDefBldr = new HeraclesSecurityBeanDefinitionBuilder(elmtParser, pc);
		beanDefBldr.buildBeanDefinition();
		log.info("Heracles security - bean definition build completed!");

		// Handle spring security related.
		SpringSecurityNamespaceHandler secuHdlr = new SpringSecurityNamespaceHandler(configElmt, elmtParser, pc);
		secuHdlr.parseSpringSecurity();
		log.info("Heracles security - spring security namespace element parse completed!");
	}


	@Override
	protected boolean shouldGenerateId() {
		return true;
	}

}
