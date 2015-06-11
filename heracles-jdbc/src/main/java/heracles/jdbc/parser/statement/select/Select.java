package heracles.jdbc.parser.statement.select;

import heracles.jdbc.parser.common.HintType;
import heracles.jdbc.parser.common.MergeType;
import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.common.Utils;
import heracles.jdbc.parser.expression.OrderByExpr;
import heracles.jdbc.parser.expression.function.Function;
import heracles.jdbc.parser.expression.variable.Numerical;
import heracles.jdbc.parser.statement.StatementWithWhere;
import heracles.jdbc.parser.statement.select.from.FromItem;
import heracles.jdbc.parser.statement.select.from.Table;
import heracles.jdbc.parser.statement.select.select.SelectExpression;
import heracles.jdbc.parser.statement.select.select.SelectItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class Select extends StatementWithWhere {
	private List<SelectItem> selectItems = new ArrayList<SelectItem>();
	private List<Join> joins = new ArrayList<Join>();
	private List<FromItem> fromItems = new ArrayList<FromItem>();
	private List<OrderByExpr> orderByExprs = new ArrayList<OrderByExpr>();
	private Numerical offset;
	private Numerical rowCount;
	private MergeType mergeType = null;
	private HintType hintType = null;

	public HintType getHintType() {
		return hintType;
	}

	public void setHintType(HintType hintType) {
		this.hintType = hintType;
	}

	public MergeType getMergeType() {
		return mergeType;
	}

	public List<SelectItem> getSelectItems() {
		return selectItems;
	}

	public List<FromItem> getFromItems() {
		return fromItems;
	}

	public void setFromItems(List<FromItem> fromItems) {
		this.fromItems = fromItems;
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}

	public List<FromItem> addFromItem(FromItem fromItem) {
		fromItems.add(fromItem);
		return fromItems;
	}

	public List<SelectItem> addSelectItem(SelectItem selectItem) {
		selectItems.add(selectItem);

		if ((selectItem instanceof SelectExpression)
				&& ((SelectExpression) selectItem).getExpression() instanceof Function) {
			if (mergeType == null) {
				mergeType = MergeType.FUNCTION;
			}
		} else {
			mergeType = MergeType.NONE;
		}

		return selectItems;
	}

	public List<Join> addJoin(Join join) {
		joins.add(join);
		return joins;
	}

	public List<OrderByExpr> getOrderByExprs() {
		return orderByExprs;
	}

	public void setOrderByExprs(List<OrderByExpr> orderByExprs) {
		this.orderByExprs = orderByExprs;
	}

	public Numerical getOffset() {
		return offset;
	}

	public void setOffset(Numerical offset) {
		this.offset = offset;
	}

	public Numerical getRowCount() {
		return rowCount;
	}

	public void setRowCount(Numerical rowCount) {
		this.rowCount = rowCount;
	}

	public void setLimit(Numerical offset, Numerical rowCount) {
		this.offset = offset;
		this.rowCount = rowCount;
	}

	public List<OrderByExpr> addOrderByExpr(OrderByExpr orderByExpr) {
		orderByExprs.add(orderByExpr);
		return orderByExprs;
	}

	@Override
	public StatementType getType() {
		return StatementType.SELECT;
	}

	public void setTableName(String oldName, String newName) {
		List<FromItem> allFromItems = new ArrayList<FromItem>();
		allFromItems.add(fromItem);
		if (CollectionUtils.isNotEmpty(fromItems)) {
			allFromItems.addAll(fromItems);
		} else if (CollectionUtils.isNotEmpty(joins)) {
			for (Join join : joins) {
				allFromItems.add(join.getFromItem());
			}
		}

		for (FromItem fromItem : allFromItems) {
			if (fromItem instanceof Table) {
				((Table) fromItem).setTableName(oldName, newName);
			} else {
				throw new UnsupportedOperationException("only support table");
			}
		}
	}

	@Override
	public String toStr() {
		Assert.notEmpty(selectItems);
		Assert.notNull(fromItem);
		Assert.notNull(where);

		StringBuilder sb = new StringBuilder();
		for (SelectItem selectItem : selectItems) {
			sb.append(selectItem.toStr() + COMMA_WS);
		}

		StringBuilder fromItemSb = new StringBuilder();
		if (CollectionUtils.isNotEmpty(fromItems)) {
			for (FromItem fromItem : fromItems) {
				fromItemSb.append(COMMA_WS + fromItem.toStr());
			}
		}

		String joinSql = EMPTY;
		if (CollectionUtils.isNotEmpty(joins)) {
			StringBuilder joinSb = new StringBuilder();
			for (Join join : joins) {
				joinSb.append(join.toStr() + SPACE);
			}
			joinSql = SPACE + StringUtils.trim(joinSb.toString());
		}

		StringBuilder orderByExprSb = new StringBuilder();
		String orderBySql = EMPTY;
		if (CollectionUtils.isNotEmpty(orderByExprs)) {
			orderByExprSb.append(SPACE + ORDER_BY + SPACE);
			for (OrderByExpr orderByExpr : orderByExprs) {
				orderByExprSb.append(orderByExpr.toStr() + COMMA_WS);
			}
			orderBySql = Utils.trimLastTwoChars(orderByExprSb);
		}

		StringBuilder limitSb = new StringBuilder();
		if (rowCount != null) {
			limitSb.append(SPACE + LIMIT + SPACE);
			if (offset != null) {
				limitSb.append(offset.toStr() + COMMA_WS + rowCount.toStr());
			} else {
				limitSb.append(rowCount.toStr());
			}
		}

		return String.format("SELECT %s FROM %s%s%s WHERE %s%s%s", Utils.trimLastTwoChars(sb), fromItem.toStr(),
				fromItemSb.toString(), joinSql, Utils.exprToSQL(where), orderBySql, limitSb.toString());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Select select = (Select) super.clone();
		select.setFromItem((FromItem) select.getFromItem().clone());

		List<Join> joins = select.getJoins();
		List<Join> cloneJoins = new ArrayList<Join>(joins.size());
		Iterator<Join> itJoins = joins.iterator();
		while (itJoins.hasNext()) {
			cloneJoins.add((Join) itJoins.next().clone());
		}
		select.setJoins(cloneJoins);

		List<FromItem> fromItems = select.getFromItems();
		List<FromItem> cloneFromItems = new ArrayList<FromItem>(joins.size());
		Iterator<FromItem> itFromItems = fromItems.iterator();
		while (itFromItems.hasNext()) {
			cloneFromItems.add((FromItem) itFromItems.next().clone());
		}
		select.setFromItems(cloneFromItems);

		return select;
	}
}
