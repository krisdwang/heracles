package heracles.jdbc.mybatis.service.impl;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.repository.CustMapper;
import heracles.jdbc.mybatis.service.CustService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Throwable.class)
@Service("custService")
public class CustServiceImpl implements CustService {

	@Resource(name = "custMapper")
	private CustMapper custMapper;

	@Override
	@Transactional
	public void insert(Cust cust) {
		custMapper.insert(cust);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		custMapper.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Cust selectById(Long id) {
		return custMapper.selectById(id);
	}

	@Override
	@Transactional
	public void update(Cust cust) {
		custMapper.update(cust);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cust> selectByIds(List<Long> ids) {
		return custMapper.selectByIds(ids);
	}

	@Override
	public List<Cust> selectBy(Cust cust) {
		return custMapper.selectBy(cust);
	}

	@Override
	public Cust selectByConstant() {
		return custMapper.selectByConstant();
	}

	@Override
	public Cust selectByJoin() {
		return custMapper.selectByJoin();
	}

	@Override
	public int selectCount(List<Long> ids) {
		return custMapper.selectCount(ids);
	}

	@Override
	public int selectMax(List<Long> ids) {
		return custMapper.selectMax(ids);
	}

	@Override
	public int selectMin(List<Long> ids) {
		return custMapper.selectMin(ids);
	}

	@Override
	public BigDecimal selectAvg(List<Long> ids) {
		return custMapper.selectAvg(ids);
	}

	@Override
	public int selectSum(List<Long> ids) {
		return custMapper.selectSum(ids);
	}

	@Override
	public Map<String, Object> selectFuns(List<Long> ids) {
		return custMapper.selectFuns(ids);
	}
}
