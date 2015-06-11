package heracles.data.mybatis.dao.impl;

import heracles.data.common.util.Utils;
import heracles.data.mybatis.dao.CustDao;
import heracles.data.mybatis.entity.Cust;
import heracles.data.mybatis.entity.CustCriteria;
import heracles.data.mybatis.entity.CustCriteria.Criteria;
import heracles.data.mybatis.repository.CustMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("custDao")
public class CustDaoImpl implements CustDao {

	@Resource(name = "custMapper")
	private CustMapper custMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		Assert.notNull(id);
		return custMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Cust cust) {
		Assert.notNull(cust);
		return custMapper.insert(cust);
	}

	@Override
	public int insertSelective(Cust cust) {
		Assert.notNull(cust);
		return custMapper.insertSelective(cust);
	}

	@Override
	public Cust selectByPrimaryKey(Long id) {
		Assert.notNull(id);
		return custMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Cust cust) {
		Assert.notNull(cust);
		return custMapper.updateByPrimaryKeySelective(cust);
	}

	@Override
	public int updateByPrimaryKey(Cust cust) {
		Assert.notNull(cust);
		return custMapper.updateByPrimaryKey(cust);
	}

	@Override
	public int getCount(Cust cust) {
		Assert.notNull(cust);

		CustCriteria custCriteria = new CustCriteria();
		Criteria criteria = custCriteria.createCriteria();
		criteria.andNameEqualTo(cust.getName());

		return custMapper.countByCriteria(custCriteria);
	}

	@Override
	public List<Cust> getPage(Cust cust, Pageable pageable) {
		Assert.notNull(cust);

		CustCriteria custCriteria = new CustCriteria();
		custCriteria.setOrderByClause(Utils.getOrderBy(pageable.getSort()));
		custCriteria.setPageNum(pageable.getPageNumber());
		custCriteria.setPageSize(pageable.getPageSize());
		Criteria criteria = custCriteria.createCriteria();
		criteria.andNameEqualTo(cust.getName());

		return custMapper.selectByCriteria(custCriteria);
	}
}