package heracles.core.zookeeper;

import java.util.List;

/**
 * 
 * @author kriswang
 * 
 */
public interface ZookeeperClient {

	/**
	 * 开启zk连接
	 */
	public void startClient();

	/**
	 * 释放zk连接
	 */
	public void closeClient();

	/**
	 * 客户端是否已经start
	 * 
	 * @return Boolean
	 */
	public Boolean isStarted();

	/**
	 * 更新
	 * 
	 * @param path
	 * @param content
	 * @throws Exception
	 */
	public void updZnode(String path, String content) throws Exception;
	
	public void updZnode(String path, byte[] content) throws Exception;

	/**
	 * 删除
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void delZnode(String path) throws Exception;

	/**
	 * 新增
	 * 
	 * @param path
	 * @param content
	 * @throws Exception
	 */
	public void createZnode(String path, String content) throws Exception;
	
	public void createZnode(String path, byte[] content) throws Exception;

	/**
	 * 新增或修改
	 * 
	 * @param path
	 * @param content
	 * @throws Exception
	 */
	public void createOrUpdZnode(String path, String content) throws Exception;
	
	public void createOrUpdZnode(String path, byte[] content) throws Exception;

	/**
	 * 查询子节点
	 * 
	 * @param path
	 * @return List<String>
	 * @throws Exception
	 */
	public List<String> findChildren(String path) throws Exception;

	/**
	 * 查询节点内容
	 * 
	 * @param path
	 * @return String
	 * @throws Exception
	 */
	public String findChildData(String path) throws Exception;
	
	public byte[] findChildByte(String path) throws Exception;

	/**
	 * 查询节点内容
	 * 
	 * @param path
	 * @return Boolean
	 * @throws Exception
	 */
	public Boolean isExists(String path) throws Exception;

	/**
	 * 查询节点内容,并watch
	 * 
	 * @param path
	 * @return String
	 * @throws Exception
	 */
	public String findChildDataAndWatch(String path, List<PropertyChangedHandler> handlers, String file) throws Exception;

	/**
	 * 查询节点内容,并watch
	 * 
	 * @param path
	 * @return String
	 * @throws Exception
	 */
	public String watch(String path, List<PropertyChangedHandler> handlers, String file) throws Exception;
	
	public void watchNode(String path, List<PropertyChangedHandler> handlers, String file) throws Exception;

	/**
	 * 查看当前连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCurrenConnectionStr() throws Exception;
}
