package heracles.jdbc.parser.statement.update;

import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.common.Utils;
import heracles.jdbc.parser.expression.ExpressionList;
import heracles.jdbc.parser.statement.StatementWithWhere;

import org.springframework.util.Assert;

public class Update extends StatementWithWhere {
	private ExpressionList setList;

	@Override
	public StatementType getType() {
		return StatementType.UPDATE;
	}

	@Override
	public String toStr() {
		Assert.isTrue(setList.isNotEmpty(), "setList is empty");
		Assert.notNull(fromItem);
		Assert.notNull(where);

		return String.format("UPDATE %s SET %s WHERE %s", fromItem.toStr(), setList.toStr(), Utils.exprToSQL(where));
	}

	public ExpressionList getSetList() {
		return setList;
	}

	public void setSetList(ExpressionList setList) {
		this.setList = setList;
	}
}
