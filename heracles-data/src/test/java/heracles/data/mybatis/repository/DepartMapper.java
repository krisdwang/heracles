package heracles.data.mybatis.repository;

import heracles.data.common.annotation.TableSharding;
import heracles.data.mybatis.entity.Depart;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartMapper {
	@TableSharding(strategy = "depart", key = "#id")
	int deleteByPrimaryKey(@Param("id") Long id);

	@TableSharding(strategy = "depart", key = "#depart.id")
	int insert(@Param("depart") Depart depart);

	@TableSharding(strategy = "depart", key = "#depart.id")
	int insertSelective(@Param("depart") Depart depart);

	@TableSharding(strategy = "depart", key = "#id")
	Depart selectByPrimaryKey(@Param("id") Long id);

	@TableSharding(strategy = "depart", key = "#depart.id")
	int updateByPrimaryKeySelective(@Param("depart") Depart depart);

	@TableSharding(strategy = "depart", key = "#depart.id")
	int updateByPrimaryKey(@Param("depart") Depart depart);

	@TableSharding(strategy = "depart", key = "#depart.id")
	int selectCount(@Param("depart") Depart depart);

	@TableSharding(strategy = "depart", key = "#depart.id")
	List<Depart> selectPage(@Param("depart") Depart depart, @Param("pageable") Pageable pageable);
}