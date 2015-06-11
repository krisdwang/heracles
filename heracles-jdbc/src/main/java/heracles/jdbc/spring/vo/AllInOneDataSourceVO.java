package heracles.jdbc.spring.vo;

import heracles.jdbc.spring.util.Constants;
import heracles.jdbc.spring.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class AllInOneDataSourceVO {
	private String id = Constants.DEFAULT_DATASOURCE_ID;
	private String dsType = Constants.DEFAULT_DATASOURCE_TYPE;
	private String dbType = Constants.DEFAULT_DATASOURCE_DB_TYPE;
	private Map<String, String> properties = Utils.getDruidDefaulProperties(Constants.DEFAULT_DATASOURCE_DB_TYPE);
	private List<AllInOneReadWriteDataSourceVO> readWriteDataSourceMetaVOs = new ArrayList<AllInOneReadWriteDataSourceVO>();

	public void addReadWriteDataSourceMetaVOList(List<AllInOneReadWriteDataSourceVO> readWriteDataSourceMetaVOList) {
		readWriteDataSourceMetaVOs.addAll(readWriteDataSourceMetaVOList);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDsType() {
		return dsType;
	}

	public void setDsType(String dsType) {
		this.dsType = dsType;
		this.properties = Utils.getDefaulProperties(dsType, dbType);
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
		this.properties = Utils.getDefaulProperties(dsType, dbType);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public List<AllInOneReadWriteDataSourceVO> getReadWriteDataSourceMetaVOs() {
		return readWriteDataSourceMetaVOs;
	}

	public void setReadWriteDataSourceMetaVOs(List<AllInOneReadWriteDataSourceVO> readWriteDataSourceMetaVOs) {
		this.readWriteDataSourceMetaVOs = readWriteDataSourceMetaVOs;
	}

	public Class<?> getDataSourceClass() {
		if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DBCP)) {
			return BasicDataSource.class;
		}
		// else if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_C3P0)) {
		// return ComboPooledDataSource.class;
		// }
		else if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DRUID)) {
			return DruidDataSource.class;
		}

		return ComboPooledDataSource.class;
	}

}
