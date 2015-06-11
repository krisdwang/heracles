package heracles.jdbc.matrix;

import heracles.jdbc.common.exception.ExceptionUtils;
import heracles.jdbc.common.rs.MergeResultSet;
import heracles.jdbc.executor.merge.MergeExecutor;
import heracles.jdbc.parser.Parser;
import heracles.jdbc.parser.common.ShardingType;
import heracles.jdbc.parser.exception.SQLParserException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

public class MatrixStatement implements Statement {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatrixStatement.class);

	private boolean closed;
	private boolean readOnly;
	private boolean autoCommit = true;
	private int resultSetType = -1;
	private int resultSetConcurrency = -1;
	private int resultSetHoldability = -1;
	private MatrixConnection connectionWrapper;
	protected Set<ResultSet> attachedResultSets = new HashSet<ResultSet>();
	protected List<Statement> actualStatements = new ArrayList<Statement>();
	protected ResultSet resultSet;
	protected boolean moreResults;
	protected int updateCount;
	protected Parser parser;
	protected int maxRows;
	protected int maxFieldSize;
	protected int queryTimeOut;
	protected List<String> batchedArgs;
	protected int direction;
	protected MergeExecutor mergeExecutor = new MergeExecutor();

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public void setResultSetType(int resultSetType) {
		this.resultSetType = resultSetType;
	}

	public void setResultSetHoldability(int resultSetHoldability) {
		this.resultSetHoldability = resultSetHoldability;
	}

	public void setResultSetConcurrency(int resultSetConcurrency) {
		this.resultSetConcurrency = resultSetConcurrency;
	}

	public MatrixConnection getConnectionWrapper() {
		return connectionWrapper;
	}

	public void setConnectionWrapper(MatrixConnection connection) {
		this.connectionWrapper = connection;
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
	public ResultSet executeQuery(String sql) throws SQLException {
		checkClosed();
		checkParsed(sql);

		parser.eval(null, ShardingType.DB);

		String[] dbNames = parser.getShardingDbNames();
		Assert.notEmpty(dbNames);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MatrixStatement executeQuery: this sql split to " + dbNames.length);
		}

		// MergeResultSet mergeResultSet = new MergeResultSet();

		List<ResultSet> resultSets = new ArrayList<ResultSet>();
		for (String dbName : dbNames) {
			try {
				Connection connection = getConnectionWrapper().getActualConnections().get(dbName);
				if (connection == null) {
					// connection = getConnectionWrapper().getDataSourceWrapper().getDataSource(dbName).getConnection();
					connection = DataSourceUtils.getConnection(getConnectionWrapper().getDataSourceWrapper()
							.getDataSource(dbName));
					connection.setAutoCommit(isAutoCommit());
					getConnectionWrapper().getActualConnections().put(dbName, connection);
				}
				Statement statement = __createStatement(connection);
				actualStatements.add(statement);
				ResultSet resultSet = statement.executeQuery(sql);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("MatrixStatement executeQuery: the targetDb is [" + dbName
							+ "];  number of result rows: " + resultSet.getRow());
				}
				attachedResultSets.add(resultSet);
				resultSets.add(resultSet);
				// mergeResultSet.addResultSet(resultSet);
			} catch (Exception e) {
				throw new SQLException(e);
			}

		}

		MergeResultSet mergeResultSet = mergeExecutor.merge(resultSets.toArray(new ResultSet[resultSets.size()]),
				parser.getParserResult().getStatement());

		moreResults = false;
		updateCount = -1;
		resultSet = new MatrixResultSet(mergeResultSet.getResultSet());
		return resultSet;
	}

	private Statement __createStatement(Connection connection) throws SQLException {
		Statement statement;
		if (resultSetType != -1 && resultSetConcurrency != -1 && resultSetHoldability != -1) {
			statement = connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		} else if (resultSetType != -1 && this.resultSetConcurrency != -1) {
			statement = connection.createStatement(resultSetType, resultSetConcurrency);
		} else {
			statement = connection.createStatement();
		}
		return statement;
	}

	private int __executeUpdate(String sql, int autoGeneratedKeys, int[] columnIndexes, String[] columnNames)
			throws SQLException {
		checkClosed();
		checkParsed(sql);

		parser.eval(null, ShardingType.DB);

		String[] dbNames = parser.getShardingDbNames();
		Assert.notEmpty(dbNames);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MatrixStatement _executeUpdate: this sql split to " + dbNames.length);
		}

		int affectedRows = 0;

		for (String dbName : dbNames) {
			try {
				Connection connection = getConnectionWrapper().getActualConnections().get(dbName);
				if (connection == null) {
					// connection = getConnectionWrapper().getDataSourceWrapper().getDataSource(dbName).getConnection();
					connection = DataSourceUtils.getConnection(getConnectionWrapper().getDataSourceWrapper()
							.getDataSource(dbName));
					connection.setAutoCommit(isAutoCommit());
					getConnectionWrapper().getActualConnections().put(dbName, connection);
				}
				Statement statement = __createStatement(connection);
				actualStatements.add(statement);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("MatrixStatement _executeUpdate: the targetDb is [" + dbName + "]");
				}
				affectedRows += statement.executeUpdate(sql);
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}

		moreResults = false;
		updateCount = affectedRows;
		resultSet = null;
		return affectedRows;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return __executeUpdate(sql, -1, null, null);
	}

	@Override
	public void close() throws SQLException {
		if (closed) {
			return;
		}

		try {
			List<SQLException> sqlExceptions = new ArrayList<SQLException>();

			for (ResultSet resultSet : attachedResultSets) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					sqlExceptions.add(e);
				}

			}

			for (Statement stmt : actualStatements) {
				try {
					stmt.close();
				} catch (SQLException e) {
					sqlExceptions.add(e);
				}
			}

			ExceptionUtils.throwSQLExceptions(LOGGER, sqlExceptions);
		} finally {
			closed = true;
			attachedResultSets.clear();
			actualStatements.clear();
			resultSet = null;
		}
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return maxFieldSize;
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		maxFieldSize = max;
	}

	@Override
	public int getMaxRows() throws SQLException {
		return this.maxRows;
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		this.maxRows = max;
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new UnsupportedOperationException("setEscapeProcessing");
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return queryTimeOut;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		this.queryTimeOut = seconds;
	}

	@Override
	public void cancel() throws SQLException {
		throw new UnsupportedOperationException("cancel");
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		throw new UnsupportedOperationException("setCursorName");
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return __execute(sql, -1, null, null);
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return resultSet;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return updateCount;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return moreResults;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		this.direction = direction;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return direction;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException("setFetchSize");
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException("getFetchSize");
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return resultSetConcurrency;
	}

	@Override
	public int getResultSetType() throws SQLException {
		return resultSetType;
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		throw new UnsupportedOperationException("addBatch");
	}

	@Override
	public void clearBatch() throws SQLException {
		throw new UnsupportedOperationException("clearBatch");
	}

	@Override
	public int[] executeBatch() throws SQLException {
		throw new UnsupportedOperationException("executeBatch");
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connectionWrapper;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return moreResults;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return __executeUpdate(sql, autoGeneratedKeys, null, null);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return __executeUpdate(sql, -1, columnIndexes, null);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return __executeUpdate(sql, -1, null, columnNames);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return __execute(sql, autoGeneratedKeys, null, null);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return __execute(sql, -1, columnIndexes, null);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return __execute(sql, -1, null, columnNames);
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return resultSetHoldability;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		throw new UnsupportedOperationException("setPoolable");
	}

	@Override
	public boolean isPoolable() throws SQLException {
		throw new UnsupportedOperationException("isPoolable");
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		throw new UnsupportedOperationException("closeOnCompletion");
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		throw new UnsupportedOperationException("isCloseOnCompletion");
	}

	protected void checkClosed() throws SQLException {
		if (closed) {
			throw new SQLException();
		}
	}

	private boolean __execute(String sql, int autoGeneratedKeys, int[] columnIndexes, String[] columnNames)
			throws SQLException {
		checkClosed();
		checkParsed(sql);

		switch (parser.getParserResult().getType()) {
		case SELECT:
			executeQuery(sql);
			return true;
		case DELETE:
		case INSERT:
		case UPDATE:
			if (autoGeneratedKeys == -1 && columnIndexes == null && columnNames == null) {
				executeUpdate(sql);
			} else if (autoGeneratedKeys != -1) {
				executeUpdate(sql, autoGeneratedKeys);
			} else if (columnIndexes != null) {
				executeUpdate(sql, columnIndexes);
			} else if (columnNames != null) {
				executeUpdate(sql, columnNames);
			} else {
				executeUpdate(sql);
			}
			return false;
		default:
			throw new SQLParserException("MatrixStatement _execute:not support this sql : " + sql
					+ ";statement type is " + parser.getParserResult().getType());
		}
	}

	protected void checkParsed(String sql) {
		parser = new Parser(sql, this.getConnectionWrapper().getDataSourceWrapper().getRules());
	}
}
