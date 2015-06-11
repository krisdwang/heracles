package heracles.unit.model.xml.matrixdatasource;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

/**
 * 
 * @author azen
 *
 */
@XmlRootElement(name="heracles-datasource:matrix-datasource")
public class MatrixDsModel implements Serializable {
	
	private static final long serialVersionUID = 127565916465460113L;
	
	@Setter
	private String matrixName;
	@XmlAttribute(name="matrix-name")
	public String getMatrixName() {
		return matrixName;
	}
}
