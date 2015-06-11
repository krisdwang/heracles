package heracles.data.datasource.interceptor;

import heracles.data.common.annotation.TableSharding;
import heracles.data.common.holder.StrategyHolder;
import heracles.data.common.strategy.table.TableShardingStrategy;
import heracles.data.common.util.Utils;
import heracles.data.common.vo.ShardingParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.query.Param;
import org.springframework.util.Assert;

/**
 * 分表拦截器
 * 
 * @author kriswang
 * 
 */
public class TableShardingDataSourceInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(TableShardingDataSourceInterceptor.class);

	/**
	 * key : sharding strategy name, value : TableShardingStrategy instance
	 */
	private Map<String, TableShardingStrategy> tableShardingStrategyMap = new HashMap<String, TableShardingStrategy>();;
	/**
	 * key : sharding strategy name, value : TableShardingStrategy class
	 */
	// private Map<String, Class<?>> tableShardingStrategyClassMap = new HashMap<String, Class<?>>();

	private ApplicationContext applicationContext;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// TODO kriswang 删除掉
		// Long startTime = System.nanoTime();

		String debugInfo = "[" + invocation.toString() + "]";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get into table sharding data source interceptor" + debugInfo);
		}

		// 目前DAO层使用MyBatis Mapper，只有接口，无法获取方法的参数名，因此下面方法无效
		// Method method = invocation.getThis().getClass().getMethod(invocation.getMethod().getName(),
		// invocation.getMethod().getParameterTypes());
		// ParameterNameDiscoverer paraNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		// String[] paraNames = paraNameDiscoverer.getParameterNames(method);

		Method method = invocation.getMethod();
		TableSharding annotation = method.getAnnotation(TableSharding.class);
		if (annotation != null) {
			String strategy = annotation.strategy();
			String key = annotation.key();

			List<String> paramNameList = new ArrayList<String>();
			Annotation[][] annotationDyadicArray = method.getParameterAnnotations();
			if (ArrayUtils.isNotEmpty(annotationDyadicArray)) {
				for (Annotation[] annotations : annotationDyadicArray) {
					if (ArrayUtils.isNotEmpty(annotations)) {
						for (Annotation anno : annotations) {
							if (anno instanceof Param) {
								paramNameList.add(((Param) anno).value());
								break;
							}

							if (anno instanceof org.apache.ibatis.annotations.Param) {
								paramNameList.add(((org.apache.ibatis.annotations.Param) anno).value());
								break;
							}
						}
					}
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("parameter annotations is empty" + debugInfo);
				}
			}

			Assert.notEmpty(paramNameList);

			ShardingParameter shardingParameter = new ShardingParameter();
			shardingParameter.setName(strategy);
			String[] paramNames = new String[paramNameList.size()];
			Object[] args = invocation.getArguments();
			shardingParameter.setValue(Utils.getSpelValue(args, paramNameList.toArray(paramNames), key,
					applicationContext));

			TableShardingStrategy tableShardingStrategy = tableShardingStrategyMap.get(strategy);

			if (tableShardingStrategy != null) {
				tableShardingStrategy.setShardingParameter(shardingParameter);
				StrategyHolder.addTableShardingStrategies(strategy, tableShardingStrategy);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("table sharding strategy is null" + debugInfo);
				}
			}
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("table sharding annotation is null" + debugInfo);
			}
		}

		// TODO kriswang 删除掉
		// Long endTime = System.nanoTime();
		// LOGGER.error("table sharding interceptor time : \t" + (endTime - startTime) + "\t\t" +
		// invocation.getMethod().getName() + "zhuzhenti");

		Object object = invocation.proceed();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get out table sharding dataSource interceptor" + debugInfo);
		}

		return object;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// if (MapUtils.isNotEmpty(tableShardingStrategyClassMap)) {
		// tableShardingStrategyMap = new HashMap<String, TableShardingStrategy>();
		// for (Map.Entry<String, Class<?>> entry : tableShardingStrategyClassMap.entrySet()) {
		// Class<?> clazz = entry.getValue();
		// if (!TableShardingStrategy.class.isAssignableFrom(clazz)) {
		// throw new IllegalArgumentException("class " + clazz.getName() +
		// " is illegal, subclass of TableShardingStrategy is required.");
		// }
		// try {
		// tableShardingStrategyMap.put(entry.getKey(), (TableShardingStrategy) (entry.getValue().newInstance()));
		// }
		// catch (Exception e) {
		// throw new RuntimeException("new instance for class " + clazz.getName() + " failed, error : " +
		// e.getMessage());
		// }
		// }
		//
		// tableShardingStrategyClassMap = null;
		// }
	}

	// public void setTableShardingStrategies(Map<String, Class<?>> tableShardingStrategies) {
	// this.tableShardingStrategyClassMap = tableShardingStrategies;
	// }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setTableShardingStrategies(Map<String, TableShardingStrategy> tableShardingStrategyMap) {
		this.tableShardingStrategyMap = tableShardingStrategyMap;
	}
}
