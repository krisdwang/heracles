package heracles.jdbc.parser.expression;

import org.apache.commons.lang3.StringUtils;

public class Alias implements Expression {

	private String name;
	private boolean useAs = false;

	public Alias(String name) {
		this.name = name;
	}

	public Alias(String name, boolean useAs) {
		this.name = name;
		this.useAs = useAs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUseAs() {
		return useAs;
	}

	public void setUseAs(boolean useAs) {
		this.useAs = useAs;
	}

	@Override
	public String toStr() {
		return String.format("%s%s", useAs ? AS_WS : EMPTY, StringUtils.isNotBlank(name) ? name : EMPTY);
	}
}
