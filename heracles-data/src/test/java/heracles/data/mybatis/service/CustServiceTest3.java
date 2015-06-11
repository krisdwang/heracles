package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Cust;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-xsd-simple.xml" }, inheritLocations = true)
public class CustServiceTest3 {
	@Resource(name = "custService")
	private CustService custService;

	@Test
	public void testCRUD() {
		custService.deleteById(11L);
		custService.deleteById(12L);
		custService.deleteById(101L);
		custService.deleteById(102L);
		custService.deleteById(201L);
		custService.deleteById(202L);
		// save
		Cust cust1 = new Cust();
		cust1.setId(11L);
		cust1.setName("cust3_tom1");
		custService.save(cust1);

		Cust cust2 = new Cust();
		cust2.setId(12L);
		cust2.setName("cust3_tom2");
		custService.save(cust2);

		Cust cust101 = new Cust();
		cust101.setId(111L);
		cust101.setName("cust3_tom101");
		custService.save(cust101);

		Cust cust102 = new Cust();
		cust102.setId(112L);
		cust102.setName("cust3_tom102");
		custService.save(cust102);

		Cust cust201 = new Cust();
		cust201.setId(211L);
		cust201.setName("cust3_tom201");
		custService.save(cust201);

		Cust cust202 = new Cust();
		cust202.setId(212L);
		cust202.setName("cust3_tom202");
		custService.save(cust202);

		// find in slave
		Assert.assertNotNull(custService.findById(11L));
		Assert.assertNotNull(custService.findById(12L));
		Assert.assertNotNull(custService.findById(111L));
		Assert.assertNotNull(custService.findById(112L));
		Assert.assertNotNull(custService.findById(211L));
		Assert.assertNotNull(custService.findById(212L));

		// update
		cust1.setName("cust3_john1");
		custService.update(cust1);

		cust2.setName("cust3_john2");
		custService.update(cust2);

		cust101.setName("cust3_john101");
		custService.update(cust101);

		cust102.setName("cust3_john102");
		custService.update(cust102);

		cust201.setName("cust3_john201");
		custService.update(cust201);

		cust202.setName("cust3_john202");
		custService.update(cust202);

		// delete
		custService.deleteById(11L);
		custService.deleteById(12L);
		custService.deleteById(111L);
		custService.deleteById(112L);
		custService.deleteById(211L);
		custService.deleteById(212L);
	}
}
