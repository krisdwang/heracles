package heracles.jdbc.common.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

public class SetTime2Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setTime((Integer) args[0], (Time) args[1], (Calendar) args[2]);
	}
}
