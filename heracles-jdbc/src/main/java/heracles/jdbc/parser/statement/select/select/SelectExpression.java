package heracles.jdbc.parser.statement.select.select;

import heracles.jdbc.parser.expression.Alias;
import heracles.jdbc.parser.expression.Expression;

public class SelectExpression implements SelectItem {

	private Expression expression;
	private Alias alias;

	public SelectExpression() {
	}

	public SelectExpression(Expression expression) {
		this.expression = expression;
	}

	public Alias getAlias() {
		return alias;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setAlias(Alias alias) {
		this.alias = alias;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public String toStr() {
		return String.format("%s%s", expression.toStr(), alias != null ? SPACE + alias.toStr() : EMPTY);
	}
}
