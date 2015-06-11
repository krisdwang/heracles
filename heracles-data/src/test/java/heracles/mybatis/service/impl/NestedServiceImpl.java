package heracles.mybatis.service.impl;

import heracles.data.common.annotation.RepositorySharding;
import heracles.data.mybatis.service.NestedService;
import heracles.data.mybatis.service.VlashPermissionModel;
import heracles.data.mybatis.service.VlashPermissionService;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("nestedService")
public class NestedServiceImpl implements NestedService {

	@Resource(name = "vlashPermissionService")
	private VlashPermissionService vlashPermissionService;
	
	@Transactional
	@RepositorySharding(strategy="vlash", key="#param")
	@Override
	public int test(int param) {
		// TODO Auto-generated method stub
		VlashPermissionModel vlashPermissionModel = new VlashPermissionModel();
		vlashPermissionModel.setId(1);
		vlashPermissionModel.setParentId(123);
		vlashPermissionModel.setName("");
		vlashPermissionModel.setPermAction("");
		vlashPermissionModel.setPermCode("");
		vlashPermissionModel.setRel("");
		vlashPermissionModel.setSort(1);
		vlashPermissionModel.setCreatename("");
		vlashPermissionModel.setUpdatename("");
		vlashPermissionModel.setStatus((byte)0);		
		vlashPermissionService.findByPrimaryKey(1);

		//System.out.println(vlashPermissionService.findByPrimaryKey(1).getParentId());

		vlashPermissionService.findByPrimaryKey(101);
		//System.out.println(vlashPermissionService.findByPrimaryKey(101).getParentId());
		
		return 0;
	}

}
