package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Order;

import java.util.List;

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
public class OrderServiceTest {
	@Resource(name = "orderService")
	private OrderService orderService;

	@Test
	public void testCRUD() {
		// delete
		orderService.deleteById(1L);
		orderService.deleteById(2L);
		orderService.deleteById(101L);
		orderService.deleteById(102L);
		orderService.deleteById(201L);
		orderService.deleteById(202L);
		
		// save
		Order order1 = new Order();
		order1.setId(1L);
		order1.setName("order_tom1");
		order1.setUserId(1L);
		orderService.save(order1);

		Order order2 = new Order();
		order2.setId(2L);
		order2.setName("order_tom2");
		order2.setUserId(1L);
		orderService.save(order2);

		Order order101 = new Order();
		order101.setId(101L);
		order101.setName("order_tom101");
		order101.setUserId(1L);
		orderService.save(order101);

		Order order102 = new Order();
		order102.setId(102L);
		order102.setName("order_tom102");
		order102.setUserId(1L);
		orderService.save(order102);

		Order order201 = new Order();
		order201.setId(201L);
		order201.setName("order_tom201");
		order201.setUserId(1L);
		orderService.save(order201);

		Order order202 = new Order();
		order202.setId(202L);
		order202.setName("order_tom202");
		order202.setUserId(1L);
		orderService.save(order202);

		// find in slave
		Assert.assertNull(orderService.findById(1L));
		Assert.assertNull(orderService.findById(2L));
		Assert.assertNull(orderService.findById(101L));
		Assert.assertNull(orderService.findById(102L));
		Assert.assertNull(orderService.findById(201L));
		Assert.assertNull(orderService.findById(202L));

		// find in master
		Assert.assertEquals("order_tom1", orderService.findByIdWithNewWriteTrans(1L).getName());
		Assert.assertEquals("order_tom2", orderService.findByIdWithNewWriteTrans(2L).getName());
		Assert.assertEquals("order_tom101", orderService.findByIdWithNewWriteTrans(101L).getName());
		Assert.assertEquals("order_tom102", orderService.findByIdWithNewWriteTrans(102L).getName());
		Assert.assertEquals("order_tom201", orderService.findByIdWithNewWriteTrans(201L).getName());
		Assert.assertEquals("order_tom202", orderService.findByIdWithNewWriteTrans(202L).getName());

		// update
		order1.setName("order_john1");
		orderService.update(order1);

		order2.setName("order_john2");
		orderService.update(order2);

		order101.setName("order_john101");
		orderService.update(order101);

		order102.setName("order_john102");
		orderService.update(order102);

		order201.setName("order_john201");
		orderService.update(order201);

		order202.setName("order_john202");
		orderService.update(order202);

		// find in master
		Assert.assertEquals("order_john1", orderService.findByIdWithNewWriteTrans(1L).getName());
		Assert.assertEquals("order_john2", orderService.findByIdWithNewWriteTrans(2L).getName());
		Assert.assertEquals("order_john101", orderService.findByIdWithNewWriteTrans(101L).getName());
		Assert.assertEquals("order_john102", orderService.findByIdWithNewWriteTrans(102L).getName());
		Assert.assertEquals("order_john201", orderService.findByIdWithNewWriteTrans(201L).getName());
		Assert.assertEquals("order_john202", orderService.findByIdWithNewWriteTrans(202L).getName());

		// delete
		orderService.deleteById(1L);
		orderService.deleteById(2L);
		orderService.deleteById(101L);
		orderService.deleteById(102L);
		orderService.deleteById(201L);
		orderService.deleteById(202L);
	}

	@Test
	public void testFindPage() {
		orderService.deleteById(1L);
		orderService.deleteById(3L);
		orderService.deleteById(5L);
		
		// save
		Order order1 = new Order();
		order1.setId(1L);
		order1.setName("order_tom");
		order1.setUserId(1L);
		orderService.save(order1);

		Order order3 = new Order();
		order3.setId(3L);
		order3.setName("order_tom");
		order3.setUserId(1L);
		orderService.save(order3);

		Order order5 = new Order();
		order5.setId(5L);
		order5.setName("order_tom");
		order5.setUserId(1L);
		orderService.save(order5);

		Order order = new Order();
		order.setId(1L);
		order.setName("order_tom");

		Pageable pageable = new PageRequest(0, 2, new Sort(Direction.DESC, "id", "name"));

		Page<Order> page = orderService.findPage(order, pageable);
		Assert.assertEquals(0, page.getTotalElements());
		Assert.assertEquals(0, page.getTotalPages());

		page = orderService.findPageWithNewTrans(order, pageable);
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(3, page.getTotalElements());
		Assert.assertFalse(page.hasPreviousPage());
		Assert.assertTrue(page.isFirstPage());
		Assert.assertTrue(page.hasNextPage());
		Assert.assertFalse(page.isLastPage());
		Assert.assertEquals(2, page.getContent().size());
		Assert.assertEquals(0, page.getNumber());
		Assert.assertEquals(2, page.getNumberOfElements());

		page = orderService.findPageWithNewTrans(order, pageable);
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(3, page.getTotalElements());
		Assert.assertTrue(page.hasPreviousPage());
		Assert.assertFalse(page.isFirstPage());
		Assert.assertFalse(page.hasNextPage());
		Assert.assertTrue(page.isLastPage());
		Assert.assertEquals(1, page.getContent().size());
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getNumberOfElements());

		orderService.deleteById(1L);
		orderService.deleteById(3L);
		orderService.deleteById(5L);
	}

	@Test
	public void testFindWithFields() {
		orderService.deleteById(1L);
		
		// save
		Order order1 = new Order();
		order1.setId(1L);
		order1.setName("order_tom");
		order1.setUserId(1L);
		orderService.save(order1);

		List<Order> orders = orderService.findWithFieldsWithNewWriteTrans(1L);
		Assert.assertEquals(1, orders.size());
		Assert.assertEquals(1, orders.get(0).getId().longValue());
		Assert.assertEquals("order_tom", orders.get(0).getName());
		Assert.assertNull(orders.get(0).getUserId());

		orderService.deleteById(1L);
	}
}
