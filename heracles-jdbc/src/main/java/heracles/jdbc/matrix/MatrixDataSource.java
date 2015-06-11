package heracles.jdbc.matrix;

import heracles.jdbc.matrix.model.RuleListModel;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class MatrixDataSource implements DataSource {

	private Map<String, DataSource> dataSources;
	
	private RuleListModel rules;

	public RuleListModel getRules() {
		return rules;
	}

	public void setRules(RuleListModel rules) {
		this.rules = rules;
	}

	public Map<String, DataSource> getDataSources() {
		return dataSources;
	}

	public DataSource getDataSource(String key) {
		return dataSources.get(key);
	}

	public void setDataSources(Map<String, DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("getLogWriter");
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException("setLogWriter");
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException("getLoginTimeout");
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException("getParentLogger");
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
	public Connection getConnection() throws SQLException {
		return new MatrixConnection(this);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
	}
}
