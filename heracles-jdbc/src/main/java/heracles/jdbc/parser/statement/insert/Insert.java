package heracles.jdbc.parser.statement.insert;

import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.expression.ExpressionList;
import heracles.jdbc.parser.statement.Statement;

import org.springframework.util.Assert;

public class Insert extends Statement {

	private ExpressionList columnList;
	private ExpressionList expressionList;

	@Override
	public StatementType getType() {
		return StatementType.INSERT;
	}

	@Override
	public String toStr() {
		Assert.isTrue(columnList.isNotEmpty(), "columnList is empty");
		Assert.isTrue(expressionList.isNotEmpty(), "expressionList is empty");
		Assert.notNull(fromItem);

		return String.format("INSERT INTO %s (%s) VALUES (%s)", fromItem.toStr(), columnList.toStr(), expressionList.toStr());
	}

	public ExpressionList getColumnList() {
		return columnList;
	}

	public void setColumnList(ExpressionList columnList) {
		this.columnList = columnList;
	}

	public ExpressionList getExpressionList() {
		return expressionList;
	}

	public void setExpressionList(ExpressionList expressionList) {
		this.expressionList = expressionList;
	}
}
