package heracles.jdbc.group;

import heracles.jdbc.common.exception.ExceptionUtils;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class GroupConnection implements Connection {

	private static final Logger LOGGER = LoggerFactory.getLogger(GroupConnection.class);
	
	// FIXME AZEN 必须使用write
	private static final String WRITE_KEY = "write";

	private GroupDataSource groupDataSource;

	private String username;

	private String password;

	private List<Connection> targetConnections = new LinkedList<Connection>();

	private List<Statement> targetStatements = new LinkedList<Statement>();

	private Map<String, Connection> targetConnectionMap = new HashMap<String, Connection>();

	public GroupDataSource getGroupDataSource() {
		return groupDataSource;
	}

	public void setGroupDataSource(GroupDataSource groupDataSource) {
		this.groupDataSource = groupDataSource;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Connection> getTargetConnections() {
		return targetConnections;
	}

	public void setTargetConnections(List<Connection> targetConnections) {
		this.targetConnections = targetConnections;
	}

	public List<Statement> getTargetStatements() {
		return targetStatements;
	}

	public void setTargetStatements(List<Statement> targetStatements) {
		this.targetStatements = targetStatements;
	}

	public Map<String, Connection> getTargetConnectionMap() {
		return targetConnectionMap;
	}

	public void setTargetConnectionMap(Map<String, Connection> targetConnectionMap) {
		this.targetConnectionMap = targetConnectionMap;
	}

	public Connection getWriteTargetConnection() throws SQLException {
		if (targetConnectionMap.get(WRITE_KEY) != null) {
			return targetConnectionMap.get(WRITE_KEY);
		}
		Connection conn = null;
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			conn = groupDataSource.getTargetDataSources().get(WRITE_KEY).getConnection(username, password);
			targetConnectionMap.put(WRITE_KEY, conn);
			return conn;
		}
		conn = DataSourceUtils.getConnection(groupDataSource.getTargetDataSources().get(WRITE_KEY));
		targetConnectionMap.put(WRITE_KEY, conn);
		return conn;
	}

	public Connection getReadTargetConnection(boolean isRead) throws SQLException {
		if(!isRead) {
			if (targetConnectionMap.get(WRITE_KEY) != null && !targetConnectionMap.get(WRITE_KEY).isClosed()) {
				return targetConnectionMap.get(WRITE_KEY);
			}
		}
		String readKey = groupDataSource.getLbStrategy().elect();
		if (targetConnectionMap.get(readKey) != null) {
			return targetConnectionMap.get(readKey);
		}
		Connection conn = null;
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			conn = groupDataSource.getTargetDataSources().get(readKey).getConnection(username, password);
			targetConnectionMap.put(readKey, conn);
			return conn;
		}
		conn = DataSourceUtils.getConnection(groupDataSource.getTargetDataSources().get(readKey));
		targetConnectionMap.put(readKey, conn);
		return conn;
	}

	private boolean closed = false;
	private boolean readOnly;
	private boolean autoCommit = true;
	private int transactionIsolation = -1;
	private Map<String, Connection> actualConnections = new HashMap<String, Connection>();
	private Set<Statement> attachedStatements = new HashSet<Statement>();

	public GroupConnection() {
	}

	public GroupConnection(GroupDataSource groupDataSource) throws SQLException {
		this.groupDataSource = groupDataSource;
	}

	public GroupConnection(GroupDataSource groupDataSource, String username, String password) throws SQLException {
		this.groupDataSource = groupDataSource;
		this.username = username;
		this.password = password;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		checkClosed();

		GroupPreparedStatement vPreparedStatement = new GroupPreparedStatement(this);
		vPreparedStatement.setAutoCommit(autoCommit);
		vPreparedStatement.setReadOnly(readOnly);
		vPreparedStatement.setSql(sql);

		targetStatements.add(vPreparedStatement);

		return vPreparedStatement;
	}

	private void checkClosed() throws SQLException {
		if (closed) {
			throw new SQLException();
		}
	}

	public Map<String, Connection> getActualConnections() {
		return actualConnections;
	}

	public void setActualConnections(Map<String, Connection> actualConnections) {
		this.actualConnections = actualConnections;
	}

	public Set<Statement> getAttachedStatements() {
		return attachedStatements;
	}

	public void setAttachedStatements(Set<Statement> attachedStatements) {
		this.attachedStatements = attachedStatements;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
			return (T) this;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.getClass().isAssignableFrom(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		checkClosed();

		GroupStatement vStatement = new GroupStatement(this);
		vStatement.setAutoCommit(autoCommit);
		vStatement.setReadOnly(readOnly);
		vStatement.setConnectionWrapper(this);

		targetStatements.add(vStatement);

		return vStatement;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new UnsupportedOperationException("prepareCall");
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new UnsupportedOperationException("nativeSQL");
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.autoCommit = autoCommit;
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return this.autoCommit;
	}

	@Override
	public void commit() throws SQLException {
		checkClosed();

		if (autoCommit) {
			return;
		}

		List<SQLException> sqlExceptions = new ArrayList<SQLException>();

		for (Connection conn : targetConnections) {
			try {
				conn.commit();
			} catch (SQLException e) {
				LOGGER.error("GroupConnection commit: tx commit is error:" + e.getMessage());
				sqlExceptions.add(e);
			}
		}
		for (Connection conn : targetConnectionMap.values()) {
			try {
				conn.commit();
			} catch (SQLException e) {
				LOGGER.error("GroupConnection commit: tx commit is error:" + e.getMessage());
				sqlExceptions.add(e);
			}
		}

		ExceptionUtils.throwSQLExceptions(LOGGER, sqlExceptions);
	}

	@Override
	public void rollback() throws SQLException {
		checkClosed();

		if (autoCommit) {
			return;
		}

		List<SQLException> sqlExceptions = new ArrayList<SQLException>();

		for (Connection conn : targetConnections) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				LOGGER.error("GroupConnection rollback: tx commit is error:" + e.getMessage());
				sqlExceptions.add(e);
			}
		}
		for (Connection conn : targetConnectionMap.values()) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				LOGGER.error("GroupConnection rollback: tx commit is error:" + e.getMessage());
				sqlExceptions.add(e);
			}
		}

		ExceptionUtils.throwSQLExceptions(LOGGER, sqlExceptions);
	}

	@Override
	public void close() throws SQLException {
		if (closed) {
			return;
		}

		try {
			List<SQLException> sqlExceptions = new ArrayList<SQLException>();

			for (Statement stmt : targetStatements) {
				try {
					stmt.close();
				} catch (SQLException e) {
					LOGGER.error("GroupConnection close: conn commit is error:" + e.getMessage());
					sqlExceptions.add(e);
				}
			}

			for (Connection conn : targetConnections) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error("GroupConnection close: conn commit is error:" + e.getMessage());
					sqlExceptions.add(e);
				}
			}
			for (Connection conn : targetConnectionMap.values()) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOGGER.error("GroupConnection close: conn commit is error:" + e.getMessage());
					sqlExceptions.add(e);
				}
			}
			ExceptionUtils.throwSQLExceptions(LOGGER, sqlExceptions);
		} finally {
			closed = true;
			targetStatements.clear();
			targetConnections.clear();
			targetConnectionMap.clear();
		}

	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		checkClosed();

		Collection<DataSource> dataSources = this.groupDataSource.getTargetDataSources().values();
		DataSource[] dsArray = dataSources.toArray(new DataSource[dataSources.size()]);
		int index = new Random().nextInt(1000) % dataSources.size();
		return new GroupDatabaseMetaData(dsArray[index]);

	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		checkClosed();
		this.readOnly = readOnly;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return readOnly;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		throw new UnsupportedOperationException("setCatalog");

	}

	@Override
	public String getCatalog() throws SQLException {
		throw new UnsupportedOperationException("getCatalog");
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		checkClosed();
		this.transactionIsolation = level;
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return transactionIsolation;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		GroupStatement vStatement = (GroupStatement) createStatement();
		vStatement.setResultSetType(resultSetType);
		vStatement.setResultSetConcurrency(resultSetConcurrency);
		return vStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		GroupPreparedStatement vPreparedStatement = (GroupPreparedStatement) prepareStatement(sql);
		vPreparedStatement.setResultSetType(resultSetType);
		vPreparedStatement.setResultSetConcurrency(resultSetConcurrency);
		return vPreparedStatement;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnsupportedOperationException("prepareCall");
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new UnsupportedOperationException("getTypeMap");
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException("setTypeMap");
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new UnsupportedOperationException("setHoldability");
	}

	@Override
	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException("getHoldability");
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new UnsupportedOperationException("setSavepoint");
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new UnsupportedOperationException("setSavepoint");
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException("rollback");

	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException("releaseSavepoint");
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		GroupStatement vStatement = (GroupStatement) createStatement(resultSetType, resultSetConcurrency);
		vStatement.setResultSetHoldability(resultSetHoldability);
		return vStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		GroupPreparedStatement vPreparedStatement = (GroupPreparedStatement) prepareStatement(sql, resultSetType,
				resultSetConcurrency);
		vPreparedStatement.setResultSetHoldability(resultSetHoldability);
		return vPreparedStatement;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		throw new UnsupportedOperationException("prepareCall");
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		GroupPreparedStatement vPreparedStatement = (GroupPreparedStatement) prepareStatement(sql);
		vPreparedStatement.setAutoGeneratedKeys(autoGeneratedKeys);
		return vPreparedStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		GroupPreparedStatement vPreparedStatement = (GroupPreparedStatement) prepareStatement(sql);
		vPreparedStatement.setColumnIndexes(columnIndexes);
		return vPreparedStatement;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		GroupPreparedStatement vPreparedStatement = (GroupPreparedStatement) prepareStatement(sql);
		vPreparedStatement.setColumnNames(columnNames);
		return vPreparedStatement;
	}

	@Override
	public Clob createClob() throws SQLException {
		throw new UnsupportedOperationException("createClob");
	}

	@Override
	public Blob createBlob() throws SQLException {
		throw new UnsupportedOperationException("createBlob");
	}

	@Override
	public NClob createNClob() throws SQLException {
		throw new UnsupportedOperationException("createNClob");
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new UnsupportedOperationException("createSQLXML");
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		throw new UnsupportedOperationException("isValid");
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		throw new UnsupportedOperationException("setClientInfo");

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		throw new UnsupportedOperationException("setClientInfo");

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		throw new UnsupportedOperationException("getClientInfo");
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		throw new UnsupportedOperationException("getClientInfo");
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new UnsupportedOperationException("createArrayOf");
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		throw new UnsupportedOperationException("createStruct");
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		throw new UnsupportedOperationException("setSchema");
	}

	@Override
	public String getSchema() throws SQLException {
		throw new UnsupportedOperationException("getSchema");
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		throw new UnsupportedOperationException("abort");
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new UnsupportedOperationException("setNetworkTimeout");
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		throw new UnsupportedOperationException("getNetworkTimeout");
	}
}
