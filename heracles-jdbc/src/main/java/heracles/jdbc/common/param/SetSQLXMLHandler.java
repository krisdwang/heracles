package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;

public class SetSQLXMLHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setSQLXML((Integer) args[0], (SQLXML) args[1]);
	}
}
