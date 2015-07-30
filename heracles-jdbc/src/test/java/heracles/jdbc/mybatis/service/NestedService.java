package heracles.jdbc.mybatis.service;

public interface NestedService {
	void commit();

	void noTrans();

	void readOnly();

	void rollback();

	void twoTableCommit();

	void twoTableNoTrans();

	void twoTableRollback();

	void twoTableRequiresNew();

	void twoTableNested();
}
