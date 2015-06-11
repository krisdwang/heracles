package heracles.jdbc.common.cache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 6500777206462582664L;

	public final static int DEFAULT_SIZE = 10000;
	protected int maxSize = DEFAULT_SIZE;

	public LRUCache(int maxSize) {
		super(maxSize, 0.75f, true);
		this.maxSize = maxSize;
	}

	public LRUCache() {
		super(DEFAULT_SIZE, 0.75f, true);
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return size() > this.maxSize;
	}

}
