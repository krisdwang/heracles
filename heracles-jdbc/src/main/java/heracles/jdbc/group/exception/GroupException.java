package heracles.jdbc.group.exception;

public class GroupException extends RuntimeException {

	private static final long serialVersionUID = 3850370059338430939L;

	public GroupException(String msg) {
		super(msg);
	}

	public GroupException(Throwable cause) {
		super(cause);
	}
}
