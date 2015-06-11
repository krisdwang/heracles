package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.User;

public interface SystemService {
	public User save(User user);

	public User saveWithNewReadTrans(User user);

	public void saveWithNewWriteTrans(User user1, User user2);

	public void saveWithNewTransException(User user1, User user2);

	public User findByIdWithNewWriteTrans(Long id);

	public void deleteById(Long id);
}
