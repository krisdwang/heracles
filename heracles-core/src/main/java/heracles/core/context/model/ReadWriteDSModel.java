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
@XmlRootElement(name="heracles-datasource:readWriteDataSource")
public class ReadWriteDSModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3315040819516733767L;

	@Setter
	private List<WriteDSModel> writeDSList;

	@XmlElement(name="heracles-datasource:writeDataSource")
	public List<WriteDSModel> getWriteDSList() {
		return writeDSList;
	}
	
	@Setter
	private List<ReadDSModel> readDSList;

	@XmlElement(name="heracles-datasource:readDataSource")
	public List<ReadDSModel> getReadDSList() {
		return readDSList;
	}
	
}
