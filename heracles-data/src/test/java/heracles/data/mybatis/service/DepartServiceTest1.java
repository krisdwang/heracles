package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Depart;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-xsd-all.xml" }, inheritLocations = true)
public class DepartServiceTest1 {
	@Resource(name = "departService")
	private DepartService departService;

	@Test
	public void testCRUD() {
		departService.findById(1L);
		// save
//		Depart depart1 = new Depart();
//		depart1.setId(3L);
//		depart1.setName("depart1_tom1");
//		departService.save(depart1);

//		Depart depart2 = new Depart();
//		depart2.setId(4L);
//		depart2.setName("depart1_tom2");
//		departService.save(depart2);

//		Depart depart101 = new Depart();
//		depart101.setId(103L);
//		depart101.setName("depart1_tom101");
//		departService.save(depart101);

		Depart depart102 = new Depart();
		depart102.setId(104L);
		depart102.setName("depart1_tom102");
		departService.save(depart102);

		Depart depart201 = new Depart();
		depart201.setId(203L);
		depart201.setName("depart1_tom201");
		departService.save(depart201);

		Depart depart202 = new Depart();
		depart202.setId(204L);
		depart202.setName("depart1_tom202");
		departService.save(depart202);

		// find in slave
		Assert.assertNull(departService.findById(104L));
		Assert.assertNull(departService.findById(203L));
		Assert.assertNull(departService.findById(204L));

		// update
//		depart1.setName("depart1_john1");
//		departService.update(depart1);

//		depart2.setName("depart1_john2");
//		departService.update(depart2);

//		depart101.setName("depart1_john101");
//		departService.update(depart101);

		depart102.setName("depart1_john102");
		departService.update(depart102);

		depart201.setName("depart1_john201");
		departService.update(depart201);

		depart202.setName("depart1_john202");
		departService.update(depart202);

		// delete
		departService.deleteById(3L);
		departService.deleteById(4L);
		departService.deleteById(103L);
		departService.deleteById(104L);
		departService.deleteById(203L);
		departService.deleteById(204L);
	}

	@Test
	public void testFindPage() {
		// save
		Depart depart1 = new Depart();
		depart1.setId(21L);
		depart1.setName("depart1_tom");
		departService.save(depart1);

		Depart depart3 = new Depart();
		depart3.setId(23L);
		depart3.setName("depart1_tom");
		departService.save(depart3);

		Depart depart5 = new Depart();
		depart5.setId(25L);
		depart5.setName("depart1_tom");
		departService.save(depart5);

		Depart depart = new Depart();
		depart.setId(21L);
		depart.setName("depart1_tom");

		departService.deleteById(21L);
		departService.deleteById(23L);
		departService.deleteById(25L);
	}
}
