package heracles.mybatis.service.impl;

import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.annotation.RepositorySharding;
import heracles.data.common.util.ReadWriteType;
import heracles.data.mybatis.dao.UserDao;
import heracles.data.mybatis.entity.User;
import heracles.data.mybatis.service.SystemService;
import heracles.data.mybatis.service.UserService;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("systemService")
public class SystemServiceImpl implements SystemService {

	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "userDao")
	private UserDao userDao;

	/**
	 * 方法中所有操作都在写库上执行，包括读操作
	 */
	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public User save(User user) {
		userService.save(user);
		User u = userService.findById(user.getId());
		userService.deleteById(u.getId());
		userDao.deleteByPrimaryKey(user.getId());
		return u;
	}

	/**
	 * 理论上方法中所有操作都在写库上执行，通过配置findByIdWithNewTrans为PROPAGATION_REQUIRES_NEW，实现findByIdWithNewTrans在读库上执行
	 */
	@Override
	@Transactional
	@RepositorySharding(strategy = "user", key = "#user.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public User saveWithNewReadTrans(User user) {
		userService.save(user);
		// User u = userService.findByIdWithNewReadTrans(user.getId());
		User u = null;
		userService.deleteById(user.getId());
		return u;
	}

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.WRITE)
	public void saveWithNewWriteTrans(User user1, User user2) {
		userService.save(user1);
		// userService.saveWithNewTrans(user2);
		// userService.deleteByIdWithNewTrans(user2.getId());
		userService.deleteById(user1.getId());
	}

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.WRITE)
	public void saveWithNewTransException(User user1, User user2) {
		userService.save(user1);
		try {
			userService.saveWithNewTrans(user2);
		}
		catch (Throwable e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.READ)
	public User findByIdWithNewWriteTrans(Long id) {
		return userService.findByIdWithNewWriteTrans(id);
	}

	@Override
	@Transactional
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteById(Long id) {
		userService.deleteById(id);
	}
}
