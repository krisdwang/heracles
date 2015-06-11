package heracles.jdbc.parser.statement.select;

import heracles.jdbc.parser.common.Element;
import heracles.jdbc.parser.common.JoinType;
import heracles.jdbc.parser.common.Utils;
import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.statement.select.from.FromItem;

import org.springframework.util.Assert;

public class Join implements Element {
	private Expression onExpression;
	private FromItem fromItem;
	private JoinType joinType = JoinType.INNER;

	public Expression getOnExpression() {
		return onExpression;
	}

	public void setOnExpression(Expression onExpression) {
		this.onExpression = onExpression;
	}

	public FromItem getFromItem() {
		return fromItem;
	}

	public void setFromItem(FromItem fromItem) {
		this.fromItem = fromItem;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public Join(JoinType joinType, FromItem fromItem, Expression onExpression) {
		this.joinType = joinType;
		this.fromItem = fromItem;
		this.onExpression = onExpression;
	}

	@Override
	public String toStr() {
		Assert.notNull(onExpression);
		Assert.notNull(fromItem);

		return String.format("%s %s ON %s", joinType.toStr(), fromItem.toStr(), Utils.exprToSQL(onExpression));
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Join join = (Join) super.clone();
		join.setFromItem((FromItem) join.getFromItem().clone());
		return join;
	}
}
