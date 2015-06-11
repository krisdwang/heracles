package heracles.unit.model.xml.jdbcmatrix;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

/**
 * 
 * @author azen
 *
 */
@XmlRootElement(name="beans")
public class JdbcMatrixXmlModel implements Serializable {

	private static final long serialVersionUID = 7127315654876274733L;
	
	@Setter
	private List<JdbcMatrixModel> jdbcList;
	@XmlElement(name="heracles-datasource:jdbc-matrix")
	public List<JdbcMatrixModel> getJdbcList() {
		return jdbcList;
	}
}
