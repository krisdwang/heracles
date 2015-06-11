package heracles.jdbc.parser.expression;

import heracles.jdbc.parser.common.OrderByType;
import heracles.jdbc.parser.expression.variable.Column;
import heracles.jdbc.parser.statement.select.from.Table;

import java.util.Map;

public class OrderByExpr extends Column {

	private OrderByType orderByType = OrderByType.DEFAULT;

	public OrderByExpr(Map<String, Table> tableMap, String name) {
		super(tableMap, name);
	}

	public OrderByExpr(Map<String, Table> tableMap, String name, OrderByType orderByType) {
		super(tableMap, name);
		this.orderByType = orderByType;
	}

	public OrderByType getOrderByType() {
		return orderByType;
	}

	public void setOrderByType(OrderByType orderByType) {
		this.orderByType = orderByType;
	}

	@Override
	public String toStr() {
		String str = super.toStr();

		switch (orderByType) {
		case ASC:
			str += ASC_WS;
			break;
		case DESC:
			str += DESC_WS;
			break;
		default:
			break;
		}

		return str;
	}

}