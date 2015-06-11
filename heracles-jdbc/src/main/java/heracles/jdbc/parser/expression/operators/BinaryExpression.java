package heracles.jdbc.parser.expression.operators;

import heracles.jdbc.parser.expression.Expression;

public abstract class BinaryExpression implements Expression {

	private Expression leftExpression;
	private Expression rightExpression;

	public BinaryExpression() {
	}

	public Expression getLeftExpression() {
		return leftExpression;
	}

	public Expression getRightExpression() {
		return rightExpression;
	}

	public void setLeftExpression(Expression expression) {
		leftExpression = expression;
	}

	public void setRightExpression(Expression expression) {
		rightExpression = expression;
	}

	@Override
	public String toStr() {
		return String.format("%s %s %s", getLeftExpression().toStr(), getOperator(), getRightExpression().toStr());
	}

	public abstract String getOperator();
}
