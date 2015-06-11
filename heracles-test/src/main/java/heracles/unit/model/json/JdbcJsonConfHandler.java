
package heracles.unit.model.json;

import heracles.core.context.property.PropertyHolder;
import heracles.core.zookeeper.ZookeeperClient;
import heracles.core.zookeeper.ZookeeperClientFactory;
import heracles.unit.TestConstants;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author kriswang
 *
 */
public class JdbcJsonConfHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcJsonConfHandler.class);
	
	private String clusterName;
	
	private String partition;
	
	public JdbcJsonConfHandler(String matrixName) {
		if(StringUtils.isBlank(matrixName)) {
			this.clusterName = PropertyHolder.getProperties().getProperty("logicName");
		} else {
			this.clusterName = matrixName;
		}
	}

	public MatrixModel getJdbcConfig(){
		partition = PropertyHolder.getProperties().getProperty(TestConstants.BASE_CFGCENTER_PARTITION_KEY);
		if(StringUtils.isBlank(partition)) {
			partition = TestConstants.BASE_CFGCENTER_PARTITION_VALUE;
		}
		LOGGER.info("Heracles jdbc - get jdbc cluster config - partition:{}", partition);
				
		//config from local
		String jdbcPath = createJdbcResourcePath();
		String jdbcJson = readJdbcConfigFromLocal(jdbcPath);
		
		//FIXME AZEN 暂未考虑 config from zookeeper
		jdbcPath = "/" + partition + jdbcPath;
//		String jdbcJsonFrmZk = readJdbcConfigFromZK(jdbcPath);
//		if (StringUtils.isNotBlank(jdbcJsonFrmZk)) {
//			jdbcJson = jdbcJsonFrmZk;
//		}
		
		if (StringUtils.isBlank(jdbcJson)) {
			String errMsg = "Failed to get jdbc config, either from local properties or zk";
			LOGGER.error(errMsg);
		}
		
		MatrixModel jdbcMdl = parseMatrix(jdbcJson);
		if(jdbcMdl == null) {
			return null;
		}
		if (!StringUtils.equalsIgnoreCase(clusterName, jdbcMdl.getMatrixName())) {
			String errMsg = "Jdbc name is not consistent, jdbc name from xml: " + clusterName
					+ ", jdbc name from zk: " + jdbcMdl.getMatrixName();
			LOGGER.error(errMsg);
		}
		return jdbcMdl;
	}

	private String createJdbcResourcePath() {
		String jdbcPath = TestConstants.BASE_MATRIX_PATH + clusterName;	
		LOGGER.info("Heracles jdbc - jdbc path:{}", jdbcPath);
		return jdbcPath;
	}

	private String readJdbcConfigFromLocal(String jdbcPath) {
		try {
			String jdbcJson = PropertyHolder.getProperties().getProperty(jdbcPath);
			if(StringUtils.isBlank(jdbcJson)) {
				//jdbcJson = PropertiesUtils.getPropsConfig().getProperty(jdbcPath);
			}
			LOGGER.info("Heracles jdbc - jdbc path:{}, local config: {}", jdbcPath, jdbcJson);
			return jdbcJson;
		} catch (Throwable t) {
			LOGGER.error("Failed to get jdbc config from local", t);
			return null;
		}
	}
	
	/**
	 * 从zk取配置
	 * 
	 * @param jdbcPath
	 * @return
	 */
	@SuppressWarnings("unused")
	private String readJdbcConfigFromZK(String jdbcPath){
		ZookeeperClient zkClient = null;
		try {
			zkClient = (ZookeeperClient) ZookeeperClientFactory.getClient(jdbcPath);
			
			String jdbcJson = zkClient.findChildData(jdbcPath);
			LOGGER.info("Heracles jdbc - jdbc path:{}, zk config: {}", jdbcPath, jdbcJson);
			return jdbcJson;
		} catch (Throwable t) {
			String errMsg = "Failed to get jdbc config from zk";
			LOGGER.error(errMsg);
		} finally {
			if (null != zkClient) {
				zkClient.closeClient();
			}
		}
		return null;
	}
	
	private MatrixModel parseMatrix(String jdbcJson) {
		try {
			JSONObject jsonObj = JSON.parseObject(jdbcJson);
			MatrixModel jdbcMdl = new MatrixModel();
			jdbcMdl.setMatrixName(jsonObj.getString("matrixName"));
			jdbcMdl.setType(jsonObj.getString("type"));
			jdbcMdl.setState(jsonObj.getString("state"));

			List<GroupModel> groupNodes = parseGroup(jsonObj);
			jdbcMdl.setGroups(groupNodes);
			
			List<RuleModel> ruleNodes = parseRule(jsonObj);
			jdbcMdl.setRules(ruleNodes);

			LOGGER.info("Heracles jdbc - jdbc config:{}", jdbcMdl);
			return jdbcMdl;
		} catch (Throwable t) {
			String errMsg = "Failed to parse jdbc json, " + t.getMessage();
			LOGGER.error(errMsg, t);
			return null;
		}
	}

	private List<GroupModel> parseGroup(JSONObject jsonObj) {
		JSONArray jsonArray = jsonObj.getJSONArray("groups");
		List<GroupModel> groupNodes = new LinkedList<GroupModel>();
		if(CollectionUtils.isNotEmpty(jsonArray)) {
			for (Object obj : jsonArray) {
				JSONObject nodeObj = (JSONObject) obj;
				GroupModel jdbcNodeMdl = new GroupModel();
				jdbcNodeMdl.setGroupName(nodeObj.getString("groupName"));
				jdbcNodeMdl.setState(nodeObj.getString("state"));
				jdbcNodeMdl.setLoadBalance(nodeObj.getString("loadBalance"));
				
				List<AtomModel> atomNodes = parseAtom((JSONObject)obj);
				jdbcNodeMdl.setAtoms(atomNodes);
				
				groupNodes.add(jdbcNodeMdl);
			}
		}
		return groupNodes;
	}
	
	private List<AtomModel> parseAtom(JSONObject jsonObj) {
		JSONArray jsonArray = jsonObj.getJSONArray("atoms");
		List<AtomModel> nodes = new LinkedList<AtomModel>();
		if(CollectionUtils.isNotEmpty(jsonArray)) {
			for (Object obj : jsonArray) {
				JSONObject nodeObj = (JSONObject) obj;
				AtomModel jdbcNodeMdl = new AtomModel();
				jdbcNodeMdl.setAtomName(nodeObj.getString("atomName"));
				jdbcNodeMdl.setHost(nodeObj.getString("host"));
				jdbcNodeMdl.setPort(nodeObj.getIntValue("port"));
				jdbcNodeMdl.setUsername(nodeObj.getString("username"));
				jdbcNodeMdl.setPassword(nodeObj.getString("password"));
				jdbcNodeMdl.setDbName(nodeObj.getString("dbName"));
				jdbcNodeMdl.setParam(nodeObj.getString("param"));
				jdbcNodeMdl.setIsMaster(nodeObj.getBooleanValue("isMaster"));
				jdbcNodeMdl.setState(nodeObj.getString("state"));
				nodes.add(jdbcNodeMdl);
			}
		}
		return nodes;
	}
	
	private List<RuleModel> parseRule(JSONObject jsonObj) {
		JSONArray jsonArray = jsonObj.getJSONArray("rules");
		List<RuleModel> rules = new LinkedList<RuleModel>();
		if(CollectionUtils.isNotEmpty(jsonArray)) {
			for (Object obj : jsonArray) {
				JSONObject nodeObj = (JSONObject) obj;
				RuleModel ruleModel = new RuleModel();
				ruleModel.setTableNames(nodeObj.getString("tableNames"));
				ruleModel.setGroupShardRule(nodeObj.getString("groupShardRule"));
				ruleModel.setGroupIndex(nodeObj.getString("groupIndex"));
				ruleModel.setTableShardRule(nodeObj.getString("tableShardRule"));
				ruleModel.setTableSuffix(nodeObj.getString("tableSuffix"));
	
				rules.add(ruleModel);
			}
		}
		return rules;
	}
	
	@SuppressWarnings("unused")
	private <T extends Enum<T>> T getEnumFromString(String strType, Class<T> clazz) {
		T enumType = null;
		try {
			enumType = Enum.valueOf(clazz, StringUtils.upperCase(strType));
		} catch (Exception e) {
			String errMsg = "Failed to get enum type, unsupported enum type:" + strType + " for enum:"
					+ clazz.getName();
			LOGGER.error(errMsg, e);
		}
		return enumType;
	}
}
