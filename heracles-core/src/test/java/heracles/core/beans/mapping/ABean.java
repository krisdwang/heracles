package heracles.core.beans.mapping;

import heracles.core.beans.mapping.annotation.MapClass;
import heracles.core.beans.mapping.annotation.MapField;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@MapClass("heracles.core.beans.mapping.BBean")
public class ABean {
	@Getter
	@Setter
	@MapField("desc")
	private String name;

	@Getter
	@Setter
	@MapField("name")
	private String desc;

	@Getter
	@Setter
	@MapField(complexMap = "listField[0]=forList0,listField[1]=forList1")
	private List<String> listField;

	@Getter
	@Setter
	@MapField(complexMap = "mapField['first']=forMapFirst")
	private Map<String, String> mapField;

	@Getter
	@Setter
	@MapField(complexMap = "innerBean.innerName=forInnerName")
	private InnerBean innerBean;

	@Getter
	@Setter
	@MapField("nestBBean")
	private NestABean nestABean;

	@Getter
	@Setter
	@MapField(complexMap = "innerBeans{innerName}=innerBeansMap{key},innerBeans{}=innerBeansMap{value}")
	private List<InnerBean> innerBeans;

	@Getter
	@Setter
	private String createTime;

	@Getter
	@Setter
	private Date updateTime;
}
