package heracles.jdbc.common.param;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetArrayHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setArray((Integer) args[0], (Array) args[1]);
	}
}
