package heracles.data.cache.spring;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import heracles.core.spring.SpringBeanElementParser;
import heracles.data.cache.config.model.CacheClusterModel;
import heracles.data.cache.config.model.CacheManagerModel;
import heracles.data.cache.config.model.CacheType;
import heracles.data.cache.config.model.PoolConfigModel;
import heracles.data.cache.config.model.RedisPoolConfigModel;
import heracles.data.cache.config.model.SerializerConfigModel;
import heracles.data.cache.config.model.SerializerType;
import heracles.data.cache.utils.Utils;

/**
 * @author kriswang
 *
 */
public class HeraclesCacheElementParser implements HeraclesCacheConstants {

	@Getter
	private CacheManagerModel cacheManagerModel;

	private ParserContext pc;

	private Element rootElmt;

	private static final Logger log = LoggerFactory.getLogger(HeraclesCacheElementParser.class);

	public HeraclesCacheElementParser(Element rootElmt, ParserContext pc) {
		this.rootElmt = rootElmt;
		this.pc = pc;
	}

	public void parse() {
		// Parse cache-manager element
		cacheManagerModel = parseCacheManagerElmt();
	}

	/**
	 * Parse cache-manager element
	 * 
	 * @return
	 */
	private CacheManagerModel parseCacheManagerElmt() {
		String caheMgmrName = rootElmt.getAttribute(NAME);
		if (StringUtils.isBlank(caheMgmrName)) {
			String errMsg = "Failed to get cache manager name, cache manager name is mandatory";
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, pc.extractSource(rootElmt));
		}

		CacheManagerModel caheMgmrMdl = new CacheManagerModel();
		caheMgmrMdl.setName(caheMgmrName);
		
		// Parse caches element
		parseCachesElmt(caheMgmrMdl);

		log.info("Element cache-manager:{}", caheMgmrMdl);
		return caheMgmrMdl;
	}

	private void parseCachesElmt(CacheManagerModel caheMgmrMdl) {
		Element cahesElmt = DomUtils.getChildElementByTagName(rootElmt, "caches");
		if (null == cahesElmt) {
			List<CacheClusterModel> eptyClstList = Collections.emptyList();
			caheMgmrMdl.setCacheClusterModels(eptyClstList);
			List<Element> eptyElts = Collections.emptyList();
			caheMgmrMdl.setSpringBeans(SpringBeanElementParser.parseSpringBeans(eptyElts, pc));
			
			caheMgmrMdl.setKeyPrefix(null);
			caheMgmrMdl.setExpiration(null);
			caheMgmrMdl.setSerializerConfigModel(new SerializerConfigModel());
		} else {
			parseCachesAttributes(caheMgmrMdl, cahesElmt);
			caheMgmrMdl.setSerializerConfigModel(parseSerializerConfig(cahesElmt));
			caheMgmrMdl.setCacheClusterModels(parseCacheClusterElts(cahesElmt));
			caheMgmrMdl.setSpringBeans(parseSpringBeans(cahesElmt));
		}
	}
	
	private void parseCachesAttributes(CacheManagerModel caheMgmrMdl, Element cahesElmt) {
		String caheKeyPfx = cahesElmt.getAttribute(KEY_PREFIX);
		if (StringUtils.isNotBlank(caheKeyPfx)) {
			caheMgmrMdl.setKeyPrefix(caheKeyPfx);
		}
		
		String caheExpn = cahesElmt.getAttribute(EXPIRATION);
		if (StringUtils.isNotBlank(caheExpn)) {
			caheMgmrMdl.setExpiration(convertExpiration(caheExpn));
		}
	}

	private Long convertExpiration(String caheExpn) {
		Long val = null;
		try {
			val = Long.valueOf(caheExpn);
		} catch (Exception e) {
			String errMsg = "Failed to get long value, please provide correct expiration long value: " + caheExpn;
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, pc.extractSource(rootElmt));
		}
		return val;
	}

	private SerializerConfigModel parseSerializerConfig(Element cahesElmt) {
		SerializerConfigModel srlzCfgMdl = new SerializerConfigModel();
		Element srlzElmt = DomUtils.getChildElementByTagName(cahesElmt, "serializer-config");
		if (null != srlzElmt) {
			parseChildSerializerElement(srlzElmt, KEY_SERIALIZER, srlzCfgMdl);
			parseChildSerializerElement(srlzElmt, VALUE_SERIALIZER, srlzCfgMdl);
			parseChildSerializerElement(srlzElmt, HASH_KEY_SERIALIZER, srlzCfgMdl);
			parseChildSerializerElement(srlzElmt, HASH_VALUE_SERIALIZER, srlzCfgMdl);
		}
		return srlzCfgMdl;
	}

	private void parseChildSerializerElement(Element srlzElmt, String chldSrlzElmtName, SerializerConfigModel srlzCfgMdl) {
		Element chldSrlzElmt = DomUtils.getChildElementByTagName(srlzElmt, chldSrlzElmtName);
		if (null != chldSrlzElmt) {
			String chldSrlzTypeStr = chldSrlzElmt.getAttribute(TYPE);
			String chldSrlzClass = chldSrlzElmt.getAttribute(CLAZZ);
			if (StringUtils.isNotBlank(chldSrlzTypeStr) && StringUtils.isNotBlank(chldSrlzClass)) {
				String errMsg = "Failed to parse serializer, please provide only type or class in one config entry! type="
						+ chldSrlzTypeStr + ", class=" + chldSrlzClass;
				log.error(errMsg);
				pc.getReaderContext().error(errMsg, pc.extractSource(rootElmt));
			}

			SerializerType chldSrlzType = null;
			if (StringUtils.isNotBlank(chldSrlzTypeStr)) {
				chldSrlzType = Utils.getEnumFromString(chldSrlzTypeStr, SerializerType.class, pc);
			}
			if (StringUtils.isBlank(chldSrlzClass)) {
				chldSrlzClass = null;
			}
			
			if (chldSrlzElmtName == KEY_SERIALIZER) {
				srlzCfgMdl.setKeySerializerType(chldSrlzType);
				srlzCfgMdl.setKeySerializerClass(chldSrlzClass);
			}
			if (chldSrlzElmtName == VALUE_SERIALIZER) {
				srlzCfgMdl.setValueSerializerType(chldSrlzType);
				srlzCfgMdl.setValueSerializerClass(chldSrlzClass);
			}
			if (chldSrlzElmtName == HASH_KEY_SERIALIZER) {
				srlzCfgMdl.setHashKeySerializerType(chldSrlzType);
				srlzCfgMdl.setHashKeySerializerClass(chldSrlzClass);
			}
			if (chldSrlzElmtName == HASH_VALUE_SERIALIZER) {
				srlzCfgMdl.setHashValueSerializerType(chldSrlzType);
				srlzCfgMdl.setHashValueSerializerClass(chldSrlzClass);
			}
		}
	}

	/**
	 * Parse cache-cluster element
	 * 
	 * @return
	 */
	private List<CacheClusterModel> parseCacheClusterElts(Element cahesElmt) {
		List<Element> clstElts = DomUtils.getChildElementsByTagName(cahesElmt, "cache-cluster");
		if (CollectionUtils.isEmpty(clstElts)) {
			return Collections.emptyList();
		} else {
			List<CacheClusterModel> clstMdls = new LinkedList<CacheClusterModel>();
			for (Element clstElmt : clstElts) {
				CacheClusterModel clstMdl = parseCacheClusterElmt(clstElmt);
				clstMdls.add(clstMdl);
			}
			return clstMdls;
		}
		
	}
	
	private ManagedList<Object> parseSpringBeans(Element cahesElmt) {
		List<Element> beanElts = DomUtils.getChildElementsByTagName(cahesElmt, "bean", "ref");
		return SpringBeanElementParser.parseSpringBeans(beanElts, pc);
	}

	private CacheClusterModel parseCacheClusterElmt(Element clstElmt) {
		String name = clstElmt.getAttribute(NAME);
		String clstType = clstElmt.getAttribute(TYPE);
		checkCacheClusterAttribute(name, clstType, clstElmt);

		String clstName = clstElmt.getAttribute(CLUSTER_NAME);
		if (StringUtils.isBlank(clstName)) {
			clstName = name;
		}
		
		String clstTpltName = clstElmt.getAttribute(TEMPLATE_NAME);
		if (StringUtils.isBlank(clstTpltName)) {
			clstTpltName = name + "Template";
		}
		
		String clstKeyPfx = clstElmt.getAttribute(KEY_PREFIX);
		String clstExpn = clstElmt.getAttribute(EXPIRATION);

		CacheClusterModel clstMdl = new CacheClusterModel();
		clstMdl.setName(name);
		clstMdl.setClusterName(clstName);
		clstMdl.setType(Utils.getEnumFromString(clstType, CacheType.class, pc));
		clstMdl.setTemplateName(clstTpltName);

		if (StringUtils.isNotBlank(clstKeyPfx)) {
			clstMdl.setKeyPrefix(clstKeyPfx);
		}
		if (StringUtils.isNotBlank(clstExpn)) {
			clstMdl.setExpiration(convertExpiration(clstExpn));
		}
		
		clstMdl.setPoolConfigModel(parsePoolConfElmt(clstElmt, clstMdl));
		clstMdl.setSerializerConfigModel(parseSerializerConfig(clstElmt));

		log.info("Element cache-cluster:{}", clstMdl);
		return clstMdl;
	}

	private void checkCacheClusterAttribute(String name, String clstType, Element clstElmt) {
		if (StringUtils.isBlank(name)) {
			String errMsg = "Failed to get cache name";
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, pc.extractSource(clstElmt));
		}

		if (StringUtils.isBlank(clstType)) {
			String errMsg = "Failed to get cache type";
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, pc.extractSource(clstElmt));
		}
	}

	/**
	 * Parse pool config element
	 * 
	 * @param clstElmt
	 * @param clstMdl
	 * @return
	 */
	private PoolConfigModel parsePoolConfElmt(Element clstElmt, CacheClusterModel clstMdl) {
		Element poolConfElmt = DomUtils.getChildElementByTagName(clstElmt, "pool-config");
		if (null == poolConfElmt) {
			log.warn("Element pool-config does not exist");
			return null;
		}

		BeanDefinition propBeanDef = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
		pc.getDelegate().parsePropertyElements(poolConfElmt, propBeanDef);
		MutablePropertyValues propVals = propBeanDef.getPropertyValues();
		if (null == propVals || propVals.isEmpty()) {
			log.warn("Element pool-config does not have properties");
			return null;
		}

		PoolConfigModel poolConfMdl = null;
		if (CacheType.REDIS == clstMdl.getType()) {
			poolConfMdl = createRedisPoolConfigModel(propVals);
		} else {
			String errMsg = "Unsupported cache cluster type: " + clstMdl.getType();
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, pc.extractSource(clstElmt));
		}

		log.info("Element pool-config:{}", poolConfMdl);
		return poolConfMdl;
	}

	private RedisPoolConfigModel createRedisPoolConfigModel(MutablePropertyValues propVals) {
		RedisPoolConfigModel rdsPoolConfMdl = new RedisPoolConfigModel();
		String maxTotal = getPropertyValue(propVals, MAX_TOTAL);
		rdsPoolConfMdl.setMaxTotal(maxTotal);

		String maxIdle = getPropertyValue(propVals, MAX_IDLE);
		rdsPoolConfMdl.setMaxIdle(maxIdle);

		String timeBetweenEvictionRunsMillis = getPropertyValue(propVals, TIME_BETWEEN_EVICTION_RUNS_MILLIS);
		rdsPoolConfMdl.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

		String minEvictableIdleTimeMillis = getPropertyValue(propVals, MIN_EVICTABLE_IDLE_TIME_MILLIS);
		rdsPoolConfMdl.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

		String maxWaitMillis = getPropertyValue(propVals, MAX_WAIT_MILLIS);
		rdsPoolConfMdl.setMaxWaitMillis(maxWaitMillis);
		return rdsPoolConfMdl;
	}

	public static String getPropertyValue(PropertyValues propVals, String propertyName) {
		PropertyValue propertyValue = propVals.getPropertyValue(propertyName);
		if (null != propertyValue) {
			TypedStringValue strVal = (TypedStringValue) propertyValue.getValue();
			return strVal.getValue();
		}
		return null;
	}

}
