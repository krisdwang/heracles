package heracles.mybatis.service.impl;

import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.annotation.RepositorySharding;
import heracles.data.common.util.ReadWriteType;
import heracles.data.mybatis.entity.Depart;
import heracles.data.mybatis.repository.DepartMapper;
import heracles.data.mybatis.service.DepartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Throwable.class)
@Service("departService")
public class DepartServiceImpl implements DepartService {

	@Autowired
	private DepartMapper departMapper;

	@Override
	@Transactional
	@RepositorySharding(strategy = "depart", key = "#depart.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void save(Depart depart) {
		departMapper.insert(depart);

	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "depart", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteById(Long id) {
		departMapper.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "depart", key = "#depart.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void update(Depart depart) {
		departMapper.updateByPrimaryKey(depart);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "depart", key = "#id")
	@ReadWrite(type = ReadWriteType.READ)
	public Depart findById(Long id) {
		return departMapper.selectByPrimaryKey(id);
	}

}
