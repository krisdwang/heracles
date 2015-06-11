/**
 * 
 */
package heracles.data.common.interceptor;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * if BindingResult was comment with @ResolveError, it will be checked by BindingResultResolverInterceptor
 * @author kriswang
 * @version 0.0.1
 * @since 2014/12/23 11:37
 */
public class DaoResultInterceptor implements MethodInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(DaoResultInterceptor.class);

	/**
	 * Create a new BindingResultInterceptor.
	 */
	public DaoResultInterceptor() {
	}

	public void afterAdvice() {
		logger.debug(">>>>>>>>>>>>>>>>>>afterAdvice.");
	}

	public Object around(ProceedingJoinPoint proceedingJoinPoint) {
		logger.debug(">>>>>>>>>>>>>>>>>> around. ");
		Object ret = null;
		try {
			ret = proceedingJoinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		logger.debug(">>>>>>>>>>>>>>>>>> around end.");
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] args = invocation.getArguments();

		logger.debug(">>>>>>>>>>>>>>>>>> args: " + invocation.getMethod().getAnnotations());
		logger.debug(">>>>>>>>>>>>>>>>>> method.name: " + invocation.getMethod().getName());
		Annotation[][] annos = invocation.getMethod().getParameterAnnotations();
		for (int i = 0; i < annos.length; i++) {
			for (int j = 0; j < annos[i].length; j++) {
				logger.debug(">>>>>>>>>>>>>>>>>> annotationType: " + annos[i][j].annotationType());
			}
		}

		Object object = invocation.proceed();
		return object;
	}

}
