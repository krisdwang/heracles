package heracles.security.web.authentication;

import heracles.security.utils.WebUtils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class MultiMediaTypesAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static final Logger logger = LoggerFactory.getLogger(MultiMediaTypesAuthenticationFailureHandler.class);
			
	@Setter
	private SimpleUrlAuthenticationFailureHandler splAuthFailHdlr;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String ctetType = request.getContentType();
		logger.info("onAuthenticationFailure, content type:{}, exception:{}", ctetType, exception.getMessage());
		
		if (null == ctetType || StringUtils.equalsIgnoreCase(WebUtils.CONTENT_TYPE_HTML, ctetType)) {
			splAuthFailHdlr.onAuthenticationFailure(request, response, exception);
		} else if (StringUtils.equalsIgnoreCase(WebUtils.CONTENT_TYPE_JSON, ctetType)) {
			WebUtils.writeJsonResponse(HttpServletResponse.SC_UNAUTHORIZED,
					"Authentication Failed: " + exception.getMessage(), response);
		} else {
			splAuthFailHdlr.onAuthenticationFailure(request, response, exception);
		}

	}

}
