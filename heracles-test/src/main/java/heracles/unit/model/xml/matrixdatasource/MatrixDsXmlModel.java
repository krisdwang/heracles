package heracles.unit.model.xml.matrixdatasource;

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
public class MatrixDsXmlModel implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8082129228935510914L;
	@Setter
	private MatrixDsModel model;
	@XmlElement(name="heracles-datasource:matrix-datasource")
	public MatrixDsModel getModel() {
		return model;
	}
}
