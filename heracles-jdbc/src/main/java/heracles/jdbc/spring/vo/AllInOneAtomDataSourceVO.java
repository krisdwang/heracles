package heracles.jdbc.spring.vo;

import heracles.jdbc.matrix.model.AtomModel;

import java.util.HashMap;
import java.util.Map;

public class AllInOneAtomDataSourceVO {

	private Map<String, String> properties = new HashMap<String, String>();
	private AtomModel atomModel;

	public AtomModel getAtomModel() {
		return atomModel;
	}

	public void setAtomModel(AtomModel atomModel) {
		this.atomModel = atomModel;
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
