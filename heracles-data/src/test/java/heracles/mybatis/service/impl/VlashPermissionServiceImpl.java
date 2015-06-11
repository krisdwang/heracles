package heracles.mybatis.service.impl;

import heracles.core.beans.mapping.BeanMapper;
import heracles.data.common.annotation.ReadWrite;
import heracles.data.common.annotation.RepositorySharding;
import heracles.data.common.util.ReadWriteType;
import heracles.data.mybatis.entity.VlashPermission;
import heracles.data.mybatis.repository.VlashPermissionRepository;
import heracles.data.mybatis.service.VlashPermissionModel;
import heracles.data.mybatis.service.VlashPermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("vlashPermissionService")
public class VlashPermissionServiceImpl implements VlashPermissionService {

	@Autowired
	private BeanMapper beanMapper;

	@Autowired
	private VlashPermissionRepository vlashPermissionRepo;

	@Transactional
	@RepositorySharding(strategy="vlash", key="#vlashPermissionModel.id")
	@Override
	public int create(VlashPermissionModel vlashPermissionModel) {
		return vlashPermissionRepo.insert(beanMapper.map(vlashPermissionModel, VlashPermission.class));
	}

	@Transactional
	@Override
	public int createSelective(VlashPermissionModel vlashPermissionModel) {
		return vlashPermissionRepo.insertSelective(beanMapper.map(vlashPermissionModel, VlashPermission.class));
	}

	@Transactional
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return vlashPermissionRepo.deleteByPrimaryKey(id);
	}

	@Transactional
	@RepositorySharding(strategy="vlash", key="#id")
	@ReadWrite(type=ReadWriteType.READ)
	@Override
	public VlashPermissionModel findByPrimaryKey(Integer id) {
		VlashPermission vlashPermission = vlashPermissionRepo.selectByPrimaryKey(id);
		return beanMapper.map(vlashPermission, VlashPermissionModel.class);
	}

	@Transactional(readOnly = true)
	@Override
	public int selectCount(VlashPermissionModel vlashPermissionModel) {
		return vlashPermissionRepo.selectCount(beanMapper.map(vlashPermissionModel, VlashPermission.class));
	}

	@Transactional
	@Override
	public int updateByPrimaryKey(VlashPermissionModel vlashPermissionModel) {
		return vlashPermissionRepo.updateByPrimaryKey(beanMapper.map(vlashPermissionModel, VlashPermission.class));
	}
	
	@Transactional
	@Override
	public int updateByPrimaryKeySelective(VlashPermissionModel vlashPermissionModel) {
		return vlashPermissionRepo.updateByPrimaryKeySelective(beanMapper.map(vlashPermissionModel, VlashPermission.class));
	}

}
