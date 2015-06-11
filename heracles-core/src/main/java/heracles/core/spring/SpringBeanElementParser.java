package heracles.core.spring;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class SpringBeanElementParser {

	private static final Logger log = LoggerFactory.getLogger(SpringBeanElementParser.class);

	/**
	 * Parse Spring beans, refs.
	 * 
	 * @param beanElts
	 * @param pc
	 * @return
	 */
	public static ManagedList<Object> parseSpringBeans(List<Element> beanElts, ParserContext pc) {
		BeanDefinitionParserDelegate beanDefDelegate = pc.getDelegate();

		ManagedList<Object> springBeanDefs = new ManagedList<Object>();
		for (Element beanElmt : beanElts) {
			String tagName = beanElmt.getLocalName();
			if (StringUtils.equals(tagName, "bean")) {
				BeanDefinitionHolder beanDefHolder = beanDefDelegate.parseBeanDefinitionElement(beanElmt);
				beanDefHolder.getBeanName();
				log.info("parseSpringBeans - bean name:{}", beanDefHolder.getBeanName());

				springBeanDefs.add(beanDefHolder.getBeanDefinition());
			} else if (StringUtils.equals(tagName, "ref")) {
				Object refObj = beanDefDelegate.parseIdRefElement(beanElmt);
				RuntimeBeanNameReference nameRef = (RuntimeBeanNameReference) refObj;
				log.info("parseSpringBeans - ref bean name:{}", nameRef.getBeanName());

				RuntimeBeanReference beanRef = new RuntimeBeanReference(nameRef.getBeanName());
				beanRef.setSource(nameRef.getSource());
				springBeanDefs.add(beanRef);
			} else {
				String errMsg = String.format(
						"Heracles only support spring bean and ref tag element, but %s element is provided",
						tagName);
				log.error(errMsg, tagName);
				pc.getReaderContext().error(errMsg, pc.extractSource(beanElmt));
			}
		}
		return springBeanDefs;
	}
}
