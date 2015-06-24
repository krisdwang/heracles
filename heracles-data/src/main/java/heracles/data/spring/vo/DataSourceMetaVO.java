package heracles.data.spring.vo;

import heracles.data.spring.util.Constants;
import heracles.data.spring.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSourceMetaVO {
	private String id = Constants.DEFAULT_DATASOURCE_ID;
	private String dsType = Constants.DEFAULT_DATASOURCE_TYPE;
	private String dbType = Constants.DEFAULT_DATASOURCE_DB_TYPE;
	private String transactionManager = Constants.DEFAULT_TRANSACTION_MANAGER_NAME;
	private String myBatisSqlSessionFactory = Constants.DEFAULT_MYBATIS_SQL_SESSION_FACTORY_NAME;
	private Map<String, String> properties = Utils
			.getDruidDefaulProperties(Constants.DEFAULT_DATASOURCE_DB_TYPE);
	private List<ReadWriteDataSourceMetaVO> readWriteDataSourceMetaVOs = new ArrayList<ReadWriteDataSourceMetaVO>();
	private TableShardingMetaVO tableShardingMetaVO;
	private RepositoryShardingMetaVO repositoryShardingMetaVO;

	public void addReadWriteDataSourceMetaVO(
			ReadWriteDataSourceMetaVO readWriteDataSourceMetaVO) {
		readWriteDataSourceMetaVOs.add(readWriteDataSourceMetaVO);
	}

	public void addReadWriteDataSourceMetaVOList(
			List<ReadWriteDataSourceMetaVO> readWriteDataSourceMetaVOList) {
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

	public String getTransactionManager() {
		return transactionManager;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
		this.properties = Utils.getDefaulProperties(dsType, dbType);
	}

	public String getMyBatisSqlSessionFactory() {
		return myBatisSqlSessionFactory;
	}

	public void setMyBatisSqlSessionFactory(String myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}

	public void setTransactionManager(String transactionManager) {
		this.transactionManager = transactionManager;
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

	public List<ReadWriteDataSourceMetaVO> getReadWriteDataSourceMetaVOs() {
		return readWriteDataSourceMetaVOs;
	}

	public void setReadWriteDataSourceMetaVOs(
			List<ReadWriteDataSourceMetaVO> readWriteDataSourceMetaVOs) {
		this.readWriteDataSourceMetaVOs = readWriteDataSourceMetaVOs;
	}

	public Class<?> getDataSourceClass() {
		if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DBCP)) {
			return BasicDataSource.class;
		}
		else if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DRUID)) {
			return DruidDataSource.class;
		}

		return ComboPooledDataSource.class;
	}

	public TableShardingMetaVO getTableShardingMetaVO() {
		return tableShardingMetaVO;
	}

	public void setTableShardingMetaVO(TableShardingMetaVO tableShardingMetaVO) {
		this.tableShardingMetaVO = tableShardingMetaVO;
	}

	public RepositoryShardingMetaVO getRepositoryShardingMetaVO() {
		return repositoryShardingMetaVO;
	}

	public void setRepositoryShardingMetaVO(
			RepositoryShardingMetaVO repositoryShardingMetaVO) {
		this.repositoryShardingMetaVO = repositoryShardingMetaVO;
	}
}
