package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
	void save(User user);

	void deleteById(Long id);

	void update(User user);

	void update(Map<String, Object> map);

	User findById(Long id);

	Page<User> findPage(User user, Pageable pageable);

	void saveWithNewTrans(User user);

	void deleteByIdWithNewTrans(Long id);

	void updateWithNewTrans(User user);

	User findByIdWithNewReadTrans(Long id);

	User findByIdWithNewWriteTrans(Long id);

	List<User> findByIds(List<Long> ids);

	List<User> findByIds(Set<Long> ids);

	List<User> findByIdsWithNewTrans(Set<Long> ids);

	Page<User> findPageWithNewTrans(User user, Pageable pageable);
}
