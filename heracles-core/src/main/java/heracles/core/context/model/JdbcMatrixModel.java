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
@XmlRootElement(name="heracles-datasource:jdbc-matrix")
public class JdbcMatrixModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3228280532764333716L;
	
	@Setter
	private String dbType = "mysql";
	@XmlAttribute
	public String getDbType() {
		return dbType;
	}

	@Setter
	private List<ReadWriteDSModel> readWriteDSList;

	@XmlElement(name="heracles-datasource:readWriteDataSource")
	public List<ReadWriteDSModel> getReadWriteDSList() {
		return readWriteDSList;
	}
}
