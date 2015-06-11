package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Depart;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-xsd-dbcp.xml" }, inheritLocations = true)
public class DepartServiceTest {
	@Resource(name = "departService")
	private DepartService departService;

	@Test
	public void testCRUD() {
		// delete
		departService.deleteById(1L);
		departService.deleteById(2L);
		departService.deleteById(101L);
		departService.deleteById(102L);
		departService.deleteById(201L);
		departService.deleteById(202L);
		// save
		Depart depart1 = new Depart();
		depart1.setId(1L);
		depart1.setName("depart_tom1");
		departService.save(depart1);

		Depart depart2 = new Depart();
		depart2.setId(2L);
		depart2.setName("depart_tom2");
		departService.save(depart2);

		Depart depart101 = new Depart();
		depart101.setId(101L);
		depart101.setName("depart_tom101");
		departService.save(depart101);

		Depart depart102 = new Depart();
		depart102.setId(102L);
		depart102.setName("depart_tom102");
		departService.save(depart102);

		Depart depart201 = new Depart();
		depart201.setId(201L);
		depart201.setName("depart_tom201");
		departService.save(depart201);

		Depart depart202 = new Depart();
		depart202.setId(202L);
		depart202.setName("depart_tom202");
		departService.save(depart202);

		// find in slave
		Assert.assertNull(departService.findById(1L));
		Assert.assertNull(departService.findById(2L));
		Assert.assertNull(departService.findById(101L));
		Assert.assertNull(departService.findById(102L));
		Assert.assertNull(departService.findById(201L));
		Assert.assertNull(departService.findById(202L));

		// update
		depart1.setName("depart_john1");
		departService.update(depart1);

		depart2.setName("depart_john2");
		departService.update(depart2);

		depart101.setName("depart_john101");
		departService.update(depart101);

		depart102.setName("depart_john102");
		departService.update(depart102);

		depart201.setName("depart_john201");
		departService.update(depart201);

		depart202.setName("depart_john202");
		departService.update(depart202);

		// delete
		 departService.deleteById(1L);
		 departService.deleteById(2L);
		 departService.deleteById(101L);
		 departService.deleteById(102L);
		 departService.deleteById(201L);
		 departService.deleteById(202L);
	}

	@Test
	public void testFindPage() {
		departService.deleteById(11L);
		departService.deleteById(13L);
		departService.deleteById(15L);
		// save
		Depart depart1 = new Depart();
		depart1.setId(11L);
		depart1.setName("depart_tom");
		departService.save(depart1);

		Depart depart3 = new Depart();
		depart3.setId(13L);
		depart3.setName("depart_tom");
		departService.save(depart3);

		Depart depart5 = new Depart();
		depart5.setId(15L);
		depart5.setName("depart_tom");
		departService.save(depart5);

		Depart depart = new Depart();
		depart.setId(1L);
		depart.setName("depart_tom");

		departService.deleteById(11L);
		departService.deleteById(13L);
		departService.deleteById(15L);
	}
}
