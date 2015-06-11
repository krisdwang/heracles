package heracles.data.common.id.impl;

import heracles.data.common.id.ShardingKey;

@Deprecated
public class ShardingKeyImpl implements ShardingKey {

	@Override
	public Object getKey() {
		return 1L;
	}
}
