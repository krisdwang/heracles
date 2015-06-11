package heracles.data.mybatis.dao.impl;

import heracles.data.common.annotation.TableSharding;
import heracles.data.common.util.Utils;
import heracles.data.mybatis.dao.UserDao;
import heracles.data.mybatis.entity.User;
import heracles.data.mybatis.entity.UserCriteria;
import heracles.data.mybatis.entity.UserCriteria.Criteria;
import heracles.data.mybatis.repository.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

	@Resource(name = "userMapper")
	private UserMapper userMapper;

	@Override
	@TableSharding(strategy = "user", key = "#id")
	public int deleteByPrimaryKey(Long id) {
		Assert.notNull(id);
		return userMapper.deleteByPrimaryKey(id);
	}

	@Override
	@TableSharding(strategy = "user", key = "#user.id")
	public int insert(User user) {
		Assert.notNull(user);
		return userMapper.insert(user);
	}

	@Override
	@TableSharding(strategy = "user", key = "#user.id")
	public int insertSelective(User user) {
		Assert.notNull(user);
		return userMapper.insertSelective(user);
	}

	@Override
	@TableSharding(strategy = "user", key = "#id")
	public User selectByPrimaryKey(Long id) {
		Assert.notNull(id);
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	@TableSharding(strategy = "user", key = "#user.id")
	public int updateByPrimaryKeySelective(User user) {
		Assert.notNull(user);
		return userMapper.updateByPrimaryKeySelective(user);
	}

	@Override
	@TableSharding(strategy = "user", key = "#user.id")
	public int updateByPrimaryKey(User user) {
		Assert.notNull(user);
		return userMapper.updateByPrimaryKey(user);
	}

	@Override
	@TableSharding(strategy = "user", key = "#user.id")
	public int getCount(User user) {
		Assert.notNull(user);

		UserCriteria userCriteria = new UserCriteria();
		Criteria criteria = userCriteria.createCriteria();
		criteria.andNameEqualTo(user.getName());

		return userMapper.countByCriteria(userCriteria);
	}

	@Override
	@TableSharding(strategy = "user", key = "#user.id")
	public List<User> getPage(User user, Pageable pageable) {
		Assert.notNull(user);

		UserCriteria userCriteria = new UserCriteria();
		userCriteria.setOrderByClause(Utils.getOrderBy(pageable.getSort()));
		userCriteria.setPageNum(pageable.getPageNumber());
		userCriteria.setPageSize(pageable.getPageSize());
		Criteria criteria = userCriteria.createCriteria();
		criteria.andNameEqualTo(user.getName());

		return userMapper.selectByCriteria(userCriteria);
	}

	@Override
	@TableSharding(strategy = "user", key = "#ids[0]")
	public List<User> selectByPrimaryKeys(List<Long> ids) {
		Assert.notEmpty(ids);

		UserCriteria userCriteria = new UserCriteria();
		Criteria criteria = userCriteria.createCriteria();
		criteria.andIdIn(ids);

		return userMapper.selectByCriteria(userCriteria);
	}

	@Override
	@TableSharding(strategy = "user", key = "@shardingKey.getKey()")
	public List<User> selectByPrimaryKeys(Set<Long> ids) {
		Assert.notEmpty(ids);

		UserCriteria userCriteria = new UserCriteria();
		Criteria criteria = userCriteria.createCriteria();
		criteria.andIdIn(new ArrayList<Long>(ids));

		return userMapper.selectByCriteria(userCriteria);
	}
}