package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Depart;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-xsd-druid.xml" }, inheritLocations = true)
public class DepartServiceTest2 {
	@Resource(name = "departService")
	private DepartService departService;

	@Test
	public void testCRUD() {
		// save
		Depart depart1 = new Depart();
		depart1.setId(5L);
		depart1.setName("depart2_tom1");
		departService.save(depart1);

		Depart depart2 = new Depart();
		depart2.setId(6L);
		depart2.setName("depart2_tom2");
		departService.save(depart2);

		Depart depart101 = new Depart();
		depart101.setId(105L);
		depart101.setName("depart2_tom101");
		departService.save(depart101);

		Depart depart102 = new Depart();
		depart102.setId(106L);
		depart102.setName("depart2_tom102");
		departService.save(depart102);

		Depart depart201 = new Depart();
		depart201.setId(205L);
		depart201.setName("depart2_tom201");
		departService.save(depart201);

		Depart depart202 = new Depart();
		depart202.setId(206L);
		depart202.setName("depart2_tom202");
		departService.save(depart202);

		// find in slave
		Assert.assertNotNull(departService.findById(5L));
		Assert.assertNotNull(departService.findById(6L));
		Assert.assertNotNull(departService.findById(105L));
		Assert.assertNotNull(departService.findById(106L));
		Assert.assertNotNull(departService.findById(205L));
		Assert.assertNotNull(departService.findById(206L));

		// update
		depart1.setName("depart2_john1");
		departService.update(depart1);

		depart2.setName("depart2_john2");
		departService.update(depart2);

		depart101.setName("depart2_john101");
		departService.update(depart101);

		depart102.setName("depart2_john102");
		departService.update(depart102);

		depart201.setName("depart2_john201");
		departService.update(depart201);

		depart202.setName("depart2_john202");
		departService.update(depart202);

		// delete
		departService.deleteById(5L);
		departService.deleteById(6L);
		departService.deleteById(105L);
		departService.deleteById(106L);
		departService.deleteById(205L);
		departService.deleteById(206L);
	}

	@Test
	public void testFindPage() {
		// save
		Depart depart1 = new Depart();
		depart1.setId(31L);
		depart1.setName("depart2_tom");
		departService.save(depart1);

		Depart depart3 = new Depart();
		depart3.setId(33L);
		depart3.setName("depart2_tom");
		departService.save(depart3);

		Depart depart5 = new Depart();
		depart5.setId(35L);
		depart5.setName("depart2_tom");
		departService.save(depart5);

		Depart depart = new Depart();
		depart.setId(31L);
		depart.setName("depart2_tom");

		departService.deleteById(31L);
		departService.deleteById(33L);
		departService.deleteById(35L);
	}
}
