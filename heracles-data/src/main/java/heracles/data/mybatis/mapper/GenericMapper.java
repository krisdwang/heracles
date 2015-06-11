package heracles.data.mybatis.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface GenericMapper<T, C, PK extends Serializable> {

	public int deleteByPrimaryKey(PK id);

	public int insert(T record);

	public int insertSelective(T record);

	public T selectByPrimaryKey(PK id);

	public int updateByPrimaryKeySelective(T record);

	public int updateByPrimaryKey(T record);

	public int countByCriteria(C example);

	public int deleteByCriteria(C example);

	public List<T> selectByCriteria(C example);

	public int updateByCriteriaSelective(@Param("record") T record, @Param("example") C example);

	public int updateByCriteria(@Param("record") T record, @Param("example") C example);
}