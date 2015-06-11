package heracles.unit;

/**
 * heracles-test专用Constants
 * 
 * @author kriswang
 *
 */
public class TestConstants {

	/**
	 * classpath*:
	 */
	public static final String CLASS_PATH_ALL = "classpath*:";
	/**
	 * matrix专用zk路径
	 */
	public static final String BASE_MATRIX_PATH = "/resource/RDBMS/matrix/";
	/**
	 * heracles.cfgcenter.partition
	 */
	public static final String BASE_CFGCENTER_PARTITION_KEY = "heracles.cfgcenter.partition";
	/**
	 * heracles.cfgcenter.partition
	 */
	public static final String BASE_CFGCENTER_PARTITION_VALUE = "default";
	/**
	 * classpath:
	 */
	public static final String CLASS_PATH = "classpath:";
	/**
	 * h2
	 */
	public static final String H2_DATABASE = "h2";
	/**
	 * MySQL
	 */
	public static final String MYSQL_DATABASE = "mysql";
	/**
	 * 问号
	 */
	public static final String QUESTION_MARK = "?";
	/**
	 * 分号
	 */
	public static final String SEMICOLON = ";";
	/**
	 * utf-8
	 */
	public static final String UTF_8 = "utf-8";
	/**
	 * 解析对象
	 */
	public static final String GROUP_CONF = "group:";
	/**
	 * 解析对象
	 */
	public static final String ATOM_CONF = "atom:";
	/**
	 * 元素分隔符
	 */
	public static final String SPLIT_ELEMENT = ";";
	/**
	 * 值分隔符
	 */
	public static final String SPLIT_VALUE = ",";
	/**
	 * 通配符
	 */
	public static final String ALL_SYMBOL = "**";
	/**
	 * 解析sql：begin注释
	 */
	public static final String BEGIN_PREFIX = "#.*(?i)begin.*";
	/**
	 * 解析sql：end注释
	 */
	public static final String END_PREFIX = "#.*(?i)end.*";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.url=jdbc:mysql://127.0.0.1:3306
	 * </p>
	 */
	public static final String DBSERVER_URL = "db.url";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.password=root
	 * </p>
	 */
	public static final String DB_PASSWORD = "db.password";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.username=root,保证足够高权限
	 * </p>
	 */
	public static final String DB_USERNAME = "db.username";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.driver=com.mysql.jdbc.Driver
	 * </p>
	 */
	public static final String DB_DRIVER = "db.driver";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.param=
	 * </p>
	 */
	public static final String DB_PARAM = "db.param";
	/**
	 * 建库SQL 注意最后保留空格
	 */
	public static final String CREATE_SQL = "CREATE DATABASE IF NOT EXISTS ";
	/**
	 * 删库SQL 注意最后保留空格
	 */
	public static final String DROP_SQL = "DROP DATABASE IF EXISTS ";
}
