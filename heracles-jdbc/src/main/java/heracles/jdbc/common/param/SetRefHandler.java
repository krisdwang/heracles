package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;

public class SetRefHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setRef((Integer) args[0], (Ref) args[1]);
	}
}
