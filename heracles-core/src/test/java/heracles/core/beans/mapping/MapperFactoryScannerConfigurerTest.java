package heracles.core.beans.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import heracles.core.beans.mapping.BeanMapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-beanMapper.xml" })
@ActiveProfiles(value = "test")
public class MapperFactoryScannerConfigurerTest {

	@Autowired
	private BeanMapper beanMapper;

	@Test
	public void testMapperFactoryFromBToA() throws ParseException {
		BBean bBean = new BBean();
		bBean.setName("bname");
		bBean.setDesc("bdesc");
		bBean.setForList0("bforlist0");
		bBean.setForList1("bforlist1");
		bBean.setForMapFirst("bForMapFirst");
		bBean.setForInnerName("forInnerName");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String createTime = "2015-06-08";
		bBean.setCreateTime(format.parse(createTime));
		bBean.setUpdateTime(1413080853865l);

		NestBBean nestBBean = new NestBBean();
		nestBBean.setName("nestBBeanName");
		nestBBean.setDesc("nestBBeanDesc");
		bBean.setNestBBean(nestBBean);

		Map<String, InnerBean> innerBeansMap = new HashMap<String, InnerBean>();
		InnerBean innerBean = new InnerBean();
		innerBean.setInnerName("innerName");
		innerBean.setInnerDesc("innerDesc");
		innerBeansMap.put(innerBean.getInnerName(), innerBean);
		bBean.setInnerBeansMap(innerBeansMap);

		ABean abean = beanMapper.map(bBean, ABean.class);
		assertThat(abean.getCreateTime(), equalTo(createTime));
		assertThat(abean.getName(), equalTo("bdesc"));
		assertThat(abean.getListField().get(0), equalTo("bforlist0"));
		assertThat(abean.getListField().get(1), equalTo("bforlist1"));
		assertThat(abean.getMapField().get("first"), equalTo("bForMapFirst"));
		assertThat(abean.getInnerBean().getInnerName(), equalTo("forInnerName"));
		assertThat(abean.getNestABean().getName(), equalTo("nestBBeanDesc"));
		assertThat(abean.getInnerBeans().get(0).getInnerName(), equalTo("innerName"));
	}

	@Test
	public void testMapperFactoryFromAToB() {
		ABean aBean = new ABean();
		aBean.setName("aname");
		aBean.setDesc("adesc");

		List<String> listField = new ArrayList<String>();
		listField.add("aList0");
		listField.add("aList1");
		aBean.setListField(listField);

		Map<String, String> mapField = new HashMap<String, String>();
		mapField.put("first", "aMapFirst");
		mapField.put("Second", "aMapSecond");
		aBean.setMapField(mapField);

		NestABean nestABean = new NestABean();
		nestABean.setName("nestABeanName");
		nestABean.setDesc("nestABeanDesc");
		aBean.setNestABean(nestABean);

		// looks like the list to map mapping have bug in object cast
		BBean bbean = beanMapper.map(aBean, BBean.class);
		assertThat(bbean.getName(), equalTo("adesc"));
		assertThat(bbean.getForList0(), equalTo("aList0"));
		assertThat(bbean.getForList1(), equalTo("aList1"));
		assertThat(bbean.getForMapFirst(), equalTo("aMapFirst"));
		assertThat(bbean.getNestBBean().getName(), equalTo("nestABeanDesc"));
	}
}
