package heracles.jdbc.parser.expression.variable;

public class Numerical implements Variable {

	private String value;

	public Numerical() {
	}

	public Numerical(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toStr() {
		return value;
	}
}