package heracles.jdbc.parser.common;

public enum JoinType {
	RIGHT(Constants.RIGHT_JOIN), LEFT(Constants.LEFT_JOIN), INNER(Constants.JOIN);

	private String str;

	private JoinType(String str) {
		this.str = str;
	}

	public String toStr() {
		return str;
	}
}
