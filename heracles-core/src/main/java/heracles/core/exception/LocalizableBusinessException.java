package heracles.core.exception;

import java.util.Arrays;

import lombok.Getter;

/**
 * make the exception support localization, include localizable arguments, will use erroCode as localization key
 * 
 * @author kriswang
 *
 */
public class LocalizableBusinessException extends BusinessException {

	private static final long serialVersionUID = 165731697357562374L;

	/**
	 * The array of arguments to be used to resolve localization message
	 */
	@Getter
	protected Object[] arguments;

	public LocalizableBusinessException(String message, String errorCode) {
		super(message, errorCode);
	}

	public LocalizableBusinessException(String message, String errorCode, Object[] arguments) {
		super(message, errorCode);
		this.arguments = Arrays.copyOf(arguments, arguments.length);
	}

	public LocalizableBusinessException(String message, String errorCode, Throwable cause) {
		super(message, errorCode, cause);
	}

	public LocalizableBusinessException(String message, String errorCode, Object[] arguments, Throwable cause) {
		super(message, errorCode, cause);
		this.arguments = Arrays.copyOf(arguments, arguments.length);
	}
}
