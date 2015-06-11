package heracles.jdbc.atom.exception;

public class AtomException extends RuntimeException {

	private static final long serialVersionUID = -562159352147086509L;

	public AtomException(String msg) {
		super(msg);
	}

	public AtomException(Throwable cause) {
		super(cause);
	}
}
