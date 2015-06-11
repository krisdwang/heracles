package heracles.jdbc.parser.expression.operators.relational;

import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.operators.BinaryExpression;

public class EqualsTo extends BinaryExpression {

	public EqualsTo(Expression leftExpression, Expression rightExpression) {
		setLeftExpression(leftExpression);
		setRightExpression(rightExpression);
	}

	@Override
	public String getOperator() {
		return EQUALS_TO;
	}
}
