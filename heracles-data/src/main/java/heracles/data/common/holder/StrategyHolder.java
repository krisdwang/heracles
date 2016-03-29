package heracles.data.common.holder;

import heracles.data.common.strategy.table.TableShardingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections.CollectionUtils;

public class StrategyHolder {
	/**
	 * repository sharding strategy
	 */
	private static final ThreadLocal<Stack<ShardingStrategy>> REPOSITORY_SHARDING_STRATEGY_STACK = new ThreadLocal<Stack<ShardingStrategy>>();

	/**
	 * table sharding strategies
	 */
	private static final ThreadLocal<Map<String, TableShardingStrategy>> TABLE_SHARDING_STRATEGIES = new ThreadLocal<Map<String, TableShardingStrategy>>();

	public static synchronized void setRepositoryShardingKey(String key) {
		Stack<ShardingStrategy> stack = REPOSITORY_SHARDING_STRATEGY_STACK.get();
		if (stack == null) {
			stack = new Stack<ShardingStrategy>();
		}
		
		ShardingStrategy strategy = new ShardingStrategy();
		strategy.setRepositoryShardingKey(key);
		if(!stack.contains(strategy)) {
			stack.push(strategy);
		}
		REPOSITORY_SHARDING_STRATEGY_STACK.set(stack);
	}

	public static String getRepositoryShardingKey() {
		Stack<ShardingStrategy> stack = REPOSITORY_SHARDING_STRATEGY_STACK.get();
		if (CollectionUtils.isEmpty(stack)) {
			return null;
		}
		ShardingStrategy strategy = stack.peek();
		return strategy.getRepositoryShardingKey();
	}

	public static void removeRepositoryShardingStrategy() {
		Stack<ShardingStrategy> stack = REPOSITORY_SHARDING_STRATEGY_STACK.get();
		if (CollectionUtils.isEmpty(stack)) {
			// FIXME kris 此处请报错
			REPOSITORY_SHARDING_STRATEGY_STACK.remove();
			return;
		}
		stack.pop();
		if (stack.empty()) {
			REPOSITORY_SHARDING_STRATEGY_STACK.remove();
		}
	}

	/**
	 * 重置datasourcekey为null
	 */
	public static void clearDataSourceKey() {
		Stack<ShardingStrategy> stack = REPOSITORY_SHARDING_STRATEGY_STACK.get();
		if (CollectionUtils.isEmpty(stack)) {
			// FIXME kris 此处请报错
			REPOSITORY_SHARDING_STRATEGY_STACK.remove();
			return;
		}
		ShardingStrategy strategy = stack.pop();
		strategy.setDataSourceKey(null);
		stack.push(strategy);
		REPOSITORY_SHARDING_STRATEGY_STACK.set(stack);
	}

	/**
	 * pushes an item onto the top of this stack
	 * 
	 * @param key
	 */
	public static void setDataSourceKey(String key) {
		Stack<ShardingStrategy> stack = REPOSITORY_SHARDING_STRATEGY_STACK.get();
		if (CollectionUtils.isEmpty(stack)) {
			// FIXME kris 此处请报错
			stack = new Stack<ShardingStrategy>();
			ShardingStrategy strategy = new ShardingStrategy();
			strategy.setDataSourceKey(key);
			stack.push(strategy);
			// return;
		} else {
			ShardingStrategy strategy = stack.pop();
			strategy.setDataSourceKey(key);
			stack.push(strategy);
		}
		REPOSITORY_SHARDING_STRATEGY_STACK.set(stack);
	}

	public static String getDataSourceKey() {
		Stack<ShardingStrategy> stack = REPOSITORY_SHARDING_STRATEGY_STACK.get();
		if (CollectionUtils.isEmpty(stack)) {
			// FIXME kris 此处请报错
			return null;
		}
		ShardingStrategy strategy = stack.peek();

		return strategy.getDataSourceKey();
	}

	public static TableShardingStrategy getTableShardingStrategy(String key) {
		Map<String, TableShardingStrategy> map = TABLE_SHARDING_STRATEGIES.get();
		if (map == null) {
			return null;
		}
		return map.get(key);
	}

	public static Map<String, TableShardingStrategy> getTableShardingStrategies() {
		return TABLE_SHARDING_STRATEGIES.get();
	}

	public static void addTableShardingStrategies(String key, TableShardingStrategy value) {
		Map<String, TableShardingStrategy> map = TABLE_SHARDING_STRATEGIES.get();
		if (map == null) {
			map = new HashMap<String, TableShardingStrategy>();
			TABLE_SHARDING_STRATEGIES.set(map);
		}

		if (!map.containsKey(key)) {
			map.put(key, value);
		}
	}
}
