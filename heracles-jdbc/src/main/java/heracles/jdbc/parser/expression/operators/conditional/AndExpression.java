package heracles.jdbc.parser.expression.operators.conditional;

import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.operators.BinaryExpression;

public class AndExpression extends BinaryExpression {

	public AndExpression(Expression leftExpression, Expression rightExpression) {
		setLeftExpression(leftExpression);
		setRightExpression(rightExpression);
	}

	@Override
	public String getOperator() {
		return AND;
	}
}
