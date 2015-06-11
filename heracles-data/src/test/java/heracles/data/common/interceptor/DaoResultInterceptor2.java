/**
 * 
 */
package heracles.data.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

/**
 * if BindingResult was comment with @ResolveError, it will be checked by BindingResultResolverInterceptor
 * @author kriswang
 * @version 0.0.1
 * @since 2014/12/23 11:37
 */
public class DaoResultInterceptor2 implements Ordered {

	private static final Logger logger = LoggerFactory.getLogger(DaoResultInterceptor2.class);

	/**
	 * Create a new BindingResultInterceptor.
	 */
	public DaoResultInterceptor2() {
	}

	public void afterAdvice2() {
		logger.debug(">>>>>>>>>>>>>>>>>> afterAdvice2: ");
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
