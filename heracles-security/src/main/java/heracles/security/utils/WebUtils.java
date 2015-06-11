package heracles.security.utils;

import heracles.core.exception.BusinessException;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class WebUtils extends org.springframework.web.util.WebUtils {

	public static final String CONTENT_TYPE_HTML = "text/html";

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final String UTF_8 = "UTF-8";

	private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);
			
	/**
	 * Write unauthorized status and message to response.
	 * 
	 * @param response
	 */
	public static void writeJsonResponse(int httpStatus, String msg, HttpServletResponse response) {
		response.setStatus(httpStatus);
		response.setCharacterEncoding(UTF_8);
		response.setContentType(WebUtils.CONTENT_TYPE_JSON);

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			String errMsg = "Failed to get response writer";
			logger.error(errMsg, e);
			throw new BusinessException(errMsg, "write.response.exception", e);
		}
		JSONObject json = new JSONObject();
		json.put("message", msg);
		out.print(json);
		out.flush();
	}
}
