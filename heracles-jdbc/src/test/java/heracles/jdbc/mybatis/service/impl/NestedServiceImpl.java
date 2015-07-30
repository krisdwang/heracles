package heracles.jdbc.mybatis.service.impl;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.entity.Order;
import heracles.jdbc.mybatis.service.CustService;
import heracles.jdbc.mybatis.service.NestedService;
import heracles.jdbc.mybatis.service.OrderService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Throwable.class)
@Service("nestedService")
public class NestedServiceImpl implements NestedService {

	@Resource(name = "custService")
	private CustService custService;
	@Resource(name = "orderService")
	private OrderService orderService;

	@Override
	public void commit() {
		Cust cust1 = new Cust();
		cust1.setId(1001L);
		cust1.setName("cust1001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1001L);
		Assert.assertEquals("cust1001", cust2.getName());

		cust2.setName("cust1011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(1001L);
		Assert.assertEquals("cust1011", cust3.getName());

		custService.deleteById(1001L);

		Cust cust4 = custService.selectById(1001L);
		Assert.assertNull(cust4);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void noTrans() {
		Cust cust1 = new Cust();
		cust1.setId(1001L);
		cust1.setName("cust1001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1001L);
		Assert.assertEquals("cust1001", cust2.getName());

		cust2.setName("cust1011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(1001L);
		Assert.assertEquals("cust1011", cust3.getName());

		custService.deleteById(1001L);

		Cust cust4 = custService.selectById(1001L);
		Assert.assertNull(cust4);
	}

	@Override
	@Transactional(readOnly = true)
	public void readOnly() {
		Cust cust1 = new Cust();
		cust1.setId(1001L);
		cust1.setName("cust1001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1001L);
		Assert.assertEquals("cust1001", cust2.getName());

		cust2.setName("cust1011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(1001L);
		Assert.assertEquals("cust1011", cust3.getName());

		custService.deleteById(1001L);

		Cust cust4 = custService.selectById(1001L);
		Assert.assertNull(cust4);
	}

	@Override
	public void rollback() {
		Cust cust1 = new Cust();
		cust1.setId(1001L);
		cust1.setName("cust1001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1001L);
		Assert.assertEquals("cust1001", cust2.getName());

		cust2.setName("cust1011****************************************************************");
		custService.update(cust2);

		Cust cust3 = custService.selectById(1001L);
		Assert.assertEquals("cust1011", cust3.getName());

		custService.deleteById(1001L);

		Cust cust4 = custService.selectById(1001L);
		Assert.assertNull(cust4);
	}

	@Override
	public void twoTableCommit() {
		Order order1 = new Order();
		order1.setId(1001L);
		order1.setName("order1001");
		orderService.insert(order1);

		Cust cust1 = new Cust();
		cust1.setId(1003L);
		cust1.setName("cust1003");
		custService.insert(cust1);

		Order order2 = orderService.selectById(1001L);
		Assert.assertEquals("order1001", order2.getName());

		Cust cust2 = custService.selectById(1003L);
		Assert.assertEquals("cust1003", cust2.getName());

		order2.setName("order1011");
		orderService.update(order2);

		cust2.setName("cust1013");
		custService.update(cust2);

		Order order3 = orderService.selectById(1001L);
		Assert.assertEquals("order1011", order3.getName());

		Cust cust3 = custService.selectById(1003L);
		Assert.assertEquals("cust1013", cust3.getName());

		orderService.deleteById(1001L);

		Order order4 = orderService.selectById(1001L);
		Assert.assertNull(order4);

		custService.deleteById(1003L);

		Cust cust4 = custService.selectById(1003L);
		Assert.assertNull(cust4);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void twoTableNoTrans() {
		Order order1 = new Order();
		order1.setId(1001L);
		order1.setName("order1001");
		orderService.insert(order1);

		Cust cust1 = new Cust();
		cust1.setId(1003L);
		cust1.setName("cust1003");
		custService.insert(cust1);

		Order order2 = orderService.selectById(1001L);
		Assert.assertEquals("order1001", order2.getName());

		Cust cust2 = custService.selectById(1003L);
		Assert.assertEquals("cust1003", cust2.getName());

		order2.setName("order1011");
		orderService.update(order2);

		cust2.setName("cust1013");
		custService.update(cust2);

		Order order3 = orderService.selectById(1001L);
		Assert.assertEquals("order1011", order3.getName());

		Cust cust3 = custService.selectById(1003L);
		Assert.assertEquals("cust1013", cust3.getName());

		orderService.deleteById(1001L);

		Order order4 = orderService.selectById(1001L);
		Assert.assertNull(order4);

		custService.deleteById(1003L);

		Cust cust4 = custService.selectById(1003L);
		Assert.assertNull(cust4);
	}

	@Override
	public void twoTableRollback() {
		Order order1 = new Order();
		order1.setId(1001L);
		order1.setName("order1001");
		orderService.insert(order1);

		Cust cust1 = new Cust();
		cust1.setId(1003L);
		cust1.setName("cust1003");
		custService.insert(cust1);

		Order order2 = orderService.selectById(1001L);
		Assert.assertEquals("order1001", order2.getName());

		Cust cust2 = custService.selectById(1003L);
		Assert.assertEquals("cust1003", cust2.getName());

		order2.setName("order1011");
		orderService.update(order2);

		cust2.setName("cust1013***********************************************************************");
		custService.update(cust2);

		Order order3 = orderService.selectById(1001L);
		Assert.assertEquals("order1011", order3.getName());

		Cust cust3 = custService.selectById(1003L);
		Assert.assertEquals("cust1013", cust3.getName());

		orderService.deleteById(1001L);

		Order order4 = orderService.selectById(1001L);
		Assert.assertNull(order4);

		custService.deleteById(1003L);

		Cust cust4 = custService.selectById(1003L);
		Assert.assertNull(cust4);
	}

	@Override
	public void twoTableRequiresNew() {
		Cust cust1 = new Cust();
		cust1.setId(1003L);
		cust1.setName("cust1003");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1003L);
		Assert.assertEquals("cust1003", cust2.getName());

		Order order1 = new Order();
		order1.setId(1001L);
		order1.setName("order1001***********************************************************************");
		try {
			orderService.requiresNewInsert(order1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Order order2 = orderService.selectById(1001L);
		// Assert.assertEquals("order1001", order2.getName());
		//
		// order2.setName("order1011");
		// orderService.update(order2);

		cust2.setName("cust1013");
		custService.update(cust2);

		// Order order3 = orderService.selectById(1001L);
		// Assert.assertEquals("order1011", order3.getName());

		Cust cust3 = custService.selectById(1003L);
		Assert.assertEquals("cust1013", cust3.getName());

		// orderService.deleteById(1001L);

		// Order order4 = orderService.selectById(1001L);
		// Assert.assertNull(order4);

		custService.deleteById(1003L);

		Cust cust4 = custService.selectById(1003L);
		Assert.assertNull(cust4);
	}

	@Override
	public void twoTableNested() {
		Cust cust1 = new Cust();
		cust1.setId(1003L);
		cust1.setName("cust1003");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1003L);
		Assert.assertEquals("cust1003", cust2.getName());

		Order order1 = new Order();
		order1.setId(1001L);
		order1.setName("order1001***********************************************************************");
		try {
			orderService.nestedInsert(order1);
		} catch (Exception e) {
			order1.setName("order1001");
			orderService.insert(order1);
		}

		Order order2 = orderService.selectById(1001L);
		Assert.assertEquals("order1001", order2.getName());

		order2.setName("order1011");
		orderService.update(order2);

		cust2.setName("cust1013");
		custService.update(cust2);

		Order order3 = orderService.selectById(1001L);
		Assert.assertEquals("order1011", order3.getName());

		Cust cust3 = custService.selectById(1003L);
		Assert.assertEquals("cust1013", cust3.getName());

		orderService.deleteById(1001L);

		Order order4 = orderService.selectById(1001L);
		Assert.assertNull(order4);

		custService.deleteById(1003L);

		Cust cust4 = custService.selectById(1003L);
		Assert.assertNull(cust4);
	}
}
