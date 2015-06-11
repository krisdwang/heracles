package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
	void save(Order order);

	void deleteById(Long id);

	void update(Order order);

	Order findById(Long id);

	Page<Order> findPage(Order order, Pageable pageable);

	void saveWithNewTrans(Order order);

	void deleteByIdWithNewTrans(Long id);

	void updateWithNewTrans(Order order);

	Order findByIdWithNewReadTrans(Long id);

	Order findByIdWithNewWriteTrans(Long id);

	Page<Order> findPageWithNewTrans(Order order, Pageable pageable);

	List<Order> findWithFieldsWithNewWriteTrans(Long id);
}
