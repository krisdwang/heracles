package heracles.jdbc.matrix.exception;

public class MatrixException extends RuntimeException {

	private static final long serialVersionUID = -5388541133327164332L;

	public MatrixException(String msg) {
		super(msg);
	}

	public MatrixException(Throwable cause) {
		super(cause);
	}
}
