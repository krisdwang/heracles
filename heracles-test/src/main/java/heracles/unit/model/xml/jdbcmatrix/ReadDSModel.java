package heracles.unit.model.xml.jdbcmatrix;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

/**
 * 
 * 
 * @author azen
 * 
 */
@XmlRootElement(name = "heracles-datasource:readDataSource")
public class ReadDSModel implements Serializable {

	private static final long serialVersionUID = 6466126851532691733L;

	@Setter
	private String logicName;

	@XmlAttribute
	public String getLogicName() {
		return logicName;
	}
}
