package heracles.jdbc.atom;

import heracles.jdbc.matrix.model.RuleListModel;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtomDataSource implements DataSource {

	private final static Logger LOGGER = LoggerFactory.getLogger(AtomDataSource.class);

	private DataSource targetDataSource;
	
	private RuleListModel rules;

	public RuleListModel getRules() {
		return rules;
	}

	public void setRules(RuleListModel rules) {
		this.rules = rules;
	}

	public DataSource getTargetDataSource() {
		return targetDataSource;
	}

	public void setTargetDataSource(DataSource targetDataSource) {
		this.targetDataSource = targetDataSource;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.targetDataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.targetDataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.targetDataSource.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.targetDataSource.getLoginTimeout();
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.targetDataSource.getParentLogger();
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
		AtomConnection atomConnection = new AtomConnection(this);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AtomDataSource getConnection: new AtomConnection(this) is successful");
		}
		return atomConnection;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		AtomConnection atomConnection = new AtomConnection(this, username, password);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("AtomDataSource getConnection: new AtomConnection(this, username, password) is successful! username:" + username
					+ ";password:" + password);
		}
		return atomConnection;
	}

}
