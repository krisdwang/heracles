package heracles.data.mybatis.dao.impl;

import heracles.data.common.annotation.TableSharding;
import heracles.data.common.util.Utils;
import heracles.data.mybatis.dao.OrderDao;
import heracles.data.mybatis.entity.Order;
import heracles.data.mybatis.entity.OrderCriteria;
import heracles.data.mybatis.entity.OrderCriteria.Criteria;
import heracles.data.mybatis.repository.OrderMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("orderDao")
public class OrderDaoImpl implements OrderDao {

	@Resource(name = "orderMapper")
	private OrderMapper orderMapper;

	@Override
	@TableSharding(strategy = "order", key = "#id")
	public int deleteByPrimaryKey(Long id) {
		Assert.notNull(id);
		return orderMapper.deleteByPrimaryKey(id);
	}

	@Override
	@TableSharding(strategy = "order", key = "#order.id")
	public int insert(Order order) {
		Assert.notNull(order);
		return orderMapper.insert(order);
	}

	@Override
	@TableSharding(strategy = "order", key = "#order.id")
	public int insertSelective(Order order) {
		Assert.notNull(order);
		return orderMapper.insertSelective(order);
	}

	@Override
	@TableSharding(strategy = "order", key = "#id")
	public Order selectByPrimaryKey(Long id) {
		Assert.notNull(id);
		return orderMapper.selectByPrimaryKey(id);
	}

	@Override
	@TableSharding(strategy = "order", key = "#order.id")
	public int updateByPrimaryKeySelective(Order order) {
		Assert.notNull(order);
		return orderMapper.updateByPrimaryKeySelective(order);
	}

	@Override
	@TableSharding(strategy = "order", key = "#order.id")
	public int updateByPrimaryKey(Order order) {
		Assert.notNull(order);
		return orderMapper.updateByPrimaryKey(order);
	}

	@Override
	@TableSharding(strategy = "order", key = "#order.id")
	public int getCount(Order order) {
		Assert.notNull(order);

		OrderCriteria orderCriteria = new OrderCriteria();
		Criteria criteria = orderCriteria.createCriteria();
		criteria.andNameEqualTo(order.getName());

		return orderMapper.countByCriteria(orderCriteria);
	}

	@Override
	@TableSharding(strategy = "order", key = "#order.id")
	public List<Order> getPage(Order order, Pageable pageable) {
		Assert.notNull(order);

		OrderCriteria orderCriteria = new OrderCriteria();
		orderCriteria.setOrderByClause(Utils.getOrderBy(pageable.getSort()));
		orderCriteria.setPageNum(pageable.getPageNumber());
		orderCriteria.setPageSize(pageable.getPageSize());
		Criteria criteria = orderCriteria.createCriteria();
		criteria.andNameEqualTo(order.getName());

		return orderMapper.selectByCriteria(orderCriteria);
	}

	@Override
	@TableSharding(strategy = "order", key = "#id")
	public List<Order> selectWithFields(Long id, List<String> fields) {
		Assert.notNull(id);
		Assert.notEmpty(fields);

		OrderCriteria orderCriteria = new OrderCriteria();
		orderCriteria.addSelectFields(fields);
		Criteria criteria = orderCriteria.createCriteria();
		criteria.andIdEqualTo(id);

		return orderMapper.selectByCriteria(orderCriteria);
	}
}