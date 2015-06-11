package heracles.unit.model.xml.allinone;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

/**
 * 
 * @author azen
 *
 */
@XmlRootElement(name="heracles-datasource:allinone-datasource")
public class AllInOneDsModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6058303446364796251L;
	@Setter
	private String matrixName;
	@XmlAttribute(name="matrix-name")
	public String getMatrixName() {
		return matrixName;
	}
}
