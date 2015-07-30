package heracles.jdbc.mybatis.service.impl;

import heracles.jdbc.mybatis.entity.Order;
import heracles.jdbc.mybatis.repository.OrderMapper;
import heracles.jdbc.mybatis.service.OrderService;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Throwable.class)
@Service("orderService")
public class OrderServiceImpl implements OrderService {

	@Resource(name = "orderMapper")
	private OrderMapper orderMapper;

	@Override
	@Transactional
	public void insert(Order order) {
		orderMapper.insert(order);
	}

	@Override
	@Transactional(propagation = Propagation.NESTED)
	public void nestedInsert(Order order) {
		Order order1 = new Order();
		order1.setId(order.getId() + 1);
		order1.setName("zhuzhen");
		orderMapper.insert(order1);
		orderMapper.insert(order);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void requiresNewInsert(Order order) {
		orderMapper.insert(order);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		orderMapper.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Order selectById(Long id) {
		return orderMapper.selectById(id);
	}

	@Override
	@Transactional
	public void update(Order order) {
		orderMapper.update(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Order> selectByIds(List<Long> ids) {
		return orderMapper.selectByIds(ids);
	}

	@Override
	public List<Order> selectBy(Order order) {
		return orderMapper.selectBy(order);
	}

	@Override
	public Order selectByConstant() {
		return orderMapper.selectByConstant();
	}
}
