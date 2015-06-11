package heracles.jdbc.parser.expression.function;

import heracles.jdbc.parser.common.FunctionType;
import heracles.jdbc.parser.expression.variable.Column;

public class SumFunc extends Function {

	public SumFunc(Column column) {
		this.column = column;
	}

	@Override
	public FunctionType getFunctionType() {
		return FunctionType.SUM;
	}
}
