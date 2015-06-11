package heracles.data.mybatis.dao;

import heracles.data.mybatis.entity.User;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

public interface UserDao {

	public abstract int deleteByPrimaryKey(Long id);

	public abstract int insert(User user);

	public abstract int insertSelective(User user);

	public abstract User selectByPrimaryKey(Long id);

	public abstract List<User> selectByPrimaryKeys(List<Long> ids);

	public abstract List<User> selectByPrimaryKeys(Set<Long> ids);

	public abstract int updateByPrimaryKeySelective(User user);

	public abstract int updateByPrimaryKey(User user);

	public abstract int getCount(User user);

	public abstract List<User> getPage(User user, Pageable pageable);
}