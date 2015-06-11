package heracles.data.mybatis.repository;

import heracles.data.mybatis.entity.Order;
import heracles.data.mybatis.entity.OrderCriteria;
import heracles.data.mybatis.mapper.GenericMapper;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper extends GenericMapper<Order, OrderCriteria, Long> {
}