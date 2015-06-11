package heracles.unit.model.xml.allinone;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import lombok.Setter;

/**
 * 
 * @author azen
 *
 */
@XmlRootElement(name="beans")
public class AllInOneDsXmlModel implements Serializable {	

	/**
	 * 
	 */
	private static final long serialVersionUID = -784548070506592817L;
	@Setter
	private AllInOneDsModel model;
	@XmlElement(name="heracles-datasource:allinone-datasource")
	public AllInOneDsModel getModel() {
		return model;
	}
}
