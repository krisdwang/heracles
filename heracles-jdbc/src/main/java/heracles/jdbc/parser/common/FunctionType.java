package heracles.jdbc.parser.common;

public enum FunctionType {
	MAX(Constants.MAX), MIN(Constants.MIN), AVG(Constants.AVG), SUM(Constants.SUM), COUNT(Constants.COUNT);

	private String name;

	private FunctionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
