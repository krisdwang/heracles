package heracles.unit;

import heracles.unit.model.xml.jdbcmatrix.JdbcMatrixModel;
import heracles.unit.model.xml.jdbcmatrix.JdbcMatrixXmlModel;
import heracles.unit.model.xml.jdbcmatrix.ReadDSModel;
import heracles.unit.model.xml.jdbcmatrix.ReadWriteDSModel;
import heracles.unit.model.xml.jdbcmatrix.WriteDSModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author kriswang
 * 
 */
public class DataBaseUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseUtils.class);

	/**
	 * dbname后缀
	 */
	private static final String dbNameSuf;

	static {
		String uuid = UUID.randomUUID().toString();
		dbNameSuf = uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23)
				+ uuid.substring(24);
	}

	/**
	 * properties需配置key
	 * <p>
	 * 例：db.url=jdbc:mysql://127.0.0.1:3306
	 * </p>
	 */
	private static final String DBSERVER_URL = "db.url";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.logicname=db01,db02
	 * </p>
	 */
	// private static final String DB_LOGICNAME = "db.logicname";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.password=root
	 * </p>
	 */
	private static final String DB_PASSWORD = "db.password";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.username=root,保证足够高权限
	 * </p>
	 */
	private static final String DB_USERNAME = "db.username";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.driver=com.mysql.jdbc.Driver
	 * </p>
	 */
	private static final String DB_DRIVER = "db.driver";
	/**
	 * properties需配置key
	 * <p>
	 * 例：db.param=
	 * </p>
	 */
	private static final String DB_PARAM = "db.param";

	/**
	 * 根据logicname配置生成对应db
	 */
	private static Set<String> db_names = new HashSet<String>();
	/**
	 * properties配置key'DB_PASSWORD'对应value
	 */
	private static String db_password = "";
	/**
	 * properties配置key'DB_USERNAME'对应value
	 */
	private static String db_username = "";
	/**
	 * properties配置key'DBSERVER_URL'对应value
	 */
	private static String db_url = "";
	/**
	 * properties配置key'DB_DRIVER'对应value
	 */
	private static String db_driver = "";
	/**
	 * properties配置key'DB_PARAM'对应value
	 */
	private static String db_param = "";

	/**
	 * 建库SQL 注意最后保留空格
	 */
	private static final String CREATE_SQL = "CREATE DATABASE IF NOT EXISTS ";
	/**
	 * 删库SQL 注意最后保留空格
	 */
	private static final String DROP_SQL = "DROP DATABASE IF EXISTS ";

	/**
	 * 创建db
	 * 
	 * @param location :配置文件所在位置
	 * @param contextLocation :context配置文件所在位置
	 */
	public static void createDb(String location, String[] contextLocation) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		if (infoEnabled) {
			LOGGER.info("createDb is beginning! [" + location + "]");
		}
		String dbProps = resolveDbConfig(location);
		Map<String, Object> propsMap = Utils.propertiesToMap(dbProps);
		if (propsMap != null) {
			if (propsMap.get(DBSERVER_URL) != null) {
				db_url = propsMap.get(DBSERVER_URL).toString();
			}
			if (propsMap.get(DB_PARAM) != null) {
				db_param = propsMap.get(DB_PARAM).toString();
			}
			if (propsMap.get(DB_DRIVER) != null) {
				db_driver = propsMap.get(DB_DRIVER).toString();
			}
			if (propsMap.get(DB_PASSWORD) != null) {
				db_password = propsMap.get(DB_PASSWORD).toString();
			}
			if (propsMap.get(DB_USERNAME) != null) {
				db_username = propsMap.get(DB_USERNAME).toString();
			}

			Map<String, Object> logicnameMap = resolveLogicname(contextLocation);
			List<String> logicnames = new ArrayList<String>();
			if (logicnameMap != null) {
				Set<String> keySet = logicnameMap.keySet();
				for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (logicnameMap.get(key) != null && StringUtils.isNotBlank(logicnameMap.get(key).toString())) {
						logicnames.add(key);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(logicnames)) {
				for (String logicname : logicnames) {
					Connection conn = null;
					try {
						String dbname = logicname + "_" + dbNameSuf;
						String dbUrl = db_url + "/" + dbname + "?" + db_param;
						// FIXME AZEN 多个配置如何处理
						System.setProperty("resource.rdbms.mysql." + logicname + ".url", dbUrl);
						System.setProperty("resource.rdbms.mysql." + logicname + ".driver", db_driver);
						System.setProperty("resource.rdbms.mysql." + logicname + ".username", db_username);
						System.setProperty("resource.rdbms.mysql." + logicname + ".password", db_password);

						Class.forName(db_driver).newInstance();
						conn = DriverManager.getConnection(db_url + "?" + db_param, db_username, db_password);
						Statement stmt = conn.createStatement();
						stmt.executeUpdate(CREATE_SQL + dbname);
						stmt.close();
						conn.close();
						/**
						 * dbname
						 */
						db_names.add(dbname);
						if (infoEnabled) {
							LOGGER.info("db[" + dbname + "] is created!");
						}
					} catch (SQLException e) {
						LOGGER.error("createDb SQLException :" + e.getMessage());
					} catch (Exception e) {
						LOGGER.error("createDb Exception :" + e.getMessage());
					}
				}
			}
		}
		if (infoEnabled) {
			LOGGER.info("createDb is ending!");
		}
	}

	/**
	 * 初始化db
	 * 
	 * @param location :sql文件所在位置
	 */
	public static void initDb(String location) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		if (infoEnabled) {
			LOGGER.info("initDb is beginning! [" + location + "]");
		}
		String[] props = resolveSqlConfig(location);
		if (CollectionUtils.isNotEmpty(db_names)) {
			for (String db : db_names) {
				if (props != null && props.length > 0) {
					Connection conn = null;
					try {
						Class.forName(db_driver).newInstance();
						conn = DriverManager
								.getConnection(db_url + "/" + db + "?" + db_param, db_username, db_password);
						Statement stmt = conn.createStatement();
						for (String prop : props) {
							if (StringUtils.isNotBlank(prop)) {
								stmt.addBatch(prop);
							}
						}
						stmt.executeBatch();
						stmt.close();
						conn.close();
						if (infoEnabled) {
							LOGGER.info("db[" + db + "] is initialized!");
						}
					} catch (SQLException e) {
						LOGGER.error("initDb SQLException :" + e.getMessage());
					} catch (Exception e) {
						LOGGER.error("initDb Exception :" + e.getMessage());
					}
				}
			}
		}
		if (infoEnabled) {
			LOGGER.info("initDb is ending!");
		}
	}

	/**
	 * 删除db
	 */
	public static void dropDb() {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		if (CollectionUtils.isNotEmpty(db_names)) {
			List<String> tempDb = new ArrayList<String>();
			for (String db : db_names) {
				Connection conn = null;
				try {
					Class.forName(db_driver).newInstance();
					conn = DriverManager.getConnection(db_url + "?" + db_param, db_username, db_password);
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(DROP_SQL + db);
					stmt.close();
					tempDb.add(db);
					if (infoEnabled) {
						LOGGER.info("db[" + db + "] is dropped!");
					}
				} catch (SQLException e) {
					LOGGER.error("dropDb SQLException :" + e.getMessage());
				} catch (Exception e) {
					LOGGER.error("dropDb Exception :" + e.getMessage());
				}
			}
			db_names.removeAll(tempDb);
		}
	}

	/**
	 * 获取db.properties内容
	 * 
	 * @param location
	 * @return String
	 */
	private static String resolveDbConfig(String location) {
		InputStream is = DataBaseUtils.class.getClassLoader().getResourceAsStream(location);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Could not Creates an InputStreamReader that uses the named charset :" + e.getMessage());
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				LOGGER.error("Could not load file from " + location + ": " + e.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error("Could not close file from " + location + ": " + e.getMessage());
				}
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 获取db.properties内容
	 * 
	 * @param location
	 * @return String
	 */
	private static String[] resolveSqlConfig(String location) {
		InputStream is = DataBaseUtils.class.getClassLoader().getResourceAsStream(location);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Could not Creates an InputStreamReader that uses the named charset :" + e.getMessage());
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {

			} finally {
				try {
					is.close();
				} catch (IOException e) {

				}
			}
			String s = sb.toString();
			Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/");
			String presult = p.matcher(s).replaceAll("$1");
			return presult.split(";");
		}
		return null;
	}

	/**
	 * 获取xml内容
	 * 
	 * @param url
	 * @return
	 */
	private static String resolveXmlFile(String location) {
		InputStream is = DataBaseUtils.class.getClassLoader().getResourceAsStream(location);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Could not Creates an InputStreamReader that uses the named charset :" + e.getMessage());
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				LOGGER.error("Could not load xml from " + location + ": " + e.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error("Could not close inputStream from " + location + ": " + e.getMessage());
				}
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 获取xml内容
	 * 
	 * @param url
	 * @return
	 */
	private static Map<String, Object> resolveLogicname(String[] contextLocation) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		Map<String, Object> result = new HashMap<String, Object>();
		if (contextLocation != null && contextLocation.length > 0) {
			for (String context : contextLocation) {
				context = context.replace("classpath:", "");
				context = context.replace("classpath*:", "");
				if (infoEnabled) {
					LOGGER.info("config-path [" + context + "]");
				}
				context = resolveXmlFile(context);
				try {
					JdbcMatrixXmlModel model = (JdbcMatrixXmlModel) Utils.springXmlToBean(JdbcMatrixXmlModel.class,
							context);
					if (model != null) {
						List<JdbcMatrixModel> jdbcList = model.getJdbcList();
						if (CollectionUtils.isNotEmpty(jdbcList)) {
							for (JdbcMatrixModel jdbc : jdbcList) {
								String dbType = jdbc.getDbType();
								List<ReadWriteDSModel> readWriteDSList = jdbc.getReadWriteDSList();
								if (CollectionUtils.isNotEmpty(readWriteDSList)) {
									for (ReadWriteDSModel readWriteDS : readWriteDSList) {
										List<WriteDSModel> writeDSList = readWriteDS.getWriteDSList();
										if (CollectionUtils.isNotEmpty(writeDSList)) {
											for (WriteDSModel writeDS : writeDSList) {
												result.put(writeDS.getLogicName(), dbType);
												if (infoEnabled) {
													LOGGER.info("dbType [" + dbType + "];" + "logicname ["
															+ writeDS.getLogicName() + "]");
												}
											}
										}
										List<ReadDSModel> readDSList = readWriteDS.getReadDSList();
										if (CollectionUtils.isNotEmpty(readDSList)) {
											for (ReadDSModel readDS : readDSList) {
												result.put(readDS.getLogicName(), dbType);
												if (infoEnabled) {
													LOGGER.info("dbType [" + dbType + "];" + "logicname ["
															+ readDS.getLogicName() + "]");
												}
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("can not resolve [" + context + ":" + e.getMessage() + "]");
				}
			}
		}
		return result;
	}
}
