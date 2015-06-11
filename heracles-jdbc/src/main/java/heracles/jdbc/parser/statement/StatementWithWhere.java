package heracles.jdbc.parser.statement;

import heracles.jdbc.parser.expression.Expression;

public abstract class StatementWithWhere extends Statement {

	protected Expression where;

	public Expression getWhere() {
		return where;
	}

	public void setWhere(Expression where) {
		this.where = where;
	}
}
