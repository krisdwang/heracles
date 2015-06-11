package heracles.data.datasource.interceptor;

import heracles.data.common.annotation.TableSharding;
import heracles.data.common.holder.StrategyHolder;
import heracles.data.common.strategy.table.TableShardingStrategy;
import heracles.data.common.util.Utils;
import heracles.data.common.vo.ShardingParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * table sharding datasource interceptor
 * 
 * @author kriswang
 * 
 */
@Deprecated
public class TableShardingDataSourceForCriteriaInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

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
		Object[] args = invocation.getArguments();

		Method method = invocation.getThis().getClass().getMethod(invocation.getMethod().getName(), invocation.getMethod().getParameterTypes());
		ParameterNameDiscoverer paraNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNames = paraNameDiscoverer.getParameterNames(method);

		Annotation annotation = method.getAnnotation(TableSharding.class);
		if (annotation instanceof TableSharding) {
			String strategy = ((TableSharding) annotation).strategy();
			String key = ((TableSharding) annotation).key();

			ShardingParameter shardingParameter = new ShardingParameter();
			shardingParameter.setName(strategy);
			shardingParameter.setValue(Utils.getSpelValue(args, paraNames, key, applicationContext));

			TableShardingStrategy tableShardingStrategy = tableShardingStrategyMap.get(strategy);

			if (tableShardingStrategy != null) {
				tableShardingStrategy.setShardingParameter(shardingParameter);
				StrategyHolder.addTableShardingStrategies(strategy, tableShardingStrategy);
			}
		}

		return invocation.proceed();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// if (MapUtils.isNotEmpty(tableShardingStrategyClassMap)) {
		// tableShardingStrategyMap = new HashMap<String, TableShardingStrategy>();
		// for (Map.Entry<String, Class<?>> entry : tableShardingStrategyClassMap.entrySet()) {
		// Class<?> clazz = entry.getValue();
		// if (!TableShardingStrategy.class.isAssignableFrom(clazz)) {
		// throw new IllegalArgumentException("class " + clazz.getName() + " is illegal, subclass of TableShardingStrategy is required.");
		// }
		// try {
		// tableShardingStrategyMap.put(entry.getKey(), (TableShardingStrategy) (entry.getValue().newInstance()));
		// }
		// catch (Exception e) {
		// throw new RuntimeException("new instance for class " + clazz.getName() + " failed, error : " + e.getMessage());
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
