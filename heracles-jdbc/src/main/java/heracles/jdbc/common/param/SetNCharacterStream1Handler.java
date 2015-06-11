package heracles.jdbc.common.param;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetNCharacterStream1Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setNCharacterStream((Integer) args[0], (Reader) args[1]);
	}
}
