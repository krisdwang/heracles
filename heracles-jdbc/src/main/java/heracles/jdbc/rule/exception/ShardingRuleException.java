package heracles.jdbc.rule.exception;

public class ShardingRuleException extends RuntimeException {

	private static final long serialVersionUID = -5388541133327164332L;

	public ShardingRuleException(String msg) {
		super(msg);
	}

	public ShardingRuleException(Throwable cause) {
		super(cause);
	}
}
