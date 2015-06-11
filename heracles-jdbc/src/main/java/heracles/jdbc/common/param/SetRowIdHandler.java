package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;

public class SetRowIdHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setRowId((Integer) args[0], (RowId) args[1]);
	}
}
