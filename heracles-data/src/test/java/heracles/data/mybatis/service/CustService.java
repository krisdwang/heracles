package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Cust;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustService {
	void save(Cust cust);

	void deleteById(Long id);

	void update(Cust cust);

	Cust findById(Long id);

	Page<Cust> findPage(Cust cust, Pageable pageable);

	void saveWithNewTrans(Cust cust);

	void deleteByIdWithNewTrans(Long id);

	void updateWithNewTrans(Cust cust);

	Cust findByIdWithNewReadTrans(Long id);

	Cust findByIdWithNewWriteTrans(Long id);

	Page<Cust> findPageWithNewTrans(Cust cust, Pageable pageable);
}
