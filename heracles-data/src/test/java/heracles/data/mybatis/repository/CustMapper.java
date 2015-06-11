package heracles.data.mybatis.repository;

import heracles.data.mybatis.entity.Cust;
import heracles.data.mybatis.entity.CustCriteria;
import heracles.data.mybatis.mapper.GenericMapper;

import org.springframework.stereotype.Repository;

@Repository
public interface CustMapper extends GenericMapper<Cust, CustCriteria, Long> {
}