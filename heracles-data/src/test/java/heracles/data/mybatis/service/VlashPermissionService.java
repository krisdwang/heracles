package heracles.data.mybatis.service;


public interface VlashPermissionService{
	
	public int create(VlashPermissionModel vlashPermissionModel);
	
	public int createSelective(VlashPermissionModel vlashPermissionModel);
	
	public VlashPermissionModel findByPrimaryKey(Integer id);
	
	public int updateByPrimaryKey(VlashPermissionModel vlashPermissionModel);
	
	public int updateByPrimaryKeySelective(VlashPermissionModel vlashPermissionModel);
	
	public int deleteByPrimaryKey(Integer id);
	
	public int selectCount(VlashPermissionModel vlashPermissionModel);
	
}