package heracles.data.mybatis.repository;

import java.io.Serializable;

/**
 * Generic Respository Interface
 * 
 * @author Heracles Code Generator
 */
public interface GenericRepository<T, PK extends Serializable> {

	public abstract int deleteByPrimaryKey(PK id);

	public abstract int insert(T entity);

	public abstract int insertSelective(T entity);

	public abstract T selectByPrimaryKey(PK id);

	public abstract int updateByPrimaryKeySelective(T entity);

	public abstract int updateByPrimaryKey(T entity);
}