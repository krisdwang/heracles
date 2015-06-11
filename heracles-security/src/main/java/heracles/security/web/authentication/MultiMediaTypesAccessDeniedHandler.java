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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class MultiMediaTypesAccessDeniedHandler implements AccessDeniedHandler {

	private static final Logger logger = LoggerFactory.getLogger(MultiMediaTypesAccessDeniedHandler.class);

	@Setter
	private AccessDeniedHandler acesDenHdlr;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		String ctetType = request.getContentType();
		logger.info("AccessDeniedHandler handle, content type:{}, exception:{}", ctetType,
				accessDeniedException.getMessage());

		if (null == ctetType || StringUtils.equalsIgnoreCase(WebUtils.CONTENT_TYPE_HTML, ctetType)) {
			acesDenHdlr.handle(request, response, accessDeniedException);
		} else if (StringUtils.equalsIgnoreCase(WebUtils.CONTENT_TYPE_JSON, ctetType)) {
			WebUtils.writeJsonResponse(HttpServletResponse.SC_FORBIDDEN, "Authentication Failed: "
					+ accessDeniedException.getMessage(), response);
		} else {
			acesDenHdlr.handle(request, response, accessDeniedException);
		}
	}

}
