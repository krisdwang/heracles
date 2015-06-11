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
@XmlRootElement(name="heracles-datasource:writeDataSource")
public class WriteDSModel implements Serializable {

	private static final long serialVersionUID = 9190000810322387478L;

	@Setter
	private String logicName;
	@XmlAttribute
	public String getLogicName() {
		return logicName;
	}
}
