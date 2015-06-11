package heracles.data.datasource.interceptor;

import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.holder.StrategyHolder;
import heracles.data.common.util.ReadWriteType;
import heracles.data.datasource.ReadWriteDataSourceKey;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注解式读写分离拦截器
 * 
 * @author kriswang
 * 
 */
public class AnnotationReadWriteDataSourceInterceptor implements MethodInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationReadWriteDataSourceInterceptor.class);

	private ReadWriteDataSourceKey dataSourceKey;

	private Map<String, ReadWriteDataSourceKey> readWriteDataSourceKeyMap = new HashMap<String, ReadWriteDataSourceKey>();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// TODO kriswang 删除掉
		// Long startTime = System.nanoTime();

		String debugInfo = "[" + invocation.toString() + "]";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get into read/write data source interceptor" + debugInfo);
		}

		String repositoryShardingKey = StrategyHolder.getRepositoryShardingKey();
		if (StringUtils.isBlank(repositoryShardingKey)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("repository sharding strategy is null" + debugInfo);
			}

			for (Iterator<String> iterator = readWriteDataSourceKeyMap.keySet().iterator(); iterator.hasNext();) {
				dataSourceKey = readWriteDataSourceKeyMap.get(iterator.next());
				//StrategyHolder.setRepositoryShardingKey(iterator.next());
				break;
			}
		}
		else {
			dataSourceKey = readWriteDataSourceKeyMap.get(repositoryShardingKey);
		}

		if (dataSourceKey != null) {
			ReadWrite readWriteAnnotation = invocation.getThis().getClass().getMethod(invocation.getMethod().getName(), invocation.getMethod().getParameterTypes()).getAnnotation(ReadWrite.class);
			if (readWriteAnnotation == null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("read/write annotation is null" + debugInfo);
				}

				dataSourceKey.setWriteKey();

				Object object = invocation.proceed();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("get out read/write data source interceptor" + debugInfo);
				}

				return object;
			}

			ReadWriteType type = readWriteAnnotation.type();
			//
			// if (Utils.isRead(annotationKey)) {
			// dataSourceKey.setReadKey();
			// }
			// else if (Utils.isWrite(annotationKey)) {
			// dataSourceKey.setWriteKey();
			// }
			// else {
			// dataSourceKey.setKey(annotationKey);
			// }

			switch (type) {
			case READ:
				dataSourceKey.setReadKey();
				break;
			default:
				dataSourceKey.setWriteKey();
				break;
			}
		}
		else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("data source key is null" + debugInfo);
			}
		}

		// TODO kriswang 删除掉
		// Long endTime = System.nanoTime();
		// LOGGER.error("read/write interceptor time : \t" + (endTime - startTime) + "\t\t" + invocation.getMethod().getName() + "zhuzhenrwi");

		Object object = invocation.proceed();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get out read/write data source interceptor" + debugInfo);
		}

		return object;
	}

	public void setDataSourceKey(ReadWriteDataSourceKey dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}

	public void setReadWriteDataSourceKeys(Map<String, ReadWriteDataSourceKey> readWriteDataSourceKeyMap) {
		this.readWriteDataSourceKeyMap = readWriteDataSourceKeyMap;
	}

}
