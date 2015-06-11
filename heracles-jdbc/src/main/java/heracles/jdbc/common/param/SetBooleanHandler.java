package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetBooleanHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setBoolean((Integer) args[0], (Boolean) args[1]);
	}
}
