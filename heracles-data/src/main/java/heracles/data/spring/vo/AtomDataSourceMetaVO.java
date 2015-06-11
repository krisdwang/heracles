package heracles.data.spring.vo;

import heracles.data.common.model.AtomModel;

import java.util.HashMap;
import java.util.Map;

public class AtomDataSourceMetaVO {

	private String logicName;
	private Map<String, String> properties = new HashMap<String, String>();
	private AtomModel atomModel;

	public AtomModel getAtomModel() {
		return atomModel;
	}

	public void setAtomModel(AtomModel atomModel) {
		this.atomModel = atomModel;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public void addProperties(Map<String, String> properties) {
		this.properties.putAll(properties);
	}

}
