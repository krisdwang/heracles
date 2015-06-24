package heracles.data.mybatis.service.impl;

import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.annotation.RepositorySharding;
import heracles.data.common.util.ReadWriteType;
import heracles.data.mybatis.dao.CustDao;
import heracles.data.mybatis.entity.Cust;
import heracles.data.mybatis.service.CustService;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Throwable.class)
@Service("custService")
public class CustServiceImpl implements CustService {

	@Resource(name = "custDao")
	private CustDao custDao;

	@Override
	@Transactional
	@RepositorySharding(strategy = "cust", key = "#cust.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void save(Cust cust) {
		custDao.insert(cust);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "cust", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteById(Long id) {
		custDao.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "cust", key = "#id")
	@ReadWrite(type = ReadWriteType.READ)
	public Cust findById(Long id) {
		return custDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "cust", key = "#cust.id")
	@ReadWrite(type = ReadWriteType.READ)
	public Page<Cust> findPage(Cust cust, Pageable pageable) {
		int count = custDao.getCount(cust);
		List<Cust> list = custDao.getPage(cust, pageable);

		return new PageImpl<Cust>(list, pageable, count);
	}

	@Override
	@Transactional
	@RepositorySharding(strategy = "cust", key = "#cust.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void update(Cust cust) {
		custDao.updateByPrimaryKey(cust);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "cust", key = "#id")
	@ReadWrite(type = ReadWriteType.READ)
	public Cust findByIdWithNewReadTrans(Long id) {
		return custDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(readOnly = true)
	@RepositorySharding(strategy = "cust", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public Cust findByIdWithNewWriteTrans(Long id) {
		return custDao.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "cust", key = "#cust.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void saveWithNewTrans(Cust cust) {
		custDao.insert(cust);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "cust", key = "#id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void deleteByIdWithNewTrans(Long id) {
		custDao.deleteByPrimaryKey(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "cust", key = "#cust.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public void updateWithNewTrans(Cust cust) {
		custDao.updateByPrimaryKey(cust);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@RepositorySharding(strategy = "cust", key = "#cust.id")
	@ReadWrite(type = ReadWriteType.WRITE)
	public Page<Cust> findPageWithNewTrans(Cust cust, Pageable pageable) {
		int count = custDao.getCount(cust);
		List<Cust> list = custDao.getPage(cust, pageable);

		return new PageImpl<Cust>(list, pageable, count);
	}
}
