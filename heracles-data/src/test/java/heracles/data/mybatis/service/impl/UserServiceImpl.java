package heracles.data.mybatis.service.impl;

import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.annotation.RepositorySharding;
import heracles.data.common.util.ReadWriteType;
import heracles.data.mybatis.dao.UserDao;
import heracles.data.mybatis.entity.User;
import heracles.data.mybatis.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource(name = "userDao")
	private UserDao userDao;

	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void save(User user) {
		userDao.insert(user);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteById(Long id) {
		userDao.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#map[id]")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void update(Map<String, Object> map) {
		User user = new User();
		user.setId((Long) map.get("id"));
		user.setName(String.valueOf(map.get("name")));
		userDao.updateByPrimaryKey(user);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "user", key = "#ids[0]")
	@ReadWrite(type = ReadWriteType.READ)
	public List<User> findByIds(List<Long> ids) {
		return userDao.selectByPrimaryKeys(ids);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "user", key = "#ids[0]")
	@ReadWrite(type = ReadWriteType.READ)
	public List<User> findByIds(Set<Long> ids) {
		return userDao.selectByPrimaryKeys(ids);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "@shardingKey.getKey()")
	@ReadWrite(type = ReadWriteType.WRITE)
	public List<User> findByIdsWithNewTrans(Set<Long> ids) {
		return userDao.selectByPrimaryKeys(ids);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#id")
	@ReadWrite(type = ReadWriteType.READ)
	public User findById(Long id) {
		return userDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.READ)
	public Page<User> findPage(User user, Pageable pageable) {
		int count = userDao.getCount(user);
		List<User> list = userDao.getPage(user, pageable);

		return new PageImpl<User>(list, pageable, count);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public Page<User> findPageWithNewTrans(User user, Pageable pageable) {
		int count = userDao.getCount(user);
		List<User> list = userDao.getPage(user, pageable);

		return new PageImpl<User>(list, pageable, count);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#user.id")
	public void update(User user) {
		userDao.updateByPrimaryKey(user);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "#id")
	@ReadWrite(type = ReadWriteType.READ)
	public User findByIdWithNewReadTrans(Long id) {
		return userDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public User findByIdWithNewWriteTrans(Long id) {
		return userDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void saveWithNewTrans(User user) {
		userDao.insert(user);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteByIdWithNewTrans(Long id) {
		userDao.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void updateWithNewTrans(User user) {
		userDao.updateByPrimaryKey(user);
	}
}
