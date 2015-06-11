package heracles.jdbc.spring.util;

import heracles.jdbc.matrix.model.AtomModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class Utils {

	public static Map<String, String> getDruidDefaulProperties(String driverClassName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("driverClassName", driverClassName);
		map.put("initialSize", "5"); // 默认值为0
		map.put("maxActive", "10");
		// map.put("maxIdle", "8"); // 已废弃
		map.put("minIdle", "3");
		// map.put("maxWait", "1000"); // 不建议使用
		// map.put("poolPreparedStatements", "true"); // 不建议使用
		// map.put("maxOpenPreparedStatements", "");
		map.put("validationQuery", "SELECT 1 FROM DUAL");
		// map.put("testOnBorrow", "true");
		// map.put("testOnReturn", "false"); // 不建议使用，影响性能
		map.put("testWhileIdle", "true");
		map.put("timeBetweenEvictionRunsMillis", "60000");
		// map.put("numTestsPerEvictionRun", ""); // 已废弃
		// map.put("minEvictableIdleTimeMillis", "");
		// map.put("connectionInitSqls", "");
		// map.put("exceptionSorter", "");
		// map.put("filters", "");
		// map.put("proxyFilters", "");
		// map.put("url", "");
		// map.put("username", "");
		// map.put("password", "");

		return map;
	}

	public static Map<String, String> getC3P0DefaulProperties(String driverClassName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("acquireIncrement", "2");
		// map.put("acquireRetryAttempts", "");
		// map.put("acquireRetryDelay", "");
		// map.put("autoCommitOnClose", "");
		// map.put("automaticTestTable", "");
		// map.put("breakAfterAcquireFailure", "");
		// TODO Anders Zhu 测试
		map.put("checkoutTimeout", "30000");
		// map.put("connectionCustomizerClassName", "");
		// map.put("connectionTesterClassName", "");
		// map.put("contextClassLoaderSource", "");
		// map.put("dataSourceName", "");
		// map.put("debugUnreturnedConnectionStackTraces", "");
		map.put("driverClass", driverClassName);
		// map.put("extensions", "");
		// map.put("factoryClassLocation", "");
		// map.put("forceIgnoreUnresolvedTransactions", "");
		// map.put("forceUseNamedDriverClass", "");
		map.put("idleConnectionTestPeriod", "10");
		map.put("initialPoolSize", "5");
		// map.put("jdbcUrl", "");
		// map.put("maxAdministrativeTaskTime", "");
		// map.put("maxConnectionAge", "");
		map.put("maxIdleTime", "60");
		// map.put("maxIdleTimeExcessConnections", "");
		map.put("maxPoolSize", "10");
		// map.put("maxStatements", "");
		// map.put("maxStatementsPerConnection", "");
		map.put("minPoolSize", "3");
		// map.put("numHelperThreads", "");
		// map.put("overrideDefaultUser", "");
		// map.put("overrideDefaultPassword", "");
		// map.put("password", "");
		// map.put("preferredTestQuery", "");
		// map.put("privilegeSpawnedThreads", "");
		// map.put("propertyCycle", "");
		// map.put("statementCacheNumDeferredCloseThreads", "");
		// map.put("testConnectionOnCheckin", "");
		// map.put("testConnectionOnCheckout", "");
		// map.put("unreturnedConnectionTimeout", "");
		// map.put("user", "");
		// map.put("usesTraditionalReflectiveProxies", "");

		return map;
	}

	public static Map<String, String> getDBCPDefaulProperties(String driverClassName) {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("username", "");
		// map.put("password", "");
		// map.put("url", "");
		map.put("driverClassName", driverClassName);
		// map.put("connectionProperties", "");
		// map.put("defaultAutoCommit", "");
		// map.put("defaultReadOnly", "");
		// map.put("defaultTransactionIsolation", "");
		// map.put("defaultCatalog", "");
		// map.put("cacheState", "");
		map.put("initialSize", "5");
		map.put("maxTotal", "10");
		map.put("maxIdle", "5");
		map.put("minIdle", "3");
		map.put("maxWaitMillis", "60000");
		map.put("validationQuery", "SELECT 1 FROM DUAL");
		// map.put("testOnCreate", "");
		// map.put("testOnBorrow", "");
		// map.put("testOnReturn", "");
		// map.put("testWhileIdle", "");
		// map.put("timeBetweenEvictionRunsMillis", "");
		// map.put("numTestsPerEvictionRun", "");
		// map.put("minEvictableIdleTimeMillis", "");
		// map.put("softMiniEvictableIdleTimeMillis", "");
		// map.put("maxConnLifetimeMillis", "");
		// map.put("connectionInitSqls", "");
		// map.put("lifo", "");
		// map.put("poolPreparedStatements", "");
		// map.put("maxOpenPreparedStatements", "");
		// map.put("accessToUnderlyingConnectionAllowed", "");
		// map.put("removeAbandoned", "true");
		// map.put("removeAbandonedTimeout", "");
		// map.put("logAbandoned", "");

		return map;
	}

	public static Map<String, String> getDefaulProperties(String dsType, String dbType) {
		String driverClassName = "com.mysql.jdbc.Driver";
		if (dbType.equalsIgnoreCase("oracle")) {
			driverClassName = "oracle.jdbc.driver.OracleDriver";
		}

		if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DBCP)) {
			return getDBCPDefaulProperties(driverClassName);
		} else if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DRUID)) {
			return getDruidDefaulProperties(driverClassName);
		}

		return getC3P0DefaulProperties(driverClassName);
	}

	public static Map<String, String> getDbLoginPropertiesFromAtom(AtomModel atomModel, String dsType) {
		Map<String, String> map = new HashMap<String, String>();

		if (dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DBCP)
				|| dsType.equalsIgnoreCase(Constants.DATASOURCE_TYPE_DRUID)) {
			map.put("url",
					"jdbc:mysql://" + atomModel.getHost() + ":" + atomModel.getPort() + "/" + atomModel.getDbName());
			map.put("username", atomModel.getUsername());
			map.put("password", atomModel.getPassword());
			return map;
		}

		map.put("jdbcUrl",
				"jdbc:mysql://" + atomModel.getHost() + ":" + atomModel.getPort() + "/" + atomModel.getDbName());
		map.put("user", atomModel.getUsername());
		map.put("password", atomModel.getPassword());
		return map;
	}

	public static boolean isMatch(String patternPath, String requestPath) {
		PathMatcher matcher = new AntPathMatcher();
		return matcher.match(StringUtils.lowerCase(patternPath), StringUtils.lowerCase(requestPath));
	}

	/**
	 * 写文件
	 * 
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeFile(File file, String content) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content, 0, content.length());
			fw.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * 按行读取文件
	 * 
	 * @param file
	 * @return String
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			throw e;
		} finally {
			stream.close();
		}
		return sb.toString();
	}

}
