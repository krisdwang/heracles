package heracles.unit;

import heracles.unit.model.json.AtomModel;
import heracles.unit.model.json.GroupModel;
import heracles.unit.model.json.JdbcJsonConfHandler;
import heracles.unit.model.json.MatrixModel;
import heracles.unit.model.xml.allinone.AllInOneDsXmlModel;
import heracles.unit.model.xml.jdbcmatrix.JdbcMatrixModel;
import heracles.unit.model.xml.jdbcmatrix.JdbcMatrixXmlModel;
import heracles.unit.model.xml.jdbcmatrix.ReadDSModel;
import heracles.unit.model.xml.jdbcmatrix.ReadWriteDSModel;
import heracles.unit.model.xml.jdbcmatrix.WriteDSModel;
import heracles.unit.model.xml.matrixdatasource.MatrixDsXmlModel;

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
 * BaseDbTest：db操作
 * 
 * @author kriswang
 * 
 */
public class DataBaseHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseHelper.class);

	/**
	 * 去注释正则表达式
	 */
	private static final String PATTERN_REGEX = "(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/";

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
	
	private static String matrix_name = "";

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
			if (propsMap.get(TestConstants.DBSERVER_URL) != null) {
				db_url = propsMap.get(TestConstants.DBSERVER_URL).toString();
				if (!propsMap.get(TestConstants.DBSERVER_URL).toString().toLowerCase().contains(TestConstants.H2_DATABASE)) {
					db_param = TestConstants.QUESTION_MARK + db_param;
				} else {
					db_param = TestConstants.SEMICOLON + db_param;
				}
			}
			if (propsMap.get(TestConstants.DB_PARAM) != null) {
				db_param = propsMap.get(TestConstants.DB_PARAM).toString();
			}
			if (propsMap.get(TestConstants.DB_DRIVER) != null) {
				db_driver = propsMap.get(TestConstants.DB_DRIVER).toString();
			}
			if (propsMap.get(TestConstants.DB_PASSWORD) != null) {
				db_password = propsMap.get(TestConstants.DB_PASSWORD).toString();
			}
			if (propsMap.get(TestConstants.DB_USERNAME) != null) {
				db_username = propsMap.get(TestConstants.DB_USERNAME).toString();
			}

			List<String> logicnames = new ArrayList<String>();
			/**
			 * 获取xml内容:heracles-datasource:jdbc-matrix
			 */
			List<String> logicnameListForJdbcMatrix = resolveJdbcMatrixForLogicname(contextLocation);
			if (CollectionUtils.isNotEmpty(logicnameListForJdbcMatrix)) {
				for (String logicname : logicnameListForJdbcMatrix) {
					logicnames.add(logicname);
				}
			}
			/**
			 * 获取xml内容:heracles-datasource:matrix-datasource
			 */
			List<String> logicnameListForMatrixDs = resolveMatrixDsForLogicname(contextLocation);
			if (CollectionUtils.isNotEmpty(logicnameListForMatrixDs)) {
				for (String logicname : logicnameListForMatrixDs) {
					logicnames.add(logicname);
				}
			}
			/**
			 * 获取xml内容:heracles-datasource:allinone-datasource
			 */
			List<String> logicnameListForAllInOneDs = resolveAllInOneDsForLogicname(contextLocation);
			if (CollectionUtils.isNotEmpty(logicnameListForAllInOneDs)) {
				for (String logicname : logicnameListForAllInOneDs) {
					logicnames.add(logicname);
				}
			}
			if (CollectionUtils.isNotEmpty(logicnames)) {
				for (String logicname : logicnames) {
					Connection conn = null;
					try {
						String dbname = logicname + "_" + dbNameSuf;
						String dbUrl = db_url + "/" + dbname + db_param;
						System.setProperty("resource.rdbms.mysql." + logicname + ".url", dbUrl);
						System.setProperty("resource.rdbms.mysql." + logicname + ".driver", db_driver);
						System.setProperty("resource.rdbms.mysql." + logicname + ".username", db_username);
						System.setProperty("resource.rdbms.mysql." + logicname + ".password", db_password);

						if (propsMap.get(TestConstants.DBSERVER_URL) != null
								&& !propsMap.get(TestConstants.DBSERVER_URL).toString().toLowerCase()
										.contains(TestConstants.H2_DATABASE)) {
							Class.forName(db_driver).newInstance();
							conn = DriverManager.getConnection(db_url + db_param, db_username, db_password);
							Statement stmt = conn.createStatement();
							stmt.executeUpdate(TestConstants.CREATE_SQL + dbname);
							stmt.close();
							conn.close();
						}
						/**
						 * dbname
						 */
						db_names.add(dbname);
						if (infoEnabled) {
							LOGGER.info("db[" + dbname + "] is created!");
						}
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
		Map<String, String[]> props = resolveSqlConfig(location);
		if (CollectionUtils.isNotEmpty(db_names)) {
			for (String db : db_names) {
				if (props != null) {
					Connection conn = null;
					try {
						Class.forName(db_driver).newInstance();
						conn = DriverManager.getConnection(db_url + "/" + db + db_param, db_username, db_password);
						Statement stmt = conn.createStatement();
						String[] sqls;
						if (props.containsKey(db)) {
							sqls = props.get(db);
						} else {
							sqls = props.get(TestConstants.ALL_SYMBOL);
						}
						if (sqls != null && sqls.length > 0) {
							for (String prop : sqls) {
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
					conn = DriverManager.getConnection(db_url + db_param, db_username, db_password);
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(TestConstants.DROP_SQL + db);
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
		InputStream is = DataBaseHelper.class.getClassLoader().getResourceAsStream(location);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, TestConstants.UTF_8));
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
	private static Map<String, String[]> resolveSqlConfig(String location) {
		Map<String, String[]> result = new HashMap<String, String[]>();

		List<String> atomList = new ArrayList<String>();

		InputStream is = DataBaseHelper.class.getClassLoader().getResourceAsStream(location);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, TestConstants.UTF_8));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Could not Creates an InputStreamReader that uses the named charset :" + e.getMessage());
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			StringBuilder sbTemp = new StringBuilder();
			boolean flag = false;
			try {
				while ((line = br.readLine()) != null) {
					// match算法
					if (line.matches(TestConstants.BEGIN_PREFIX)) {
						flag = true;
						atomList.clear();
						// 解析group
						if (line.contains(TestConstants.GROUP_CONF)) {
							atomList.addAll(resolveGroupName(line));
						}
						// 解析Atom 未考虑**的情况,注释形式
						if (line.contains(TestConstants.ATOM_CONF)) {
							atomList.addAll(resolveAtomName(line));
						}
					} else {
						if (line.matches(TestConstants.END_PREFIX)) {
							String presultTemp = Pattern.compile(PATTERN_REGEX).matcher(sbTemp.toString())
									.replaceAll("$1");
							// put result
							if (CollectionUtils.isNotEmpty(atomList)) {
								for (String atom : atomList) {
									result.put(atom + "_" + dbNameSuf, presultTemp.split(TestConstants.SPLIT_ELEMENT));
								}
							}
							// clear sbTemp
							sbTemp.delete(0, sbTemp.length());
							flag = false;
						} else {
							if (flag) {
								sbTemp.append(line);
							} else {
								sb.append(line);
							}
						}
					}
				}
			} catch (IOException e) {
				LOGGER.error("Could not resolve InputStreamReader :" + e.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error("Could not resolve InputStreamReader :" + e.getMessage());
				}
			}
			String presult = Pattern.compile(PATTERN_REGEX).matcher(sb.toString()).replaceAll("$1");
			result.put(TestConstants.ALL_SYMBOL, presult.split(TestConstants.SPLIT_ELEMENT));
			return result;
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
		InputStream is = DataBaseHelper.class.getClassLoader().getResourceAsStream(location);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, TestConstants.UTF_8));
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
	 * 获取xml内容:heracles-datasource:jdbc-matrix
	 * 
	 * @param url
	 * @return
	 */
	private static List<String> resolveJdbcMatrixForLogicname(String[] contextLocation) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		List<String> result = new ArrayList<String>();
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
												// result.put(writeDS.getLogicName(), dbType);
												result.add(writeDS.getLogicName());
												if (infoEnabled) {
													LOGGER.info("dbType [" + dbType + "];" + "logicname ["
															+ writeDS.getLogicName() + "]");
												}
											}
										}
										List<ReadDSModel> readDSList = readWriteDS.getReadDSList();
										if (CollectionUtils.isNotEmpty(readDSList)) {
											for (ReadDSModel readDS : readDSList) {
												// result.put(readDS.getLogicName(), dbType);
												result.add(readDS.getLogicName());
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

	/**
	 * 获取xml内容:heracles-datasource:matrix-datasource
	 * 
	 * @param url
	 * @return
	 */
	private static List<String> resolveMatrixDsForLogicname(String[] contextLocation) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		List<String> result = new ArrayList<String>();
		if (contextLocation != null && contextLocation.length > 0) {
			for (String context : contextLocation) {
				context = context.replace(TestConstants.CLASS_PATH, "");
				context = context.replace(TestConstants.CLASS_PATH_ALL, "");
				if (infoEnabled) {
					LOGGER.info("config-path [" + context + "]");
				}
				context = resolveXmlFile(context);
				try {
					MatrixDsXmlModel model = (MatrixDsXmlModel) Utils.springXmlToBean(MatrixDsXmlModel.class, context);
					if (model != null) {
						String matrixName = model.getModel().getMatrixName();
						/**
						 * 解析：json to model
						 */
						JdbcJsonConfHandler jdbcHdlr = new JdbcJsonConfHandler(matrixName);
						matrix_name = matrixName;
						MatrixModel matrixModel = jdbcHdlr.getJdbcConfig();

						if (matrixModel != null) {
							if (CollectionUtils.isNotEmpty(matrixModel.getGroups())) {
								for (GroupModel groupModel : matrixModel.getGroups()) {
									if (CollectionUtils.isNotEmpty(groupModel.getAtoms())) {
										for (AtomModel atomModel : groupModel.getAtoms()) {
											// result.put(atomModel.getAtomName(), JdbcType.MYSQL);
											result.add(atomModel.getAtomName());
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
	
	/**
	 * 获取xml内容:heracles-datasource:allinone-datasource
	 * 
	 * @param url
	 * @return
	 */
	private static List<String> resolveAllInOneDsForLogicname(String[] contextLocation) {
		boolean infoEnabled = LOGGER.isInfoEnabled();
		List<String> result = new ArrayList<String>();
		if (contextLocation != null && contextLocation.length > 0) {
			for (String context : contextLocation) {
				context = context.replace(TestConstants.CLASS_PATH, "");
				context = context.replace(TestConstants.CLASS_PATH_ALL, "");
				if (infoEnabled) {
					LOGGER.info("config-path [" + context + "]");
				}
				context = resolveXmlFile(context);
				try {
					AllInOneDsXmlModel model = (AllInOneDsXmlModel) Utils.springXmlToBean(AllInOneDsXmlModel.class, context);
					if (model != null) {
						String matrixName = model.getModel().getMatrixName();
						/**
						 * 解析：json to model
						 */
						JdbcJsonConfHandler jdbcHdlr = new JdbcJsonConfHandler(matrixName);
						matrix_name = matrixName;
						MatrixModel matrixModel = jdbcHdlr.getJdbcConfig();

						if (matrixModel != null) {
							if (CollectionUtils.isNotEmpty(matrixModel.getGroups())) {
								for (GroupModel groupModel : matrixModel.getGroups()) {
									if (CollectionUtils.isNotEmpty(groupModel.getAtoms())) {
										for (AtomModel atomModel : groupModel.getAtoms()) {
											// result.put(atomModel.getAtomName(), JdbcType.MYSQL);
											result.add(atomModel.getAtomName());
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

	private static List<String> resolveGroupName(String express) {
		List<String> result = new ArrayList<String>();

		if (StringUtils.isBlank(express)) {
			return result;
		}
		/**
		 * 解析：json to model
		 */
		JdbcJsonConfHandler jdbcHdlr = new JdbcJsonConfHandler(matrix_name);
		MatrixModel matrixModel = jdbcHdlr.getJdbcConfig();
		/**
		 * 解析express,只取第一个
		 */
		String groupStr = express.substring(
				express.indexOf(TestConstants.GROUP_CONF) + TestConstants.GROUP_CONF.length(),
				express.indexOf(TestConstants.SPLIT_ELEMENT,
						express.indexOf(TestConstants.GROUP_CONF) + TestConstants.GROUP_CONF.length()));
		if (StringUtils.isNotBlank(groupStr)) {
			String[] groupArray = groupStr.split(TestConstants.SPLIT_VALUE);
			if (groupArray != null && groupArray.length > 0) {
				for (String groupName : groupArray) {
					if (CollectionUtils.isNotEmpty(matrixModel.getGroups())) {
						for (GroupModel groupModel : matrixModel.getGroups()) {
							if (match(groupModel.getGroupName(), groupName)) {
								if (CollectionUtils.isNotEmpty(groupModel.getAtoms())) {
									for (AtomModel atomModel : groupModel.getAtoms()) {
										result.add(atomModel.getAtomName());
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private static List<String> resolveAtomName(String express) {
		List<String> result = new ArrayList<String>();

		if (StringUtils.isBlank(express)) {
			return result;
		}
		/**
		 * 解析：json to model
		 */
		JdbcJsonConfHandler jdbcHdlr = new JdbcJsonConfHandler(matrix_name);
		MatrixModel matrixModel = jdbcHdlr.getJdbcConfig();
		/**
		 * 解析express,只取第一个
		 */
		String atomStr = express.substring(
				express.indexOf(TestConstants.ATOM_CONF) + TestConstants.ATOM_CONF.length(),
				express.indexOf(TestConstants.SPLIT_ELEMENT,
						express.indexOf(TestConstants.ATOM_CONF) + TestConstants.ATOM_CONF.length()));
		if (StringUtils.isNotBlank(atomStr)) {
			String[] atomArray = atomStr.split(TestConstants.SPLIT_VALUE);
			if (atomArray != null && atomArray.length > 0) {
				for (String atom : atomArray) {
					if (CollectionUtils.isNotEmpty(matrixModel.getGroups())) {
						for (GroupModel groupModel : matrixModel.getGroups()) {
							if (CollectionUtils.isNotEmpty(groupModel.getAtoms())) {
								for (AtomModel atomModel : groupModel.getAtoms()) {
									if (match(atomModel.getAtomName(), atom)) {
										result.add(atomModel.getAtomName());
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private static boolean match(String fromStr, String toStr) {
		if (StringUtils.isBlank(fromStr) || StringUtils.isBlank(toStr)) {
			return false;
		}
		return fromStr.equals(toStr) || fromStr.equals(TestConstants.ALL_SYMBOL);
	}
}
