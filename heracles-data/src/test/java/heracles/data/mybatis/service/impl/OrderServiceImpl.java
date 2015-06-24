package heracles.data.mybatis.service.impl;

import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.util.ReadWriteType;
import heracles.data.mybatis.dao.OrderDao;
import heracles.data.mybatis.entity.Order;
import heracles.data.mybatis.service.OrderService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("orderService")
public class OrderServiceImpl implements OrderService {

	@Resource(name = "orderDao")
	private OrderDao orderDao;

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.WRITE)
	public void save(Order order) {
		orderDao.insert(order);
	}

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteById(Long id) {
		orderDao.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.READ)
	public Order findById(Long id) {
		return orderDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true)
	@ReadWrite(type = ReadWriteType.READ)
	public Page<Order> findPage(Order order, Pageable pageable) {
		int count = orderDao.getCount(order);
		List<Order> list = orderDao.getPage(order, pageable);

		return new PageImpl<Order>(list, pageable, count);
	}

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.WRITE)
	public void update(Order order) {
		orderDao.updateByPrimaryKey(order);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.READ)
	public Order findByIdWithNewReadTrans(Long id) {
		return orderDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true)
	@ReadWrite(type = ReadWriteType.WRITE)
	public Order findByIdWithNewWriteTrans(Long id) {
		return orderDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.WRITE)
	public void saveWithNewTrans(Order order) {
		orderDao.insert(order);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteByIdWithNewTrans(Long id) {
		orderDao.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.WRITE)
	public void updateWithNewTrans(Order order) {
		orderDao.updateByPrimaryKey(order);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.WRITE)
	public Page<Order> findPageWithNewTrans(Order order, Pageable pageable) {
		int count = orderDao.getCount(order);
		List<Order> list = orderDao.getPage(order, pageable);

		return new PageImpl<Order>(list, pageable, count);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@ReadWrite(type = ReadWriteType.WRITE)
	public List<Order> findWithFieldsWithNewWriteTrans(Long id) {
		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("name");
		return orderDao.selectWithFields(id, fields);
	}
}
