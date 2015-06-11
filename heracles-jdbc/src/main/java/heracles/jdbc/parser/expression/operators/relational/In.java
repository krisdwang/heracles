package heracles.jdbc.parser.expression.operators.relational;

import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.ExpressionList;
import heracles.jdbc.parser.expression.operators.BinaryExpression;

public class In extends BinaryExpression {

	public In(Expression leftExpression, ExpressionList rightExpressionList) {
		setLeftExpression(leftExpression);
		setRightExpression(rightExpressionList);
	}

	@Override
	public String getOperator() {
		return IN;
	}
}
