package heracles.jdbc.parser.exception;

public class SQLParserException extends RuntimeException {

	private static final long serialVersionUID = -5388541133327164332L;

	public SQLParserException(String msg) {
		super(msg);
	}

	public SQLParserException(Throwable cause) {
		super(cause);
	}
}
