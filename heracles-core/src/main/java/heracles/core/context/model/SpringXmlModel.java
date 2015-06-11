package heracles.core.context.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

/**
 * 
 * @author kriswang
 *
 */
@XmlRootElement(name="beans")
public class SpringXmlModel implements Serializable {

	private static final long serialVersionUID = 7127315654876274733L;
	
	@Setter
	private List<JdbcMatrixModel> jdbcList;
	@XmlElement(name="heracles-datasource:jdbc-matrix")
	public List<JdbcMatrixModel> getJdbcList() {
		return jdbcList;
	}
}
