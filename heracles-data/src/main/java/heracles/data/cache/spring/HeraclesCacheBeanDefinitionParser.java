package heracles.data.cache.spring;

import heracles.core.zookeeper.PropertiesUtils;
import heracles.data.cache.config.model.CacheClusterModel;
import heracles.data.cache.config.model.CacheManagerModel;
import heracles.data.cache.resource.model.CacheModel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author kriswang
 * 
 */
public class HeraclesCacheBeanDefinitionParser extends
		AbstractBeanDefinitionParser implements BeanDefinitionParser,
		HeraclesCacheConstants {

	private static final Logger log = LoggerFactory.getLogger(HeraclesCacheBeanDefinitionParser.class);

	@Override
	protected AbstractBeanDefinition parseInternal(Element rootElmt,
			ParserContext pc) {
		/**
		 * init all props
		 */
		PropertiesUtils.getPropsConfig(rootElmt);

		// Parse heracles cache manager element
		HeraclesCacheElementParser elmtParser = new HeraclesCacheElementParser(rootElmt, pc);
		elmtParser.parse();
		log.info("Heracles cache - element parse completed!");

		// Get cache cluster configuration from local or zk
		CacheManagerModel caheMgmrMdl = elmtParser.getCacheManagerModel();
		List<CacheClusterModel> caheClstMdls = caheMgmrMdl
				.getCacheClusterModels();
		for (CacheClusterModel caheClstMdl : caheClstMdls) {
			HeraclesCacheZkHandler caheZkHdlr = new HeraclesCacheZkHandler(
					caheClstMdl.getClusterName(), caheClstMdl.getType(), pc);
			CacheModel cacheModel = caheZkHdlr.getCacheConfig();
			caheClstMdl.setCacheModel(cacheModel);
		}
		log.info("Heracles cache - get cache cluster configuration completed!");

		// Build Heracles cache bean definition
		HeraclesCacheBeanDefinitionBuilder beanDefBldr = new HeraclesCacheBeanDefinitionBuilder(
				caheMgmrMdl, pc);
		beanDefBldr.buildBeanDefinition();
		log.info("Heracles cache - bean definition build completed!");

		// Handle spring cache related
		SpringCacheNamespaceHandler cheHdlr = new SpringCacheNamespaceHandler(
				rootElmt, elmtParser, pc);
		cheHdlr.parseSpringCache();
		log.info("Heracles cache - spring cache namespace element parse completed!");

		return null;
	}

	@Override
	protected boolean shouldGenerateId() {
		return true;
	}

}
