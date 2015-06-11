package heracles.data.datasource;

import heracles.data.common.holder.StrategyHolder;
import heracles.data.common.util.Constants;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 读写数据源
 * 
 * @author kriswang
 * 
 */
public class ReadWriteDataSource extends AbstractRoutingDataSource implements DisposableBean, ReadWriteDataSourceMBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadWriteDataSource.class);

	private ReadWriteDataSourceKey dataSourceKey;

	private DataSourceRecoverHeartBeat recoverHeartBeat;

	private Map<String, DataSource> key2DataSourceMap = new ConcurrentHashMap<String, DataSource>();

	private ExecutorService executorService;

	private Map<String, String> markDownMap = new ConcurrentHashMap<String, String>();

	@PostConstruct
	public void register() {
		synchronized (this) {
			MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName objectName;
			try {
				String classInfo = this.toString() + "|" + this.dataSourceKey.getWriteKey();
				objectName = new ObjectName("io.doeasy.data:type=" + classInfo);
				if (!mbeanServer.isRegistered(objectName)) {
					mbeanServer.registerMBean(this, objectName);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("io.doeasy.data:type=" + classInfo + " registered successfully");
					}
				}
			} catch (InstanceAlreadyExistsException e) {
				LOGGER.error(e.getMessage());
			} catch (MBeanRegistrationException e) {
				LOGGER.error(e.getMessage());
			} catch (NotCompliantMBeanException e) {
				LOGGER.error(e.getMessage());
			} catch (MalformedObjectNameException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	@Override
	public void putKey(String key, String replaceKey) {
		this.markDownMap.put(key, replaceKey);
	}

	@Override
	public void removeKey(String key) {
		this.markDownMap.remove(key);
	}

	@Override
	public String getWriteKey() {
		return getDataSourceKey().getWriteKey();
	}

	@Override
	public Map<String, String> getReadKeys() {
		return getDataSourceKey().getReadKeys();
	}

	@Override
	public Map<String, String> getMarkDownKeys() {
		return markDownMap;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String key = dataSourceKey.getKey();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("real key is " + key);
		}

		if (key != null && markDownMap.containsKey(key)) {
			if (logger.isDebugEnabled()) {
				logger.debug("get into mark down[" + key + "]");
			}

			String replaceKey = markDownMap.get(key);

			if (!getWriteKey().equals(replaceKey) && getReadKeys().get(replaceKey) == null) {
				StrategyHolder.removeRepositoryShardingStrategy();
				if (logger.isDebugEnabled()) {
					logger.debug("clean up sharding holder before throw access denied exception[" + key + "]");
				}
				throw new RuntimeException("access denied for " + key);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("replace key is " + replaceKey);
			}

			return replaceKey;
		}

		return key;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnectionFromDataSource(null, null);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnectionFromDataSource(username, password);
	}

	private Connection getConnectionFromDataSource(String username, String password) throws SQLException {
		Connection connection = null;
		DataSource ds = determineTargetDataSource();
		try {
			if (username == null && password == null) {
				// connection = super.getConnection();
				connection = ds.getConnection();
			} else {
				// connection = super.getConnection(username, password);
				connection = ds.getConnection(username, password);
			}
			validateConnection(connection);
		} catch (Exception e) {
			if (dataSourceKey.isCurrentWriteKey()) {
				throw new SQLException(e.getMessage());
			}
			String key = (String) determineCurrentLookupKey();
			dataSourceKey.removeDataSourceKey(key);
			key2DataSourceMap.put(key, ds);
			executeHeartBeat();

			if (dataSourceKey.hasReadKey()) {
				dataSourceKey.resetKey();
				return getConnectionFromDataSource(username, password);
			}
			throw new SQLException(e.getMessage());
		}
		return connection;
	}

	private void validateConnection(Connection connection) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(Constants.VALIDATE_SQL);
		stmt.executeQuery();
		stmt.close();
	}

	@Override
	public void destroy() throws Exception {
		shutdownHeartBeat();
	}

	private synchronized void executeHeartBeat() {
		if (recoverHeartBeat == null) {
			recoverHeartBeat = new DataSourceRecoverHeartBeat(this);
			if (executorService == null) {
				executorService = Executors.newFixedThreadPool(1);
			}
			executorService.execute(recoverHeartBeat);
		} else {
			if (!recoverHeartBeat.isRuning()) {
				if (executorService == null) {
					executorService = Executors.newFixedThreadPool(1);
				}
				executorService.execute(recoverHeartBeat);
			}
		}
	}

	private synchronized void shutdownHeartBeat() {
		if (recoverHeartBeat != null) {
			recoverHeartBeat.close();
		}
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public ReadWriteDataSourceKey getDataSourceKey() {
		return dataSourceKey;
	}

	public void setDataSourceKey(ReadWriteDataSourceKey dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}

	private static class DataSourceRecoverHeartBeat implements Runnable {

		private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRecoverHeartBeat.class);

		private ReadWriteDataSource readWriteDataSource;
		private boolean runing;
		private boolean close = false;

		public boolean isRuning() {
			return runing;
		}

		public void close() {
			close = true;
		}

		public DataSourceRecoverHeartBeat(ReadWriteDataSource readWriteDataSource) {
			this.readWriteDataSource = readWriteDataSource;
		}

		public void run() {
			runing = true;
			ReadWriteDataSourceKey dataSourceKey = readWriteDataSource.getDataSourceKey();
			// Map<String, String> failedDataSources;
			// Set<String> dataSourceKeys;
			while (dataSourceKey.hasFailedDataSource() && !close) {

				// failedDataSources = dataSourceKey.getFailedDataSourceKeys();
				// dataSourceKeys = failedDataSources.keySet();
				for (String key : dataSourceKey.getFailedDataSourceKeys().keySet()) {
					try {
						DataSource ds = readWriteDataSource.key2DataSourceMap.get(key);
						Connection connection = ds.getConnection();
						readWriteDataSource.validateConnection(connection);
						dataSourceKey.recoverDateSourceKey(key);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("key = " + key + " valid ok");
						}
					} catch (Exception e) {
						LOGGER.error("key = " + key + " valid failed");
					}
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
			runing = false;
		}
	}
}
