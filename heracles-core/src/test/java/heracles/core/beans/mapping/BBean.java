package heracles.core.beans.mapping;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class BBean {
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String desc;
	@Getter
	@Setter
	private String forList0;
	@Getter
	@Setter
	private String forList1;

	@Getter
	@Setter
	private String forMapFirst;

	@Getter
	@Setter
	private String forInnerName;

	@Getter
	@Setter
	private NestBBean nestBBean;

	@Getter
	@Setter
	private Map<String, InnerBean> innerBeansMap;

	@Getter
	@Setter
	private Date createTime;

	@Getter
	@Setter
	private long updateTime;
}
