package heracles.jdbc.common.param;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetDate1Handler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setDate((Integer) args[0], (Date) args[1]);
	}
}
