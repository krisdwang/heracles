package heracles.jdbc.parser.expression.function;

import heracles.jdbc.parser.common.FunctionType;
import heracles.jdbc.parser.expression.variable.Column;

public class MinFunc extends Function {

	public MinFunc(Column column) {
		this.column = column;
	}

	@Override
	public FunctionType getFunctionType() {
		return FunctionType.MIN;
	}
}
