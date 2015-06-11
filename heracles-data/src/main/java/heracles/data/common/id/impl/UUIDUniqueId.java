package heracles.data.common.id.impl;

import heracles.data.common.id.IdGenerator;

import java.util.UUID;

@Deprecated
public class UUIDUniqueId implements IdGenerator {

	@Override
	public Long getId() {
		return UUID.randomUUID().getMostSignificantBits();
	}
}
