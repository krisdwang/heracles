package heracles.jdbc.group.strategy;

public interface LoadBalanceStrategy<T> {

	T elect();

	void removeTarget(T t);

	void recoverTarget(T t);
}
