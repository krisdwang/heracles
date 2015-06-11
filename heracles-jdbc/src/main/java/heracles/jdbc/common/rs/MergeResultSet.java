package heracles.jdbc.common.rs;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.rowset.CachedRowSetImpl;

public class MergeResultSet {

	private CachedRowSetImpl rowSet;
	private boolean empty = true;

	public MergeResultSet(ResultSet rs) throws SQLException {
		rowSet = new CachedRowSetImpl();
		rs.beforeFirst();
		rowSet.populate(rs);
		empty = false;
	}

	public MergeResultSet() throws SQLException {
		rowSet = new CachedRowSetImpl();
	}

	public void update(int rowIndex, int parameterIndex, Object object) throws SQLException {
		rowSet.absolute(rowIndex);
		rowSet.updateObject(parameterIndex, object);
		rowSet.updateRow();
		rowSet.beforeFirst();
	}

	public ResultSet addResultSet(ResultSet rs) throws SQLException {
		rs.beforeFirst();

		if (empty) {
			rowSet.populate(rs);
			empty = false;
			return rowSet;
		}

		while (rs.next()) {
			rowSet.moveToInsertRow();
			for (int j = 1; j <= rowSet.getMetaData().getColumnCount(); j++) {
				rowSet.updateObject(j, rs.getObject(j));
			}
			rowSet.insertRow();
			rowSet.moveToCurrentRow();
		}

		return rowSet;
	}

	public ResultSet getResultSet() {
		return rowSet;
	}
}
