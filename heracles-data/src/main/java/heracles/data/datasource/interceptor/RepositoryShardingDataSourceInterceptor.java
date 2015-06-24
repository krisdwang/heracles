package heracles.data.datasource.interceptor;

import heracles.data.common.annotation.RepositorySharding;
import heracles.data.common.holder.StrategyHolder;
import heracles.data.common.strategy.repository.NoRepositoryShardingStrategy;
import heracles.data.common.strategy.repository.RepositoryShardingStrategy;
import heracles.data.common.util.Utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 分库拦截器
 * 
 * @author kriswang
 * 
 */
public class RepositoryShardingDataSourceInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(RepositoryShardingDataSourceInterceptor.class);

	/**
	 * key : sharding strategy name, value : RepositoryShardingStrategy instance
	 */
	private Map<String, RepositoryShardingStrategy> repositoryShardingStrategyMap = new HashMap<String, RepositoryShardingStrategy>();

	private ApplicationContext applicationContext;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String debugInfo = "[" + invocation.toString() + "]";

		if (log.isDebugEnabled()) {
			log.debug("get into repository sharding data source interceptor" + debugInfo);
		}

		Method method = invocation.getThis().getClass().getMethod(invocation.getMethod().getName(), invocation.getMethod().getParameterTypes());
		RepositoryShardingStrategy repositoryShardingStrategy = null;

		// 根据服务类方法上的分库注解获取分库策略和分库参数
		RepositorySharding annotation = method.getAnnotation(RepositorySharding.class);

		Object obj = null;
		if (annotation != null) {
			String strategy = annotation.strategy();
			String key = annotation.key();
			Object[] args = invocation.getArguments();

			ParameterNameDiscoverer paraNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
			String[] paraNames = paraNameDiscoverer.getParameterNames(method);

			obj = Utils.getSpelValue(args, paraNames, key, applicationContext);

			repositoryShardingStrategy = repositoryShardingStrategyMap.get(strategy);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("repository sharding annotation is null" + debugInfo);
			}
		}

		Transactional annotationTx = method.getAnnotation(Transactional.class);

		if (repositoryShardingStrategy == null) {
			repositoryShardingStrategy = NoRepositoryShardingStrategy.getInstance();
		}

		if (StrategyHolder.getRepositoryShardingKey() == null) {
			if (log.isDebugEnabled()) {
				log.debug("repository sharding strategy is null in StrategyHolder" + debugInfo);
			}

			StrategyHolder.setRepositoryShardingKey(repositoryShardingStrategy.getReadWriteDataSource(obj));
		} else {
			if (Propagation.REQUIRES_NEW.equals(annotationTx.propagation())
					|| Propagation.NEVER.equals(annotationTx.propagation())) {
				if (log.isDebugEnabled()) {
					log.debug("repository sharding strategy is null in StrategyHolder" + debugInfo);
				}

				StrategyHolder.setRepositoryShardingKey(repositoryShardingStrategy.getReadWriteDataSource(obj));
			} else {
				// FIXME kris 策略不同抛异常
				if (!StrategyHolder.getRepositoryShardingKey().equals(repositoryShardingStrategy.getReadWriteDataSource(obj))) {
					throw new IllegalArgumentException("repository sharding key of current method is diffrent with parent method!");
				} 
			}
		}

		Object object = invocation.proceed();

		if (log.isDebugEnabled()) {
			log.debug("get out repository sharding data source interceptor" + debugInfo);
		}

		return object;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	public void setRepositoryShardingStrategies(Map<String, RepositoryShardingStrategy> repositoryShardingStrategyMap) {
		this.repositoryShardingStrategyMap = repositoryShardingStrategyMap;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
