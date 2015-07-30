package heracles.jdbc.mybatis.repository;

import heracles.jdbc.mybatis.entity.Cust;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustMapper {
	List<Cust> selectByIds(List<Long> ids);

	List<Cust> selectBy(Cust cust);

	int deleteById(Long id);

	int insert(Cust cust);

	// int insertA(Cust cust);

	Cust selectById(Long id);

	Cust selectByConstant();

	int update(Cust cust);

	Cust selectByJoin();

	int selectCount(List<Long> ids);

	int selectMax(List<Long> ids);

	int selectMin(List<Long> ids);

	BigDecimal selectAvg(List<Long> ids);

	int selectSum(List<Long> ids);

	Map<String, Object> selectFuns(List<Long> ids);
}