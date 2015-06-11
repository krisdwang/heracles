package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetStringHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setString((Integer) args[0], (String) args[1]);
	}
}
