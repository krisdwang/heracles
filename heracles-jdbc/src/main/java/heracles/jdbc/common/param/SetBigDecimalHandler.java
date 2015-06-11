package heracles.jdbc.common.param;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetBigDecimalHandler implements ParameterHandler {
	@Override
	public void setParameter(PreparedStatement stmt, Object[] args) throws SQLException {
		stmt.setBigDecimal((Integer) args[0], (BigDecimal) args[1]);
	}
}
