package heracles.jdbc.parser.expression.operators.conditional;

import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.operators.BinaryExpression;

public class OrExpression extends BinaryExpression {

	public OrExpression(Expression leftExpression, Expression rightExpression) {
		setLeftExpression(leftExpression);
		setRightExpression(rightExpression);
	}

	@Override
	public String getOperator() {
		return OR;
	}
}
