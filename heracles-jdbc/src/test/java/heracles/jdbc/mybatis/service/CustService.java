package heracles.jdbc.mybatis.service;

import heracles.jdbc.mybatis.entity.Cust;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustService {
	void insert(Cust cust);

	void deleteById(Long id);

	void update(Cust cust);

	Cust selectById(Long id);

	List<Cust> selectByIds(List<Long> ids);

	List<Cust> selectBy(Cust cust);

	Cust selectByConstant();

	Cust selectByJoin();

	int selectCount(List<Long> ids);

	int selectMax(List<Long> ids);

	int selectMin(List<Long> ids);

	BigDecimal selectAvg(List<Long> ids);

	int selectSum(List<Long> ids);

	Map<String, Object> selectFuns(List<Long> ids);
}
