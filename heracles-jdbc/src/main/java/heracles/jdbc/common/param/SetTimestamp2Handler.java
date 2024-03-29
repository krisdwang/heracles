package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class SetTimestamp2Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setTimestamp((Integer) args[0], (Timestamp) args[1], (Calendar) args[2]);
	}
}
