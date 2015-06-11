package heracles.jdbc.parser.expression.function;

import heracles.jdbc.parser.common.FunctionType;
import heracles.jdbc.parser.expression.variable.Column;

public class CountFunc extends Function {

	public CountFunc(Column column) {
		this.column = column;
	}

	public CountFunc() {
		this.allColumns = true;
	}

	@Override
	public FunctionType getFunctionType() {
		return FunctionType.COUNT;
	}
}
