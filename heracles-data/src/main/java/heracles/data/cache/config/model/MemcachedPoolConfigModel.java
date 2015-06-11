package heracles.data.cache.config.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MemcachedPoolConfigModel extends PoolConfigModel {

	private String maxTotal;
	
}
