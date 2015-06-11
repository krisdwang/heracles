package heracles.data.mybatis.repository;

import heracles.data.mybatis.entity.VlashPermission;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VlashPermissionRepository {
    int deleteByPrimaryKey(@Param("id") Integer id);

    int insert(@Param("vlashpermission") VlashPermission vlashpermission);

    int insertSelective(@Param("vlashpermission") VlashPermission vlashpermission);

    VlashPermission selectByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(@Param("vlashpermission") VlashPermission vlashpermission);

    int updateByPrimaryKey(@Param("vlashpermission") VlashPermission vlashpermission);

    int selectCount(@Param("vlashpermission") VlashPermission vlashpermission);

    List<VlashPermission> selectPage(@Param("vlashpermission") VlashPermission vlashpermission, @Param("pageable") Pageable pageable);
}