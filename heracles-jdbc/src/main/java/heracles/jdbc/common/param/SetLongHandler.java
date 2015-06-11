package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetLongHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setLong((Integer) args[0], (Long) args[1]);
	}
}
