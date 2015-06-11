package heracles.data.cache.spring;

import heracles.data.cache.config.model.CacheClusterModel;
import heracles.data.cache.config.model.CacheManagerModel;
import heracles.data.cache.config.model.CacheType;
import heracles.data.cache.config.model.NodeState;
import heracles.data.cache.config.model.RedisPoolConfigModel;
import heracles.data.cache.config.model.SerializerConfigModel;
import heracles.data.cache.config.model.SerializerType;
import heracles.data.cache.resource.model.CacheModel;
import heracles.data.cache.resource.model.CacheNodeModel;
import heracles.data.cache.utils.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * @author kriswang
 *
 */
public class HeraclesCacheBeanDefinitionBuilder implements HeraclesCacheConstants {

	private CacheManagerModel cacheManagerModel;
	
	private ParserContext pc;

	private static final Logger LOGGER = LoggerFactory.getLogger(HeraclesCacheBeanDefinitionBuilder.class);
	
	private static final Map<SerializerType, String> SERIALIZER_TYPE_MAP = Collections.unmodifiableMap(new HashMap<SerializerType, String>(){
		private static final long serialVersionUID = -5160449041907680338L;

		{
			put(SerializerType.STRING, SERIALZER_STRING_CLAZZ);
			put(SerializerType.JDK, SERIALZER_JDK_CLAZZ);
			/*
			put(SerializerType.XML, SERIALZER_XML_CLAZZ);
			put(SerializerType.JACKSON, SERIALZER_JACKSON_CLAZZ);
			put(SerializerType.JACKSON2, SERIALZER_JACKSON2_CLAZZ);
			put(SerializerType.GENERIC2STRING, SERIALZER_GENERIC2STRING_CLAZZ);
			*/
		}
	});

	public HeraclesCacheBeanDefinitionBuilder(CacheManagerModel cacheManagerModel, ParserContext pc) {
		this.cacheManagerModel = cacheManagerModel;
		this.pc = pc;
	}

	public void buildBeanDefinition() {
		CacheManagerModel caheMgmrMdl = cacheManagerModel;
		Set<String> caheMgmrNames = new HashSet<String>();
		
		List<CacheClusterModel> clstMdls = caheMgmrMdl.getCacheClusterModels();
		for (CacheClusterModel clstMdl : clstMdls) {
			CacheModel caheMdl = clstMdl.getCacheModel();
			
			if (clstMdl.getType() != caheMdl.getType()) {
				String errMsg = "Failed to get consistent cache type, cache manager type:" + clstMdl.getType()
						+ ", cache type from zk:" + caheMdl.getType();
				LOGGER.error(errMsg);
				pc.getReaderContext().error(errMsg, null);
			}
			
			if (CacheType.REDIS == caheMdl.getType()) {
				buildRedisCacheBeanDef(clstMdl, caheMgmrNames);
			} else {
				// FIXME feature support other cache type in the future
				String errMsg = "Unsupported cache type:" + caheMdl.getType();
				LOGGER.error(errMsg);
				pc.getReaderContext().error(errMsg, null);
			}
		}
		
		buildCompositeCacheManager(caheMgmrMdl, caheMgmrNames);
	}


	private void buildCompositeCacheManager(CacheManagerModel caheMgmrMdl, Set<String> caheMgmrNames) {
		BeanDefinitionBuilder cpstCaheMgmrBldr = BeanDefinitionBuilder.genericBeanDefinition(COMPOSITE_CACHE_MANAGER);
		cpstCaheMgmrBldr.addPropertyValue(FAILBACK_NO_OP_CACHE, false);
		
		ManagedList<Object> caheRefMgmrs = new ManagedList<Object>(caheMgmrNames.size());
		for (String caheMgmrName : caheMgmrNames) {
			RuntimeBeanReference ref = new RuntimeBeanReference(caheMgmrName);
			caheRefMgmrs.add(ref);
		}
		
		caheRefMgmrs.addAll(caheMgmrMdl.getSpringBeans());
		cpstCaheMgmrBldr.addPropertyValue("cacheManagers", caheRefMgmrs);
		pc.getRegistry().registerBeanDefinition(caheMgmrMdl.getName(), cpstCaheMgmrBldr.getBeanDefinition());
	}

	private void buildRedisCacheBeanDef(CacheClusterModel clstMdl, Set<String> caheMgmrNames) {
		CacheModel caheMdl = clstMdl.getCacheModel();
		String caheName = clstMdl.getName();
		String poolBeanName = caheName + JEDIS_POOL_CONFIG;
		String connFctyName = caheName + JEDIS_CONNECTION_FACTORY;
		String caheMgmrName = caheName + CACHE_MANAGER;
		
		// build redis pool config bean definition
		RedisPoolConfigModel rdsPolCfgMdl = (RedisPoolConfigModel) clstMdl.getPoolConfigModel();
		buildRedisPoolConfigBeanDef(rdsPolCfgMdl, poolBeanName, pc);

		// build redis connection factory definition
		buildJedisConnectionFactoryBeanDef(caheMdl, poolBeanName, connFctyName, pc);

		// build redis template bean definition
		buildRedisTemplateBeanDef(clstMdl, connFctyName);

		// build redis cache manager bean definition
		buildRedisTwemproxyCacheManagerBeanDef(clstMdl, caheName, caheMgmrName);
		
		caheMgmrNames.add(caheMgmrName);
	}

	private void buildRedisTwemproxyCacheManagerBeanDef(CacheClusterModel clstMdl, String caheName, String caheMgmrName) {
		BeanDefinitionBuilder rdsCheMgrBldr = BeanDefinitionBuilder
				.genericBeanDefinition(REDIS_TWEMPROXY_CACHE_MANAGER_CLAZZ);
		rdsCheMgrBldr.addConstructorArgReference(clstMdl.getTemplateName());
		rdsCheMgrBldr.addConstructorArgValue(Arrays.asList(caheName));
		
		String caheKeyPfx = getCacheDefaultKeyPrefix(clstMdl);
		if (null != caheKeyPfx) {
			rdsCheMgrBldr.addConstructorArgValue(true);
			rdsCheMgrBldr.addConstructorArgValue(caheKeyPfx);
		} else {
			rdsCheMgrBldr.addConstructorArgValue(false);
			rdsCheMgrBldr.addConstructorArgValue(null);
		}
		
		Long caheExpn = getCacheDefaultExpiration(clstMdl);
		if (null != caheExpn) {
			rdsCheMgrBldr.addConstructorArgValue(caheExpn.longValue());
		} else {
			rdsCheMgrBldr.addConstructorArgValue(DEFAULT_EXPIRATION_VALUE);
		}
		
		pc.getRegistry().registerBeanDefinition(caheMgmrName, rdsCheMgrBldr.getBeanDefinition());
	}
	
	private Long getCacheDefaultExpiration(CacheClusterModel clstMdl) {
		Long caheExpn = cacheManagerModel.getExpiration();
		Long clstExpn = clstMdl.getExpiration();
		if (null != clstExpn) {
			caheExpn = clstExpn;
		}
		return caheExpn;
	}
	
	private String getCacheDefaultKeyPrefix(CacheClusterModel clstMdl) {
		String caheKeyPfx = cacheManagerModel.getKeyPrefix();
		String clstKeyPfx = clstMdl.getKeyPrefix();
		if (null != clstKeyPfx) {
			caheKeyPfx = clstKeyPfx;
		}
		return caheKeyPfx;
	}

	private void buildRedisTemplateBeanDef(CacheClusterModel clstMdl, String connFctyName) {
		BeanDefinitionBuilder rdsTpltBldr = BeanDefinitionBuilder.genericBeanDefinition(REDIS_TWEMPROXY_TEMPLATE_CLAZZ);
		rdsTpltBldr.addPropertyReference(CONNECTION_FACTORY_PROPERTY, connFctyName);

		SerializerConfigModel tagtSrlzMdl = getCacheSerializer(clstMdl.getSerializerConfigModel());
		validateValueSerializer(tagtSrlzMdl);
		
		BeanDefinitionBuilder keySrlzBldr = BeanDefinitionBuilder.genericBeanDefinition(tagtSrlzMdl
				.getKeySerializerClass());
		rdsTpltBldr.addPropertyValue(KEY_SERIALIZER_PROPERTY, keySrlzBldr.getBeanDefinition());

		BeanDefinitionBuilder valSrlzBldr = BeanDefinitionBuilder.genericBeanDefinition(tagtSrlzMdl
				.getValueSerializerClass());
		rdsTpltBldr.addPropertyValue(VALUE_SERIALIZER_PROPERTY, valSrlzBldr.getBeanDefinition());

		BeanDefinitionBuilder hashKeySrlzBldr = BeanDefinitionBuilder.genericBeanDefinition(tagtSrlzMdl
				.getHashKeySerializerClass());
		rdsTpltBldr.addPropertyValue(HASH_KEY_SERIALIZER_PROPERTY, hashKeySrlzBldr.getBeanDefinition());

		BeanDefinitionBuilder hashValSrlzBldr = BeanDefinitionBuilder.genericBeanDefinition(tagtSrlzMdl
				.getHashValueSerializerClass());
		rdsTpltBldr.addPropertyValue(HASH_VALUE_SERIALIZER_PROPERTY, hashValSrlzBldr.getBeanDefinition());

		pc.getRegistry().registerBeanDefinition(clstMdl.getTemplateName(), rdsTpltBldr.getBeanDefinition());
	}

	private void validateValueSerializer(SerializerConfigModel tagtSrlzMdl) {
		if (StringUtils.equals(SERIALZER_STRING_CLAZZ, tagtSrlzMdl.getValueSerializerClass())) {
			String errMsg = "string serializer is not supported for value, please provide other serializer class. type: "
					+ tagtSrlzMdl.getValueSerializerType()
					+ ", class: "
					+ tagtSrlzMdl.getValueSerializerClass();
			LOGGER.error(errMsg);
			pc.getReaderContext().error(errMsg, null);
		}
		if (StringUtils.equals(SERIALZER_STRING_CLAZZ, tagtSrlzMdl.getHashValueSerializerClass())) {
			String errMsg = "string serializer is not supported for hash value, please provide other serializer class. type: "
					+ tagtSrlzMdl.getHashValueSerializerType()
					+ ", class: "
					+ tagtSrlzMdl.getHashValueSerializerClass();
			LOGGER.error(errMsg);
			pc.getReaderContext().error(errMsg, null);
		}
	}
	
	private SerializerConfigModel getCacheSerializer(SerializerConfigModel clstSrlzMdl) {
		// copy global serializer config to target model
		SerializerConfigModel caheMgmrSrlzMdl = cacheManagerModel.getSerializerConfigModel();

		// cache item serializer model has high priority than global model
		SerializerConfigModel targetMdl = integrateCacheClusterSerializerWithGlobal(clstSrlzMdl, caheMgmrSrlzMdl);
		
		// string is default serializer for key. hash key align to key
		// jdk is default serializer for value. hash value align to value
		injectDefaultSerializerType(targetMdl);
		
		// convert serializer type to class qualified name.
		convertSerializerType2Clazz(targetMdl);
		
		return targetMdl;
	}

	private SerializerConfigModel integrateCacheClusterSerializerWithGlobal(SerializerConfigModel clstSrlzMdl,
			SerializerConfigModel caheMgmrSrlzMdl) {
		SerializerConfigModel targetMdl = new SerializerConfigModel();
		BeanUtils.copyProperties(caheMgmrSrlzMdl, targetMdl);
		
		if (null != clstSrlzMdl.getKeySerializerType() || null != clstSrlzMdl.getKeySerializerClass()) {
			targetMdl.setKeySerializerType(clstSrlzMdl.getKeySerializerType());
			targetMdl.setKeySerializerClass(clstSrlzMdl.getKeySerializerClass());
		}
		if (null != clstSrlzMdl.getValueSerializerType() || null != clstSrlzMdl.getValueSerializerClass()) {
			targetMdl.setValueSerializerType(clstSrlzMdl.getValueSerializerType());
			targetMdl.setValueSerializerClass(clstSrlzMdl.getValueSerializerClass());
		}
		if (null != clstSrlzMdl.getHashKeySerializerType() || null != clstSrlzMdl.getHashKeySerializerClass()) {
			targetMdl.setHashKeySerializerType(clstSrlzMdl.getHashKeySerializerType());
			targetMdl.setHashKeySerializerClass(clstSrlzMdl.getHashKeySerializerClass());
		}
		if (null != clstSrlzMdl.getHashValueSerializerType() || null != clstSrlzMdl.getHashValueSerializerClass()) {
			targetMdl.setHashValueSerializerType(clstSrlzMdl.getHashValueSerializerType());
			targetMdl.setHashValueSerializerClass(clstSrlzMdl.getHashValueSerializerClass());
		}
		return targetMdl;
	}

	private void injectDefaultSerializerType(SerializerConfigModel targetMdl) {
		if (null == targetMdl.getKeySerializerType() && null == targetMdl.getKeySerializerClass()) {
			targetMdl.setKeySerializerType(SerializerType.STRING);
		}
		if (null == targetMdl.getValueSerializerType() && null == targetMdl.getValueSerializerClass()) {
			targetMdl.setValueSerializerType(SerializerType.JDK);
		}
		if (null == targetMdl.getHashKeySerializerType() && null == targetMdl.getHashKeySerializerClass()) {
			targetMdl.setHashKeySerializerType(targetMdl.getKeySerializerType());
		}
		if (null == targetMdl.getHashValueSerializerType() && null == targetMdl.getHashValueSerializerClass()) {
			targetMdl.setHashValueSerializerType(targetMdl.getValueSerializerType());
		}
	}

	private void convertSerializerType2Clazz(SerializerConfigModel targetMdl) {
		if (null == targetMdl.getKeySerializerClass()) {
			targetMdl.setKeySerializerClass(SERIALIZER_TYPE_MAP.get(targetMdl.getKeySerializerType()));
		}
		if (null == targetMdl.getValueSerializerClass()) {
			targetMdl.setValueSerializerClass(SERIALIZER_TYPE_MAP.get(targetMdl.getValueSerializerType()));
		}
		if (null == targetMdl.getHashKeySerializerClass()) {
			targetMdl.setHashKeySerializerClass(SERIALIZER_TYPE_MAP.get(targetMdl.getHashKeySerializerType()));
		}
		if (null == targetMdl.getHashValueSerializerClass()) {
			targetMdl.setHashValueSerializerClass(SERIALIZER_TYPE_MAP.get(targetMdl.getHashValueSerializerType()));
		}
	}

	/**
	 * twemproxy:true lvs 如果一个节点, (多个节点, client做loadbalance) twemproxy true sharding - 无用
	 * twemproxy:false lvs 无 twemproxy 无 sharding 有
	 * 
	 * @param caheMdl
	 * @param poolBeanName
	 * @param connFctyName
	 * @param pc
	 */
	private void buildJedisConnectionFactoryBeanDef(CacheModel caheMdl, String poolBeanName, String connFctyName,
			ParserContext pc) {
		List<CacheNodeModel> onlineNodes = getOnlineNodes(caheMdl, pc);
		
		if (caheMdl.isProxy()) {
			if (Utils.isMulitiple(onlineNodes)) {
				// FIXME feature support multiple node (client side load balance) in the future
				String errMsg = "Unsupported cache multiple node cache cluster! " + caheMdl;
				LOGGER.error(errMsg);
				pc.getReaderContext().error(errMsg, null);
			} else {
				// proxy is enabled, load balance is enabled at server side
				BeanDefinitionBuilder jdsConFacBldr = BeanDefinitionBuilder
						.genericBeanDefinition(JEDIS_CONNECTION_FACTORY_CLAZZ);
				
				// pick the first node as cache server
				CacheNodeModel caheNodeMdl = onlineNodes.get(0);
				jdsConFacBldr.addPropertyValue("hostName", caheNodeMdl.getHost());
				jdsConFacBldr.addPropertyValue("port", caheNodeMdl.getPort());
				jdsConFacBldr.addPropertyReference("poolConfig", poolBeanName);
				
				BeanDefinitionRegistry beanDefReg = pc.getRegistry();
				beanDefReg.registerBeanDefinition(connFctyName, jdsConFacBldr.getBeanDefinition());
			}
		} else {
			// FIXME feature support non proxy cache cluster(sharding) in the future
			String errMsg = "Unsupported cache non-proxy cache cluster! " + caheMdl;
			LOGGER.error(errMsg);
			pc.getReaderContext().error(errMsg, null);
		}
	}

	private List<CacheNodeModel> getOnlineNodes(CacheModel caheMdl, ParserContext pc) {
		List<CacheNodeModel> orignNodes = caheMdl.getNodes();
		List<CacheNodeModel> onlineNodes = new LinkedList<CacheNodeModel>();
		for (CacheNodeModel caheNodeMdl : orignNodes) {
			if (NodeState.ONLINE == caheNodeMdl.getState()) {
				onlineNodes.add(caheNodeMdl);
			}
		}
		
		if (CollectionUtils.isEmpty(onlineNodes)) {
			String errMsg = "Failed to get redis node:" + caheMdl;
			LOGGER.error(errMsg);
			pc.getReaderContext().error(errMsg, null);
		}
		return onlineNodes;
	}

	private void buildRedisPoolConfigBeanDef(RedisPoolConfigModel rdsPolCfgMdl, String poolBeanName, ParserContext pc) {
		BeanDefinitionBuilder jdsPolCfgBldr = BeanDefinitionBuilder.genericBeanDefinition(JEDIS_POOL_CONFIG_CLAZZ);
		if (null != rdsPolCfgMdl) {
			if (null != rdsPolCfgMdl.getMaxTotal()) {
				jdsPolCfgBldr.addPropertyValue("maxTotal", rdsPolCfgMdl.getMaxTotal());
			}
			if (null != rdsPolCfgMdl.getMaxIdle()) {
				jdsPolCfgBldr.addPropertyValue("maxIdle", rdsPolCfgMdl.getMaxIdle());
			}
			if (null != rdsPolCfgMdl.getTimeBetweenEvictionRunsMillis()) {
				jdsPolCfgBldr.addPropertyValue("timeBetweenEvictionRunsMillis",
						rdsPolCfgMdl.getTimeBetweenEvictionRunsMillis());
			}
			if (null != rdsPolCfgMdl.getMinEvictableIdleTimeMillis()) {
				jdsPolCfgBldr.addPropertyValue("minEvictableIdleTimeMillis", rdsPolCfgMdl.getMinEvictableIdleTimeMillis());
			}
			if (null != rdsPolCfgMdl.getMaxWaitMillis()) {
				jdsPolCfgBldr.addPropertyValue("maxWaitMillis", rdsPolCfgMdl.getMaxWaitMillis());
			}
		}
		jdsPolCfgBldr.addPropertyValue("testOnBorrow", "false");

		pc.getRegistry().registerBeanDefinition(poolBeanName, jdsPolCfgBldr.getBeanDefinition());
	}

}
