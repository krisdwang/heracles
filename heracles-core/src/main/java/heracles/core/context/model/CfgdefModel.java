package heracles.core.context.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

/**
 * 
 * @author kriswang
 *
 */
@XmlRootElement(name="cfgdef")
public class CfgdefModel implements Serializable {

	private static final long serialVersionUID = 1428091378918774752L;
	
	
	@Setter
	private String name;
	@XmlAttribute
	public String getName() {
		return name;
	}

	@Setter
	private String version;	
	@XmlAttribute
	public String getVersion() {
		return version;
	}
	
	@Setter
	private String group;	
	@XmlAttribute
	public String getGroup() {
		return group;
	}
	
	@Setter
	private String partition;
	@XmlAttribute
	public String getPartition() {
		return partition;
	}

	@Setter
	private List<ItemgroupModel> cpsList;
	@XmlElement(name="itemgroup")
	public List<ItemgroupModel> getCpsList() {
		return cpsList;
	}
}
