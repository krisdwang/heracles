package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Cust;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" }, inheritLocations = true)
public class CustServiceTest {
	@Resource(name = "custService")
	private CustService custService;

	@Test
	public void testCRUD() {
		// save
		Cust cust1 = new Cust();
		cust1.setId(1L);
		cust1.setName("cust1_tom1");
		custService.save(cust1);

		Cust cust2 = new Cust();
		cust2.setId(2L);
		cust2.setName("cust1_tom2");
		custService.save(cust2);

		Cust cust101 = new Cust();
		cust101.setId(101L);
		cust101.setName("cust1_tom101");
		custService.save(cust101);

		Cust cust102 = new Cust();
		cust102.setId(102L);
		cust102.setName("cust1_tom102");
		custService.save(cust102);

		Cust cust201 = new Cust();
		cust201.setId(201L);
		cust201.setName("cust1_tom201");
		custService.save(cust201);

		Cust cust202 = new Cust();
		cust202.setId(202L);
		cust202.setName("cust1_tom202");
		custService.save(cust202);

		// find in slave
		Assert.assertNull(custService.findById(1L));
		Assert.assertNull(custService.findById(2L));
		Assert.assertNull(custService.findById(101L));
		Assert.assertNull(custService.findById(102L));
		Assert.assertNull(custService.findById(201L));
		Assert.assertNull(custService.findById(202L));

		// find in master
		Assert.assertEquals("cust1_tom1", custService.findByIdWithNewWriteTrans(1L).getName());
		Assert.assertEquals("cust1_tom2", custService.findByIdWithNewWriteTrans(2L).getName());
		Assert.assertEquals("cust1_tom101", custService.findByIdWithNewWriteTrans(101L).getName());
		Assert.assertEquals("cust1_tom102", custService.findByIdWithNewWriteTrans(102L).getName());
		Assert.assertEquals("cust1_tom201", custService.findByIdWithNewWriteTrans(201L).getName());
		Assert.assertEquals("cust1_tom202", custService.findByIdWithNewWriteTrans(202L).getName());

		// update
		cust1.setName("cust_john1");
		custService.update(cust1);

		cust2.setName("cust_john2");
		custService.update(cust2);

		cust101.setName("cust_john101");
		custService.update(cust101);

		cust102.setName("cust_john102");
		custService.update(cust102);

		cust201.setName("cust_john201");
		custService.update(cust201);

		cust202.setName("cust_john202");
		custService.update(cust202);

		// find in master
		Assert.assertEquals("cust_john1", custService.findByIdWithNewWriteTrans(1L).getName());
		Assert.assertEquals("cust_john2", custService.findByIdWithNewWriteTrans(2L).getName());
		Assert.assertEquals("cust_john101", custService.findByIdWithNewWriteTrans(101L).getName());
		Assert.assertEquals("cust_john102", custService.findByIdWithNewWriteTrans(102L).getName());
		Assert.assertEquals("cust_john201", custService.findByIdWithNewWriteTrans(201L).getName());
		Assert.assertEquals("cust_john202", custService.findByIdWithNewWriteTrans(202L).getName());

		// delete
		custService.deleteById(1L);
		custService.deleteById(2L);
		custService.deleteById(101L);
		custService.deleteById(102L);
		custService.deleteById(201L);
		custService.deleteById(202L);
	}

	@Test
	public void testFindPage() {
		// save
		Cust cust1 = new Cust();
		cust1.setId(3L);
		cust1.setName("cust1_tom");
		custService.save(cust1);

		Cust cust3 = new Cust();
		cust3.setId(5L);
		cust3.setName("cust1_tom");
		custService.save(cust3);

		Cust cust5 = new Cust();
		cust5.setId(7L);
		cust5.setName("cust1_tom");
		custService.save(cust5);

		Cust cust = new Cust();
		cust.setId(7L);
		cust.setName("cust1_tom");

		Pageable pageable = new PageRequest(0, 2, new Sort(Direction.DESC, "id", "name"));

		Page<Cust> page = custService.findPage(cust, pageable);
		Assert.assertEquals(0, page.getTotalElements());
		Assert.assertEquals(0, page.getTotalPages());

		page = custService.findPageWithNewTrans(cust, pageable);
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(3, page.getTotalElements());
		Assert.assertFalse(page.hasPreviousPage());
		Assert.assertTrue(page.isFirstPage());
		Assert.assertTrue(page.hasNextPage());
		Assert.assertFalse(page.isLastPage());
		Assert.assertEquals(2, page.getContent().size());
		Assert.assertEquals(0, page.getNumber());
		Assert.assertEquals(2, page.getNumberOfElements());

		page = custService.findPageWithNewTrans(cust, pageable);
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(3, page.getTotalElements());
		Assert.assertTrue(page.hasPreviousPage());
		Assert.assertFalse(page.isFirstPage());
		Assert.assertFalse(page.hasNextPage());
		Assert.assertTrue(page.isLastPage());
		Assert.assertEquals(1, page.getContent().size());
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getNumberOfElements());

		custService.deleteById(3L);
		custService.deleteById(5L);
		custService.deleteById(7L);
	}
}
