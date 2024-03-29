package heracles.jdbc.group.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class RoundRobinLoadBalanceStrategy implements LoadBalanceStrategy<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoundRobinLoadBalanceStrategy.class);

	private static final int MIN_LB_FACTOR = 1;

	private List<String> targets;
	private int currentPos;

	private Map<String, Integer> currentTargets;
	private Map<String, Integer> failedTargets;

	public RoundRobinLoadBalanceStrategy(Map<String, Integer> lbFactors) {
		Assert.notNull(lbFactors);
		currentTargets = Collections.synchronizedMap(lbFactors);
		Assert.notEmpty(lbFactors);
		failedTargets = Collections.synchronizedMap(new HashMap<String, Integer>(currentTargets.size()));
		reInitTargets(currentTargets);
	}

	private void reInitTargets(Map<String, Integer> lbFactors) {
		targets = initTargets(lbFactors);
		// Assert.notEmpty(targets);
		if (CollectionUtils.isEmpty(targets)) {
			LOGGER.error("targets is empty");
		}
		currentPos = 0;
	}

	public List<String> initTargets(Map<String, Integer> lbFactors) {
		if (MapUtils.isEmpty(lbFactors)) {
			return null;
		}

		fixFactor(lbFactors);

		Collection<Integer> factors = lbFactors.values();

		int min = Collections.min(factors);
		if (min > MIN_LB_FACTOR && canModAll(min, factors)) {
			return buildBalanceTargets(lbFactors, min);
		}

		return buildBalanceTargets(lbFactors, MIN_LB_FACTOR);
	}

	protected synchronized List<String> getTargets() {
		if (targets == null) {
			targets = new ArrayList<String>();
		}
		return targets;
	}

	private void fixFactor(Map<String, Integer> lbFactors) {
		Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
		for (Map.Entry<String, Integer> entry : setEntries) {
			if (entry.getValue() < MIN_LB_FACTOR) {
				entry.setValue(MIN_LB_FACTOR);
			}
		}
	}

	private boolean canModAll(int base, Collection<Integer> factors) {
		for (Integer integer : factors) {
			if (integer % base != 0) {
				return false;
			}
		}
		return true;
	}

	private List<String> buildBalanceTargets(Map<String, Integer> lbFactors, int baseFactor) {
		Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
		List<String> targets = new ArrayList<String>();
		for (Map.Entry<String, Integer> entry : setEntries) {
			int factor = entry.getValue() / baseFactor;

			for (int i = 0; i < factor; i++) {
				targets.add(entry.getKey());
			}
		}
		return targets;
	}

	@Override
	public synchronized String elect() {
		if (CollectionUtils.isEmpty(this.targets)) {
			return null;
		}
		if (currentPos >= targets.size()) {
			currentPos = 0;
		}
		return targets.get(currentPos++);
	}

	@Override
	public synchronized void removeTarget(String key) {
		if (currentTargets.containsKey(key)) {
			failedTargets.put(key, currentTargets.get(key));
			currentTargets.remove(key);
			reInitTargets(currentTargets);
		}
	}

	@Override
	public synchronized void recoverTarget(String key) {
		if (failedTargets.containsKey(key)) {
			currentTargets.put(key, failedTargets.get(key));
			failedTargets.remove(key);
			reInitTargets(currentTargets);
		}
	}
}
