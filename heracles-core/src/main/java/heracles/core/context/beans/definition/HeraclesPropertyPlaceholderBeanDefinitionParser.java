package heracles.core.context.beans.definition;

import heracles.core.context.annotation.PropertyChangeHandlerScanner;
import heracles.core.context.annotation.PropertyHandler;
import heracles.core.context.property.HeraclesPropertySourcesPlaceholderConfigurer;
import heracles.core.context.property.impl.PropertyManagerImpl;
import heracles.core.zookeeper.PropertiesUtils;
import heracles.core.zookeeper.PropertyChangedHandler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author kriswang
 * 
 */
public class HeraclesPropertyPlaceholderBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	private static Logger log = LoggerFactory.getLogger(HeraclesPropertyPlaceholderBeanDefinitionParser.class);

	private static final String SYSTEM_PROPERTIES_MODE_ATTRIB = "system-properties-mode";
	private static final String SYSTEM_PROPERTIES_MODE_DEFAULT = "ENVIRONMENT";

	private final PropertyChangeHandlerScanner scanner = new PropertyChangeHandlerScanner();

	private Map<String, String> nameToHandler = new LinkedHashMap<String, String>();
	private String fileName = "";
	private ManagedList<Object> handlersRef = new ManagedList<Object>();

	@Getter
	@Setter
	private Class<? extends Annotation> annotationClass = PropertyHandler.class;

	@Override
	protected Class<?> getBeanClass(Element element) {
		if (element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIB).equals(SYSTEM_PROPERTIES_MODE_DEFAULT)) {
			return HeraclesPropertySourcesPlaceholderConfigurer.class;
		}
		return PropertyPlaceholderConfigurer.class;
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		this.doParse(element, builder);

		/**
		 * 解析propertyManager
		 */
		String managerBeanName = element.getAttribute("manager-name");
		if (!StringUtils.hasLength(managerBeanName)) {
			managerBeanName = "propertyManager";
		}

		this.resolveHandlers(element, parserContext);

		if (nameToHandler != null) {
			try {
				for (Map.Entry<String, String> entry : nameToHandler.entrySet()) {
					RootBeanDefinition propertyChangedHandler = new RootBeanDefinition(Class.forName(entry.getValue()));
					parserContext.getRegistry().registerBeanDefinition(entry.getKey(), propertyChangedHandler);
					handlersRef.add(new RuntimeBeanReference(entry.getKey()));
				}
			} catch (ClassNotFoundException e) {
				String errMsg = "Do operation failed for Class.forName : " + e.getMessage();
				log.error(errMsg, e);
			}
		}

		RootBeanDefinition propertyManager = new RootBeanDefinition(PropertyManagerImpl.class);
		propertyManager.getPropertyValues().add("paths", PropertiesUtils.getZkPaths());
		propertyManager.getPropertyValues().add("fileName", fileName);
		propertyManager.getPropertyValues().add("handlers", handlersRef);
		parserContext.getRegistry().registerBeanDefinition(managerBeanName, propertyManager);
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		/**
		 * 支持spring-context
		 */
		super.doParse(element, builder);

		/**
		 * init all props
		 */
		PropertiesUtils.getPropsConfig(element);

		String systemPropertiesModeName = element.getAttribute(SYSTEM_PROPERTIES_MODE_ATTRIB);
		if (StringUtils.hasLength(systemPropertiesModeName)
				&& !systemPropertiesModeName.equals(SYSTEM_PROPERTIES_MODE_DEFAULT)) {
			builder.addPropertyValue("systemPropertiesModeName", "SYSTEM_PROPERTIES_MODE_" + systemPropertiesModeName);
		}
	}

	/**
	 * 解析handlers
	 */
	private void resolveHandlers(Element element, ParserContext parserContext) {
		NodeList nodeList = element.getChildNodes();
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node != null && node instanceof Element) {
					Element ele = (Element) node;
					if (ele.getTagName().endsWith("property-handlers")) {
						NodeList handlerList = ele.getChildNodes();
						if (handlerList != null && handlerList.getLength() > 0) {
							for (int j = 0; j < handlerList.getLength(); j++) {
								Node handler = handlerList.item(j);
								if (handler != null && handler instanceof Element) {
									Element beanEle = (Element) handler;
									if (beanEle.getTagName().endsWith("handlers-scan")) {
										Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();
										String hotreloadhandlers = beanEle.getAttribute("base-package");
										if (StringUtils.hasLength(hotreloadhandlers)) {
											scanner.addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
											clazzCollection = scanner.scan(hotreloadhandlers);
										}
										if (CollectionUtils.isNotEmpty(clazzCollection)) {
											for (Class<?> clazz : clazzCollection) {
												try {
													if (PropertyChangedHandler.class.isAssignableFrom(clazz)) {
														nameToHandler.put(clazz.getName(), clazz.getName());
													}
												} catch (Exception e) {
													log.warn("can not find class for [" + clazz + "]");
													continue;
												}
											}
										}
									}

									if (beanEle.getTagName().endsWith("bean")) {
										BeanDefinitionHolder beanDefHolder = parserContext.getDelegate()
												.parseBeanDefinitionElement(beanEle);
										handlersRef.add(beanDefHolder.getBeanDefinition());
									} else if (beanEle.getTagName().endsWith("ref")) {
										Object refObj = parserContext.getDelegate().parseIdRefElement(beanEle);
										RuntimeBeanNameReference nameRef = (RuntimeBeanNameReference) refObj;

										RuntimeBeanReference beanRef = new RuntimeBeanReference(nameRef.getBeanName());
										beanRef.setSource(nameRef.getSource());
										handlersRef.add(beanRef);
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
