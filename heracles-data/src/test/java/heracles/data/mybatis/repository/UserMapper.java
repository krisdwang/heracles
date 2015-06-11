package heracles.data.mybatis.repository;

import heracles.data.mybatis.entity.User;
import heracles.data.mybatis.entity.UserCriteria;
import heracles.data.mybatis.mapper.GenericMapper;

import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends GenericMapper<User, UserCriteria, Long> {
}