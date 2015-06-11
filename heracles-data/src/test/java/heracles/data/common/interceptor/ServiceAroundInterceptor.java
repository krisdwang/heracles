package heracles.data.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAroundInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ServiceAroundInterceptor.class);

	public Object around(org.aspectj.lang.ProceedingJoinPoint proceedingJoinPoint) {
		logger.debug(">>>>>>>>>>>>>>>>>> around.");
		Object ret = null;
		try {
			ret = proceedingJoinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		logger.debug(">>>>>>>>>>>>>>>>>> end around. ");
		return ret;
	}
}
