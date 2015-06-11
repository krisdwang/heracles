package heracles.data.mybatis.dao;

import heracles.data.mybatis.entity.Cust;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface CustDao {
	public abstract int deleteByPrimaryKey(Long id);

	public abstract int insert(Cust cust);

	public abstract int insertSelective(Cust cust);

	public abstract Cust selectByPrimaryKey(Long id);

	public abstract int updateByPrimaryKeySelective(Cust cust);

	public abstract int updateByPrimaryKey(Cust cust);

	public abstract int getCount(Cust cust);

	public abstract List<Cust> getPage(Cust cust, Pageable pageable);
}