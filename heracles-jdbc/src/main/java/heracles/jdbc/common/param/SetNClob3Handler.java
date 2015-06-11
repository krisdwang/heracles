package heracles.jdbc.common.param;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetNClob3Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setNClob((Integer) args[0], (Reader) args[1], (Long) args[2]);
	}
}
