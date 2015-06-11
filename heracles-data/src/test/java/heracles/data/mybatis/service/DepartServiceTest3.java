package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Depart;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-xsd-druid1.xml" }, inheritLocations = true)
public class DepartServiceTest3 {
	@Resource(name = "departService")
	private DepartService departService;

	@Test
	public void testCRUD() {
		// save
		Depart depart1 = new Depart();
		depart1.setId(555L);
		depart1.setName("depart3_tom");
		departService.save(depart1);

		// delete
		departService.deleteById(555L);
	}
}
