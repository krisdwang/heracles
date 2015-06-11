package heracles.jdbc.group;

import heracles.jdbc.group.strategy.LoadBalanceStrategy;
import heracles.jdbc.matrix.model.RuleListModel;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDataSource implements DataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(GroupDataSource.class);

	private Map<String, DataSource> targetDataSources;

	private LoadBalanceStrategy<String> lbStrategy;
	
	private RuleListModel rules;

	public RuleListModel getRules() {
		return rules;
	}

	public void setRules(RuleListModel rules) {
		this.rules = rules;
	}

	public Map<String, DataSource> getTargetDataSources() {
		return targetDataSources;
	}

	public void setTargetDataSources(Map<String, DataSource> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}

	public LoadBalanceStrategy<String> getLbStrategy() {
		return lbStrategy;
	}

	public void setLbStrategy(LoadBalanceStrategy<String> lbStrategy) {
		this.lbStrategy = lbStrategy;
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
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
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
		GroupConnection groupConnection = new GroupConnection(this);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GroupDataSource getConnection : new GroupConnection(this) is successful");
		}
		return groupConnection;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		GroupConnection groupConnection = new GroupConnection(this, username, password);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GroupDataSource getConnection : new GroupConnection(this, username, password) is successful");
		}
		return groupConnection;
	}

}
