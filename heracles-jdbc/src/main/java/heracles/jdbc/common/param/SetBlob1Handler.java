package heracles.jdbc.common.param;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetBlob1Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setBlob((Integer) args[0], (Blob) args[1]);
	}
}
