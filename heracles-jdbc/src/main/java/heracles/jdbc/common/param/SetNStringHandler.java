package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetNStringHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setNString((Integer) args[0], (String) args[1]);
	}
}
