package heracles.data.datasource.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

public class RandomLoadBalanceStrategy implements LoadBalanceStrategy<String> {

	private List<String> targets;
	private List<String> failedTargets;
	private final Random random = new Random();

	public RandomLoadBalanceStrategy(List<String> targets) {
		Assert.notNull(targets);
		this.targets = Collections.synchronizedList(targets);
		Assert.notEmpty(targets);
		failedTargets = Collections.synchronizedList(new ArrayList<String>(targets.size()));
	}

	@Override
	public synchronized String elect() {
		if (CollectionUtils.isEmpty(this.targets)) {
			return null;
		}
		return targets.get(random.nextInt(targets.size()));
	}

	@Override
	public synchronized void removeTarget(String target) {
		if (targets.contains(target)) {
			targets.remove(target);
			failedTargets.add(target);
		}
	}

	@Override
	public synchronized void recoverTarget(String target) {
		if (failedTargets.contains(target)) {
			targets.add(target);
			failedTargets.remove(target);
		}
	}
}
