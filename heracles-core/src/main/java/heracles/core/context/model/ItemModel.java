package heracles.core.context.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import lombok.Setter;

/**
 * 
 * @author kriswang
 *
 */
@XmlType(propOrder = { "name", "key", "value","defaultValueOnly","dataType","desc","hotReload"}) 
public class ItemModel implements Serializable {

	private static final long serialVersionUID = 2469720382107860400L;
	
	@Setter
	private String name;

	@Setter
	private String key;

	@Setter
	private String value;

	@Setter
	private Boolean defaultValueOnly = false;
	
	@Setter
	private String dataType;
	
	@Setter
	private Boolean hotReload = false;

	@Setter
	private String desc;
	@XmlAttribute
	public String getName() {
		return name;
	}
	@XmlAttribute
	public String getKey() {
		return key;
	}
	@XmlAttribute(name="defaultValue")
	public String getValue() {
		return value;
	}
	@XmlAttribute
	public Boolean getDefaultValueOnly() {
		return defaultValueOnly;
	}
	@XmlAttribute
	public String getDataType() {
		return dataType;
	}
	@XmlAttribute
	public Boolean getHotReload() {
		return hotReload;
	}
	@XmlAttribute
	public String getDesc() {
		return desc;
	}	
}
