package heracles.data.mybatis.service;

import heracles.core.beans.mapping.annotation.MapClass;

import java.io.Serializable;
import java.util.Date;

@MapClass("com.bocomm.zttc.jump.entity.VlashPermission")
public class VlashPermissionModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3519617199091201050L;
	private Integer id;
	private Integer parentId;
	private String name;
	private String permAction;
	private String rel;
	private Integer sort;
	private String permCode;
	private Byte status;
	private Date createtime;
	private String createname;
	private Date updatetime;
	private String updatename;
		
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return this.id;
	}
		
	public void setParentId(Integer parentId){
		this.parentId = parentId;
	}
	
	public Integer getParentId(){
		return this.parentId;
	}
		
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
		
	public void setPermAction(String permAction){
		this.permAction = permAction;
	}
	
	public String getPermAction(){
		return this.permAction;
	}
		
	public void setRel(String rel){
		this.rel = rel;
	}
	
	public String getRel(){
		return this.rel;
	}
		
	public void setSort(Integer sort){
		this.sort = sort;
	}
	
	public Integer getSort(){
		return this.sort;
	}
		
	public void setPermCode(String permCode){
		this.permCode = permCode;
	}
	
	public String getPermCode(){
		return this.permCode;
	}
		
	public void setStatus(Byte status){
		this.status = status;
	}
	
	public Byte getStatus(){
		return this.status;
	}
		
	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	
	public Date getCreatetime(){
		return this.createtime;
	}
		
	public void setCreatename(String createname){
		this.createname = createname;
	}
	
	public String getCreatename(){
		return this.createname;
	}
		
	public void setUpdatetime(Date updatetime){
		this.updatetime = updatetime;
	}
	
	public Date getUpdatetime(){
		return this.updatetime;
	}
		
	public void setUpdatename(String updatename){
		this.updatename = updatename;
	}
	
	public String getUpdatename(){
		return this.updatename;
	}
		
		
}