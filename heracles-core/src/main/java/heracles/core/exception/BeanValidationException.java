package heracles.core.exception;

import lombok.Getter;

import org.springframework.validation.Errors;

/**
 * Used to translate the spring bean validation errors to the exception and make it easy to be caught in WEB
 * @author kriswang
 *
 */
public class BeanValidationException extends BusinessException {

	private static final long serialVersionUID = 8202857501514012480L;

	private static final String DEFAULT_BEAN_VALIDATION_EXCEPTION_ERRORCODE = "bean.validation.exception";

	private static final String DEFAULT_BEAN_VALIDATION_EXCEPTION_MEESAGE = "bean validation error!";

	/**
	 * The errors which contains the filed name and the error message
	 */
	@Getter
	private Errors errors;

	public BeanValidationException(Errors errors) {
		super(DEFAULT_BEAN_VALIDATION_EXCEPTION_MEESAGE, DEFAULT_BEAN_VALIDATION_EXCEPTION_ERRORCODE);
		this.errors = errors;
	}

}
