package heracles.jdbc.parser.expression;

import heracles.jdbc.parser.common.Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class ExpressionList implements ItemList {
	private List<Expression> expressionList = new ArrayList<Expression>();

	public ExpressionList() {
	}

	public ExpressionList(List<Expression> expressionList) {
		this.expressionList = expressionList;
	}

	public void addExpression(Expression expression) {
		expressionList.add(expression);
	}

	public List<Expression> getExpressionList() {
		return expressionList;
	}

	public void setExpressionList(List<Expression> expressionList) {
		this.expressionList = expressionList;
	}

	@Override
	public String toStr() {
		StringBuilder sb = new StringBuilder();
		for (Expression expr : expressionList) {
			sb.append(expr.toStr() + COMMA_WS);
		}

		return Utils.trimLastTwoChars(sb.toString());
	}

	@Override
	public int size() {
		if (CollectionUtils.isEmpty(expressionList)) {
			return 0;
		}

		return expressionList.size();
	}

	@Override
	public boolean isEmpty() {
		return !isNotEmpty();
	}

	@Override
	public boolean isNotEmpty() {
		return size() > 0;
	}
}
