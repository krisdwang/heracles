package heracles.core.context.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

/**
 * 
 * 
 * @author kriswang
 *
 */
@XmlRootElement(name="itemgroup")
public class ItemgroupModel  implements Serializable {

	private static final long serialVersionUID = -2197020743933274816L;
	@Setter
	private String name;
	@XmlAttribute
	public String getName() {
		return name;
	}

	@Setter
	private List<ItemModel> confList;

	@XmlElement(name="item")
	public List<ItemModel> getConfList() {
		return confList;
	}	
}
