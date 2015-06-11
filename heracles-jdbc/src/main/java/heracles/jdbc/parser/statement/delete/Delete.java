package heracles.jdbc.parser.statement.delete;

import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.common.Utils;
import heracles.jdbc.parser.statement.StatementWithWhere;

import org.springframework.util.Assert;

public class Delete extends StatementWithWhere {
	@Override
	public StatementType getType() {
		return StatementType.DELETE;
	}

	@Override
	public String toStr() {
		Assert.notNull(fromItem);
		Assert.notNull(where);

		return String.format("DELETE FROM %s WHERE %s", fromItem.toStr(), Utils.exprToSQL(where));
	}
}
