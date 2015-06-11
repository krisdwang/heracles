package heracles.unit;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * 继承并实现org.springframework.test.context.support.AbstractTestExecutionListener，
 * 在执行测试类之前和结束时，按 用户设定执行相应的数据操作。
 * <p>
 * 用法如下：@TestExecutionListeners({DbUnitTestExecutionListener.class })
 * </p>
 * 
 * @author kriswang
 */
public class DbInitTestExecutionListener extends AbstractTestExecutionListener {

	private static final Logger log = LoggerFactory
			.getLogger(DbInitTestExecutionListener.class);

	/**
	 * 计数器专用,请同步修改DbInitRunner
	 */
	private static final String COUNTER_KEY = "counter_key";

	/**
	 * db.properties路径
	 */
	private static final String PROPERTIES_FILE = "db.properties";

	/**
	 * db.sql路径
	 */
	private static final String SQL_FILE = "db.sql";

	private static final Object lock = new Object();

	/**
	 * 执行测试类之前，先执行定义的初始化数据操作
	 * 
	 * @param testContext
	 *            TestContext，spring框架测试上下文
	 * @throws Exception
	 *             任何异常直接抛出，数据源连不上、测试文件找不到、格式非法之类异常，将直接导致测试失败
	 */
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		try {
			Class<?> testClass = testContext.getTestClass();

			CreateDb dbInitConf = testClass.getAnnotation(CreateDb.class);
			ContextConfiguration contextLocation = testClass
					.getAnnotation(ContextConfiguration.class);
			if (dbInitConf != null && contextLocation != null) {
				DataBaseHelper.createDb(PROPERTIES_FILE,
						contextLocation.locations());
				synchronized (lock) {
					DataBaseHelper.initDb(SQL_FILE);
					System.setProperty(SQL_FILE, "true");
				}
			}
			PrePostSql prePostSql = testClass.getAnnotation(PrePostSql.class);
			if (prePostSql != null) {
				String preSqlFile = prePostSql.preSqlFile();
				if (StringUtils.isNotBlank(preSqlFile)) {
					preSqlFile = preSqlFile.replace("classpath:", "");
					preSqlFile = preSqlFile.replace("classpath*:", "");
					synchronized (lock) {
						// if
						// (StringUtils.isBlank(System.getProperty(preSqlFile)))
						// {
						DataBaseHelper.initDb(preSqlFile);
						// FIXME AZEN 有需要吗
						System.setProperty(preSqlFile, "true");
						// }
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("beforeTestClass called with [" + testContext + "].");
			}
		} catch (RuntimeException e) {
			DataBaseHelper.dropDb();
		}
	}

	/**
	 * 执行测试方法之前，先执行定义的初始化数据操作
	 * 
	 * @param testContext
	 *            TestContext，spring框架测试上下文
	 * @throws Exception
	 *             任何异常直接抛出，数据源连不上、测试文件找不到、格式非法之类异常，将直接导致测试失败
	 */
	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		try {
			Method testMethod = testContext.getTestMethod();

			PrePostSql prePostSql = testMethod.getAnnotation(PrePostSql.class);
			if (prePostSql != null) {
				String preSqlFile = prePostSql.preSqlFile();
				if (StringUtils.isNotBlank(preSqlFile)) {
					preSqlFile = preSqlFile.replace("classpath:", "");
					preSqlFile = preSqlFile.replace("classpath*:", "");
					synchronized (lock) {
						//
						DataBaseHelper.initDb(preSqlFile);
						// FIXME AZEN 有需要吗
						System.setProperty(preSqlFile, "true");
						// }
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("beforeTestMethod called with [" + testContext + "].");
			}
		} catch (RuntimeException e) {
			DataBaseHelper.dropDb();
		}
	}

	/**
	 * 执行测试方法之后，先执行定义的初始化数据操作
	 * 
	 * @param testContext
	 *            TestContext，spring框架测试上下文
	 * @throws Exception
	 *             任何异常直接抛出，数据源连不上、测试文件找不到、格式非法之类异常，将直接导致测试失败
	 */
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		try {
			Method testMethod = testContext.getTestMethod();

			PrePostSql prePostSql = testMethod.getAnnotation(PrePostSql.class);
			if (prePostSql != null) {
				String postSqlFile = prePostSql.postSqlFile();
				if (StringUtils.isNotBlank(postSqlFile)) {
					postSqlFile = postSqlFile.replace("classpath:", "");
					postSqlFile = postSqlFile.replace("classpath*:", "");
					synchronized (lock) {
						// if
						// (StringUtils.isBlank(System.getProperty(postSqlFile)))
						// {
						DataBaseHelper.initDb(postSqlFile);
						// FIXME AZEN 有需要吗
						System.setProperty(postSqlFile, "true");
						// }
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("afterTestMethod called with [" + testContext + "].");
			}
		} catch (RuntimeException e) {
			DataBaseHelper.dropDb();
		}
	}

	/**
	 * 执行测试类之后，执行定义的数据善后操作
	 * 
	 * @param testContext
	 *            TestContext
	 * @throws Exception
	 *             任何异常直接抛出，数据源连不上、测试文件找不到、格式非法之类异常，将直接导致测试失败
	 */
	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		try {
			boolean infoEnabled = log.isInfoEnabled();
			Class<?> testClass = testContext.getTestClass();
			PrePostSql prePostSql = testClass.getAnnotation(PrePostSql.class);
			if (prePostSql != null) {
				String postSqlFile = prePostSql.postSqlFile();
				if (StringUtils.isNotBlank(postSqlFile)) {
					postSqlFile = postSqlFile.replace("classpath:", "");
					postSqlFile = postSqlFile.replace("classpath*:", "");
					synchronized (lock) {
						if (StringUtils
								.isBlank(System.getProperty(postSqlFile))) {
							DataBaseHelper.initDb(postSqlFile);
							// FIXME AZEN 有需要吗
							System.setProperty(postSqlFile, "true");
						}
					}
				}
			}
			String classCounter = System.getProperty(testClass.getName());
			if (StringUtils.isNotBlank(classCounter)) {
				int num = Integer.valueOf(classCounter);
				num = num - 1;
				if (num > 0) {
					System.setProperty(testClass.getName(), num + "");
					if (infoEnabled) {
						log.info("counter minus  1 [" + testClass.getName()
								+ ":" + num + "]!");
					}
				} else {
					CreateDb dbInitConf = testClass
							.getAnnotation(CreateDb.class);
					if (dbInitConf != null) {
						DataBaseHelper.dropDb();
					}
				}
			} else {
				synchronized (lock) {
					String globalCounter = System.getProperty(COUNTER_KEY);
					if (StringUtils.isNotBlank(globalCounter)) {
						int num = Integer.valueOf(globalCounter);
						num = num - 1;
						if (num > 0) {
							System.setProperty(COUNTER_KEY, num + "");
							if (infoEnabled) {
								log.info("counter minus  1 ["
										+ testClass.getName() + ":" + num
										+ "]!");
							}
						} else {
							CreateDb dbInitConf = testClass
									.getAnnotation(CreateDb.class);
							System.setProperty(COUNTER_KEY, num + "");
							if (dbInitConf != null) {
								DataBaseHelper.dropDb();
							}
						}
					}
				}
			}
			if (infoEnabled) {
				log.info("afterTestClass called with [" + testContext + "].");
			}
		} catch (RuntimeException e) {
			DataBaseHelper.dropDb();
		}
	}

}
