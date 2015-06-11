package heracles.jdbc.common.param;

public class ParameterContext {
	private ParameterMethod parameterMethod;

	private Object[] args;

	public ParameterContext() {
	}

	public ParameterContext(ParameterMethod parameterMethod, Object[] args) {
		this.parameterMethod = parameterMethod;
		this.args = args;
	}

	public ParameterMethod getParameterMethod() {
		return parameterMethod;
	}

	public void setParameterMethod(ParameterMethod parameterMethod) {
		this.parameterMethod = parameterMethod;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(parameterMethod).append("(");
		for (int i = 0; i < args.length; ++i) {
			buffer.append(args[i]);
			if (i != args.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");

		return buffer.toString();
	}
}
