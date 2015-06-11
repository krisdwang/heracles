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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class MultiMediaTypesAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(MultiMediaTypesAuthenticationSuccessHandler.class);

	@Setter
	private SimpleUrlAuthenticationSuccessHandler splAuthSuccHdlr;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String ctetType = request.getContentType();
		logger.info("onAuthenticationSuccess, content type:{}, uid:{}", ctetType, authentication.getName());

		if (null == ctetType || StringUtils.equalsIgnoreCase(WebUtils.CONTENT_TYPE_HTML, ctetType)) {
			splAuthSuccHdlr.onAuthenticationSuccess(request, response, authentication);
		} else if (StringUtils.equalsIgnoreCase(WebUtils.CONTENT_TYPE_JSON, ctetType)) {
			WebUtils.writeJsonResponse(HttpServletResponse.SC_OK, "Authentication Success", response);
		} else {
			splAuthSuccHdlr.onAuthenticationSuccess(request, response, authentication);
		}
	}

}
