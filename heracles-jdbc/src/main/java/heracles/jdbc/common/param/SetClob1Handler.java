package heracles.jdbc.common.param;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetClob1Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setClob((Integer) args[0], (Clob) args[1]);
	}
}
