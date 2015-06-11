package heracles.jdbc.parser.expression.function;

import heracles.jdbc.parser.common.FunctionType;
import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.variable.Column;

public abstract class Function implements Expression {

	protected Column column;
	protected boolean allColumns = false;

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public boolean isAllColumns() {
		return allColumns;
	}

	public void setAllColumns(boolean allColumns) {
		this.allColumns = allColumns;
	}

	public String getFunctionName() {
		return getFunctionType().getName();
	}

	public abstract FunctionType getFunctionType();

	@Override
	public String toStr() {
		return String.format("%s(%s)", getFunctionName(), isAllColumns() ? STAR : column.toStr());
	}
}
