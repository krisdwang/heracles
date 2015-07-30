package heracles.jdbc.mybatis.repository;

import heracles.jdbc.mybatis.entity.Order;

import java.util.List;

public interface OrderMapper {
	public List<Order> selectByIds(List<Long> ids);

	public List<Order> selectBy(Order order);

	public int deleteById(Long id);

	public int insert(Order order);

	// public int insertA(Order order);

	public Order selectById(Long id);

	public Order selectByConstant();

	public int update(Order order);
}