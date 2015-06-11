package heracles.core.beans.mapping;

import heracles.core.beans.mapping.annotation.MapClass;
import heracles.core.beans.mapping.annotation.MapField;
import lombok.Getter;
import lombok.Setter;


@MapClass("heracles.core.beans.mapping.NestBBean")
public class NestABean {
	@Getter
	@Setter
	@MapField("desc")
	private String name;

	@Getter
	@Setter
	@MapField("name")
	private String desc;
}
