package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.Depart;

public interface DepartService {
	void save(Depart depart);

	void deleteById(Long id);

	void update(Depart depart);

	Depart findById(Long id);
}
