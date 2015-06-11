package heracles.data.mybatis.dao;

import heracles.data.common.annotation.TableSharding;
import heracles.data.mybatis.entity.Order;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface OrderDao {
	public abstract int deleteByPrimaryKey(Long id);

	public abstract int insert(Order order);

	public abstract int insertSelective(Order order);

	@TableSharding(strategy = "order", key = "#id")
	public abstract Order selectByPrimaryKey(@Param("id")Long id);

	public abstract int updateByPrimaryKeySelective(Order order);

	public abstract int updateByPrimaryKey(Order order);

	public abstract int getCount(Order order);

	public abstract List<Order> getPage(Order order, Pageable pageable);

	public abstract List<Order> selectWithFields(Long id, List<String> fields);
}