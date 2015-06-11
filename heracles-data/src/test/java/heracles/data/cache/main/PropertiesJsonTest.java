package heracles.data.cache.main;

import static org.junit.Assert.assertEquals;
import heracles.data.cache.config.model.CacheType;
import heracles.data.cache.config.model.LoadBalanceType;
import heracles.data.cache.config.model.NodeState;
import heracles.data.cache.resource.model.CacheModel;
import heracles.data.cache.resource.model.CacheNodeModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class PropertiesJsonTest {

	@Test
	public void testRedisJsonProperties() throws IOException {
		String propPath = "cache/properties_test.properties";
		String key = "/default/resource/Cache/Redis/redis01w";
		String expVal = "{\"loadBalance\":\"RoundRobbin\",\"name\":\"redis-cluster-01\",\"nodes\":[{\"host\":\"10.101.15.78\",\"logicName\":\"redis-node-01\",\"port\":\"8080\",\"state\":\"offline\"},{\"host\":\"10.101.255.148\",\"logicName\":\"redis-node-02\",\"port\":\"8443\",\"state\":\"offline\"}],\"proxy\":true,\"sharding\":\"\",\"type\":\"Redis\"}";

		InputStream is = PropertiesJsonTest.class.getClassLoader().getResourceAsStream(propPath);
		Properties props = new Properties();
		props.load(is);

		Object value = props.get(key);
		assertEquals(expVal, value);
	}
	
	@Test
	public void testCacheJsonParse() throws IOException {
		String propPath = "cache/properties_test.properties";
		String key = "/default/resource/Cache/Redis/redis01w";

		InputStream is = PropertiesJsonTest.class.getClassLoader().getResourceAsStream(propPath);
		Properties props = new Properties();
		props.load(is);

		String val = props.getProperty(key);

		JSONObject jsonObj = JSON.parseObject(val);
		CacheModel caheMdl = new CacheModel();
		caheMdl.setName(jsonObj.getString("name"));
		caheMdl.setType(CacheType.valueOf(StringUtils.upperCase(jsonObj.getString("type"))));
		caheMdl.setProxy(jsonObj.getBooleanValue("proxy"));
		caheMdl.setLoadBalance(LoadBalanceType.valueOf(StringUtils.upperCase(jsonObj.getString("loadBalance"))));

		JSONArray jsonArray = jsonObj.getJSONArray("nodes");
		List<CacheNodeModel> nodes = new LinkedList<CacheNodeModel>();
		caheMdl.setNodes(nodes);
		for (Object obj : jsonArray) {
			JSONObject nodeObj = (JSONObject) obj;
			CacheNodeModel caheNodeMdl = new CacheNodeModel();
			caheNodeMdl.setLogicName(nodeObj.getString("logicName"));
			caheNodeMdl.setHost(nodeObj.getString("host"));
			caheNodeMdl.setPort(nodeObj.getIntValue("port"));
			caheNodeMdl.setState(NodeState.valueOf(StringUtils.upperCase(nodeObj.getString("state"))));
			nodes.add(caheNodeMdl);
		}

		assertEquals(2, nodes.size());
		assertEquals(CacheType.REDIS, caheMdl.getType());
		System.out.println(caheMdl.toString());
	}
}
