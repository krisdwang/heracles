package heracles.jdbc.common.param;

import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetNClob1Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setNClob((Integer) args[0], (NClob) args[1]);
	}
}
