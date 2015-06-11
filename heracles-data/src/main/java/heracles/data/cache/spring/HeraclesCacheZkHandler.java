
package heracles.data.cache.spring;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.ParserContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import heracles.core.context.property.PropertyHolder;
import heracles.core.zookeeper.ZookeeperClient;
import heracles.core.zookeeper.ZookeeperClientFactory;
import heracles.data.cache.config.model.CacheType;
import heracles.data.cache.config.model.LoadBalanceType;
import heracles.data.cache.config.model.NodeState;
import heracles.data.cache.config.model.ShardingType;
import heracles.data.cache.resource.model.CacheModel;
import heracles.data.cache.resource.model.CacheNodeModel;
import heracles.data.cache.utils.Utils;

/**
 * @author kriswang
 *
 */
public class HeraclesCacheZkHandler {
	
	private String clusterName;
	
	private CacheType cacheType;
	
	private String partition;
	
	private ParserContext pc;
	
	private static final Logger log = LoggerFactory.getLogger(HeraclesCacheZkHandler.class);
	
	public HeraclesCacheZkHandler(String clusterName, CacheType cacheType, ParserContext pc) {
		this.clusterName = clusterName;
		this.cacheType = cacheType;
		this.pc = pc;
	}

	public CacheModel getCacheConfig(){
		// FIXME wjh get partion ....  confirm with zxc    heracles.cfgcenter.partition
		//partition = ZnodeUtils.resolveSystemProperty("heracles.cfgcenter.partition");
		partition = Utils.resolveSystemProperty("heracles.cfgcenter.partition");
		log.info("Heracles cache - get cache cluster config - partition:{}", partition);
				
		// Read cache configuration from local dummy path
		String cahePath = createCacheResourcePath();
		String caheJson = readCacheConfigFromLocal(cahePath);
		
		// Read cache configuration from zookeeper
		cahePath = "/" + partition + cahePath;
		String caheJsonFrmZk = readCacheConfigFromZK(cahePath);
		if (StringUtils.isNotBlank(caheJsonFrmZk)) {
			caheJson = caheJsonFrmZk;
		}
		
		if (StringUtils.isBlank(caheJson)) {
			String errMsg = "Failed to get cache configuration, either from local properties or zk";
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, null);
		}
		
		CacheModel caheMdl = parseCacheJsonString(caheJson);
		if (!StringUtils.equalsIgnoreCase(clusterName, caheMdl.getName())) {
			String errMsg = "Cache name is not consistent, cache name from xml: " + clusterName
					+ ", cache name from zk: " + caheMdl.getName();
			log.error(errMsg);
			pc.getReaderContext().error(errMsg, null);
		}
		return caheMdl;
	}

	private String createCacheResourcePath() {
		// "/default/resource/Cache/Redis/redis01w"
		String cahePath = null;
		if (CacheType.REDIS == cacheType) {
			cahePath = "/resource/Cache/Redis/" + clusterName;
		} else if (CacheType.MEMCACHED == cacheType) {
			cahePath = "/resource/Cache/Memcached/" + clusterName;
		} else {
			String errMsg = "Unsupported cache type:" + cacheType + ", clusterName:" + clusterName;
			pc.getReaderContext().error(errMsg, null);
		}
		
		log.info("Heracles cache - cache path:{}", cahePath);
		return cahePath;
	}

	private String readCacheConfigFromLocal(String cahePath) {
		try {
			String caheJson = PropertyHolder.getProperties().getProperty(cahePath);
			log.info("Heracles cache - cache path:{} \nlocal configuration: {}", cahePath, caheJson);
			return caheJson;
		} catch (Throwable t) {
			log.error("Failed to get cache config from local", t);
			return null;
		}
	}
	
	
	private String readCacheConfigFromZK(String cahePath){
		ZookeeperClient zkClient = null;
		try {
			zkClient = (ZookeeperClient) ZookeeperClientFactory.getClient(cahePath);

			//FIXME wjh, need watch
			//zkClient.findChildDataAndWatch(path, handlers, file)
			String caheJson = zkClient.findChildData(cahePath);
			log.info("Heracles cache - cache path:{} \nzk configuration: {}", cahePath, caheJson);
			return caheJson;
		} catch (Throwable t) {
			String errMsg = "Failed to get cache configuration from zk";
			log.error(errMsg);
		} finally {
			// FIXME wjh
			if (null != zkClient) {
				zkClient.closeClient();
			}
		}
		return null;
	}
	

	/**
	 * twemproxy:true lvs 如果一个节点, (多个节点, client做loadbalance) twemproxy true sharding - 无用
	 * twemproxy:false lvs 无 twemproxy 无 sharding 有
	 * 
	 * @param kriswang
	 * @return
	 */
	private CacheModel parseCacheJsonString(String caheJson) {
		try {
			JSONObject jsonObj = JSON.parseObject(caheJson);
			CacheModel caheMdl = new CacheModel();
			caheMdl.setName(jsonObj.getString("name"));
			caheMdl.setType(Utils.getEnumFromString(jsonObj.getString("type"), CacheType.class, pc));
			caheMdl.setProxy(jsonObj.getBooleanValue("proxy"));

			List<CacheNodeModel> nodes = parseCacheNodeJsonString(jsonObj);
			caheMdl.setNodes(nodes);
			
			if (caheMdl.isProxy()) {
				if (Utils.isMulitiple(nodes)) {
					caheMdl.setLoadBalance(Utils.getEnumFromString(jsonObj.getString("loadBalance"),
							LoadBalanceType.class, pc));
				}
				else {
					caheMdl.setLoadBalance(null);
				}
				caheMdl.setSharding(null);
			} else {
				caheMdl.setLoadBalance(null);
				caheMdl.setSharding(Utils.getEnumFromString(jsonObj.getString("sharding"), ShardingType.class, pc));
			}

			log.info("Heracles cache - cache configuration:{}", caheMdl);
			return caheMdl;
		} catch (Throwable t) {
			String errMsg = "Failed to parse cache json, " + t.getMessage();
			log.error(errMsg, t);
			pc.getReaderContext().error(errMsg, null);
			return null;
		}
	}

	private List<CacheNodeModel> parseCacheNodeJsonString(JSONObject jsonObj) {
		JSONArray jsonArray = jsonObj.getJSONArray("nodes");
		List<CacheNodeModel> nodes = new LinkedList<CacheNodeModel>();
		for (Object obj : jsonArray) {
			JSONObject nodeObj = (JSONObject) obj;
			CacheNodeModel caheNodeMdl = new CacheNodeModel();
			caheNodeMdl.setLogicName(nodeObj.getString("logicName"));
			caheNodeMdl.setHost(nodeObj.getString("host"));
			caheNodeMdl.setPort(nodeObj.getIntValue("port"));
			caheNodeMdl.setState(Utils.getEnumFromString(nodeObj.getString("state"), NodeState.class, pc));
			nodes.add(caheNodeMdl);
		}
		return nodes;
	}
}
