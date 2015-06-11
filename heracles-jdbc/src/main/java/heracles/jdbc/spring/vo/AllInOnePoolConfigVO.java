package heracles.jdbc.spring.vo;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class AllInOnePoolConfigVO {

	private String atomNames;
	private Map<String, String> properties = new HashMap<String, String>();

	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}
}
