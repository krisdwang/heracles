package heracles.unit;

/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.annotation.ProfileValueUtils;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks;
import org.springframework.test.context.junit4.statements.SpringFailOnTimeout;
import org.springframework.test.context.junit4.statements.SpringRepeat;
import org.springframework.util.ReflectionUtils;

/**
 * <p>
 * {@code DbInitRunner} is a custom extension of
 * {@link BlockJUnit4ClassRunner} which provides functionality of the
 * <em>Spring TestContext Framework</em> to standard JUnit 4.5+ tests by means
 * of the {@link TestContextManager} and associated support classes and
 * annotations.
 * </p>
 * <p>
 * The following list constitutes all annotations currently supported directly
 * by {@code DbInitRunner}.
 * <em>(Note that additional annotations may be supported by various
 * {@link org.springframework.test.context.TestExecutionListener
 * TestExecutionListeners})</em>
 * </p>
 * <ul>
 * <li>{@link Test#expected() @Test(expected=...)}</li>
 * <li>{@link Test#timeout() @Test(timeout=...)}</li>
 * <li>{@link Timed @Timed}</li>
 * <li>{@link Repeat @Repeat}</li>
 * <li>{@link Ignore @Ignore}</li>
 * <li>
 * {@link org.springframework.test.annotation.ProfileValueSourceConfiguration
 * @ProfileValueSourceConfiguration}</li>
 * <li>{@link org.springframework.test.annotation.IfProfileValue
 * @IfProfileValue}</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> As of Spring 3.0, {@code DbInitRunner} requires JUnit
 * 4.5+.
 * </p>
 * 
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @since 2.5
 * @see TestContextManager
 */
@SuppressWarnings("deprecation")
public class DbInitRunner extends BlockJUnit4ClassRunner {

	private static final Log logger = LogFactory.getLog(DbInitRunner.class);

	private final TestContextManager testContextManager;

	/**
	 * 计数器专用,请同步修改DbInitTestExecutionListener
	 */
	private static final String COUNTER_KEY = "counter_key";

	private static final Object lock = new Object();

	/**
	 * Constructs a new {@code DbInitRunner} and initializes a
	 * {@link TestContextManager} to provide Spring testing functionality to
	 * standard JUnit tests.
	 * 
	 * @param clazz
	 *            the test class to be run
	 * @see #createTestContextManager(Class)
	 */
	public DbInitRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		CreateDb dbInitConf = clazz.getAnnotation(CreateDb.class);
		if (dbInitConf != null) {
			synchronized (lock) {
				if (StringUtils.isNotBlank(System.getProperty(COUNTER_KEY))) {
					int num = Integer.valueOf(System.getProperty(COUNTER_KEY));
					num = num + 1;
					System.setProperty(COUNTER_KEY, num + "");
					if (logger.isInfoEnabled()) {
						logger.info("counter plus 1 [" + COUNTER_KEY + ":"
								+ num + "]!");
					}
				} else {
					System.setProperty(COUNTER_KEY, "1");
					if (logger.isInfoEnabled()) {
						logger.info("counter plus 1 [" + COUNTER_KEY + ":1]!");
					}
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("DbInitRunner constructor called with [" + clazz
					+ "].");
		}
		this.testContextManager = createTestContextManager(clazz);
	}

	/**
	 * Creates a new {@link TestContextManager} for the supplied test class and
	 * the configured <em>default {@code ContextLoader} class name</em>. Can be
	 * overridden by subclasses.
	 * 
	 * @param clazz
	 *            the test class to be managed
	 * @see #getDefaultContextLoaderClassName(Class)
	 */
	protected TestContextManager createTestContextManager(Class<?> clazz) {
		return new TestContextManager(clazz);
	}

	/**
	 * Get the {@link TestContextManager} associated with this runner.
	 */
	protected final TestContextManager getTestContextManager() {
		return this.testContextManager;
	}

	/**
	 * Get the name of the default {@code ContextLoader} class to use for the
	 * supplied test class. The named class will be used if the test class does
	 * not explicitly declare a {@code ContextLoader} class via the
	 * {@code @ContextConfiguration} annotation.
	 * <p>
	 * The default implementation returns {@code null}, thus implying use of the
	 * <em>standard</em> default {@code ContextLoader} class name.
	 * <p>
	 * Can be overridden by subclasses.
	 * 
	 * @param clazz
	 *            the test class
	 * @return {@code null}
	 * @deprecated Spring Framework 4.1 will introduce a bootstrap strategy for
	 *             the TestContext framework at which point this method will be
	 *             removed.
	 */
	@Deprecated
	protected String getDefaultContextLoaderClassName(Class<?> clazz) {
		return null;
	}

	/**
	 * Returns a description suitable for an ignored test class if the test is
	 * disabled via {@code @IfProfileValue} at the class-level, and otherwise
	 * delegates to the parent implementation.
	 * 
	 * @see ProfileValueUtils#isTestEnabledInThisEnvironment(Class)
	 */
	@Override
	public Description getDescription() {
		if (!ProfileValueUtils.isTestEnabledInThisEnvironment(getTestClass()
				.getJavaClass())) {
			return Description.createSuiteDescription(getTestClass()
					.getJavaClass());
		}
		return super.getDescription();
	}

	/**
	 * Check whether the test is enabled in the first place. This prevents
	 * classes with a non-matching {@code @IfProfileValue} annotation from
	 * running altogether, even skipping the execution of
	 * {@code prepareTestInstance()} {@code TestExecutionListener} methods.
	 * 
	 * @see ProfileValueUtils#isTestEnabledInThisEnvironment(Class)
	 * @see org.springframework.test.annotation.IfProfileValue
	 * @see org.springframework.test.context.TestExecutionListener
	 */
	@Override
	public void run(RunNotifier notifier) {
		if (!ProfileValueUtils.isTestEnabledInThisEnvironment(getTestClass()
				.getJavaClass())) {
			notifier.fireTestIgnored(getDescription());
			return;
		}
		super.run(notifier);
	}

	/**
	 * Wraps the {@link Statement} returned by the parent implementation with a
	 * {@link RunBeforeTestClassCallbacks} statement, thus preserving the
	 * default functionality but adding support for the Spring TestContext
	 * Framework.
	 * 
	 * @see RunBeforeTestClassCallbacks
	 */
	@Override
	protected Statement withBeforeClasses(Statement statement) {
		Statement junitBeforeClasses = super.withBeforeClasses(statement);
		return new RunBeforeTestClassCallbacks(junitBeforeClasses,
				getTestContextManager());
	}

	/**
	 * Wraps the {@link Statement} returned by the parent implementation with a
	 * {@link RunAfterTestClassCallbacks} statement, thus preserving the default
	 * functionality but adding support for the Spring TestContext Framework.
	 * 
	 * @see RunAfterTestClassCallbacks
	 */
	@Override
	protected Statement withAfterClasses(Statement statement) {
		Statement junitAfterClasses = super.withAfterClasses(statement);
		return new RunAfterTestClassCallbacks(junitAfterClasses,
				getTestContextManager());
	}

	/**
	 * Delegates to the parent implementation for creating the test instance and
	 * then allows the {@link #getTestContextManager() TestContextManager} to
	 * prepare the test instance before returning it.
	 * 
	 * @see TestContextManager#prepareTestInstance(Object)
	 */
	@Override
	protected Object createTest() throws Exception {
		Object testInstance = super.createTest();
		getTestContextManager().prepareTestInstance(testInstance);
		return testInstance;
	}

	/**
	 * Performs the same logic as
	 * {@link BlockJUnit4ClassRunner#runChild(FrameworkMethod, RunNotifier)},
	 * except that tests are determined to be <em>ignored</em> by
	 * {@link #isTestMethodIgnored(FrameworkMethod)}.
	 */
	@Override
	protected void runChild(FrameworkMethod frameworkMethod,
			RunNotifier notifier) {
		EachTestNotifier eachNotifier = springMakeNotifier(frameworkMethod,
				notifier);
		if (isTestMethodIgnored(frameworkMethod)) {
			eachNotifier.fireTestIgnored();
			return;
		}

		eachNotifier.fireTestStarted();
		try {
			methodBlock(frameworkMethod).evaluate();
		} catch (AssumptionViolatedException e) {
			eachNotifier.addFailedAssumption(e);
		} catch (Throwable e) {
			eachNotifier.addFailure(e);
		} finally {
			eachNotifier.fireTestFinished();
		}
	}

	/**
	 * {@code springMakeNotifier()} is an exact copy of
	 * {@link BlockJUnit4ClassRunner BlockJUnit4ClassRunner's}
	 * {@code makeNotifier()} method, but we have decided to prefix it with
	 * "spring" and keep it {@code private} in order to avoid the compatibility
	 * clashes that were introduced in JUnit between versions 4.5, 4.6, and 4.7.
	 */
	private EachTestNotifier springMakeNotifier(FrameworkMethod method,
			RunNotifier notifier) {
		Description description = describeChild(method);
		return new EachTestNotifier(notifier, description);
	}

	/**
	 * Augments the default JUnit behavior
	 * {@link #withPotentialRepeat(FrameworkMethod, Object, Statement) with
	 * potential repeats} of the entire execution chain.
	 * <p>
	 * Furthermore, support for timeouts has been moved down the execution chain
	 * in order to include execution of {@link org.junit.Before @Before} and
	 * {@link org.junit.After @After} methods within the timed execution. Note
	 * that this differs from the default JUnit behavior of executing
	 * {@code @Before} and {@code @After} methods in the main thread while
	 * executing the actual test method in a separate thread. Thus, the end
	 * effect is that {@code @Before} and {@code @After} methods will be
	 * executed in the same thread as the test method. As a consequence,
	 * JUnit-specified timeouts will work fine in combination with Spring
	 * transactions. Note that JUnit-specific timeouts still differ from
	 * Spring-specific timeouts in that the former execute in a separate thread
	 * while the latter simply execute in the main thread (like regular tests).
	 * 
	 * @see #possiblyExpectingExceptions(FrameworkMethod, Object, Statement)
	 * @see #withBefores(FrameworkMethod, Object, Statement)
	 * @see #withAfters(FrameworkMethod, Object, Statement)
	 * @see #withPotentialTimeout(FrameworkMethod, Object, Statement)
	 * @see #withPotentialRepeat(FrameworkMethod, Object, Statement)
	 */
	@Override
	protected Statement methodBlock(FrameworkMethod frameworkMethod) {
		Object testInstance;
		try {
			testInstance = new ReflectiveCallable() {

				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable ex) {
			return new Fail(ex);
		}

		Statement statement = methodInvoker(frameworkMethod, testInstance);
		statement = possiblyExpectingExceptions(frameworkMethod, testInstance,
				statement);
		statement = withBefores(frameworkMethod, testInstance, statement);
		statement = withAfters(frameworkMethod, testInstance, statement);
		statement = withRulesReflectively(frameworkMethod, testInstance,
				statement);
		statement = withPotentialRepeat(frameworkMethod, testInstance,
				statement);
		statement = withPotentialTimeout(frameworkMethod, testInstance,
				statement);
		return statement;
	}

	/**
	 * Invokes JUnit 4.7's private {@code withRules()} method using reflection.
	 * This is necessary for backwards compatibility with the JUnit 4.5 and 4.6
	 * implementations of {@link BlockJUnit4ClassRunner}.
	 */
	private Statement withRulesReflectively(FrameworkMethod frameworkMethod,
			Object testInstance, Statement statement) {
		Method withRulesMethod = ReflectionUtils.findMethod(getClass(),
				"withRules", FrameworkMethod.class, Object.class,
				Statement.class);
		if (withRulesMethod != null) {
			// Original JUnit 4.7 code:
			// statement = withRules(frameworkMethod, testInstance, statement);
			ReflectionUtils.makeAccessible(withRulesMethod);
			statement = (Statement) ReflectionUtils.invokeMethod(
					withRulesMethod, this, frameworkMethod, testInstance,
					statement);
		}
		return statement;
	}

	/**
	 * Returns {@code true} if {@link Ignore @Ignore} is present for the
	 * supplied {@link FrameworkMethod test method} or if the test method is
	 * disabled via {@code @IfProfileValue}.
	 * 
	 * @see ProfileValueUtils#isTestEnabledInThisEnvironment(Method, Class)
	 */
	protected boolean isTestMethodIgnored(FrameworkMethod frameworkMethod) {
		Method method = frameworkMethod.getMethod();
		return (method.isAnnotationPresent(Ignore.class) || !ProfileValueUtils
				.isTestEnabledInThisEnvironment(method, getTestClass()
						.getJavaClass()));
	}

	/**
	 * Performs the same logic as
	 * {@link BlockJUnit4ClassRunner#possiblyExpectingExceptions(FrameworkMethod, Object, Statement)}
	 * except that the <em>expected exception</em> is retrieved using
	 * {@link #getExpectedException(FrameworkMethod)}.
	 */
	@Override
	protected Statement possiblyExpectingExceptions(
			FrameworkMethod frameworkMethod, Object testInstance, Statement next) {
		Class<? extends Throwable> expectedException = getExpectedException(frameworkMethod);
		return expectedException != null ? new ExpectException(next,
				expectedException) : next;
	}

	/**
	 * Get the {@code exception} that the supplied {@link FrameworkMethod test
	 * method} is expected to throw.
	 * <p>
	 * Supports JUnit's {@link Test#expected() @Test(expected=...)} annotation.
	 * 
	 * @return the expected exception, or {@code null} if none was specified
	 */
	protected Class<? extends Throwable> getExpectedException(
			FrameworkMethod frameworkMethod) {
		Test testAnnotation = frameworkMethod.getAnnotation(Test.class);
		Class<? extends Throwable> junitExpectedException = (testAnnotation != null
				&& testAnnotation.expected() != Test.None.class ? testAnnotation
				.expected() : null);

		return junitExpectedException;
	}

	/**
	 * Supports both Spring's {@link Timed @Timed} and JUnit's
	 * {@link Test#timeout() @Test(timeout=...)} annotations, but not both
	 * simultaneously. Returns either a {@link SpringFailOnTimeout}, a
	 * {@link FailOnTimeout}, or the unmodified, supplied {@link Statement} as
	 * appropriate.
	 * 
	 * @see #getSpringTimeout(FrameworkMethod)
	 * @see #getJUnitTimeout(FrameworkMethod)
	 */
	@Override
	protected Statement withPotentialTimeout(FrameworkMethod frameworkMethod,
			Object testInstance, Statement next) {
		Statement statement = null;
		long springTimeout = getSpringTimeout(frameworkMethod);
		long junitTimeout = getJUnitTimeout(frameworkMethod);
		if (springTimeout > 0 && junitTimeout > 0) {
			String msg = "Test method ["
					+ frameworkMethod.getMethod()
					+ "] has been configured with Spring's @Timed(millis="
					+ springTimeout
					+ ") and JUnit's @Test(timeout="
					+ junitTimeout
					+ ") annotations. Only one declaration of a 'timeout' is permitted per test method.";
			logger.error(msg);
			throw new IllegalStateException(msg);
		} else if (springTimeout > 0) {
			statement = new SpringFailOnTimeout(next, springTimeout);
		} else if (junitTimeout > 0) {
			statement = new FailOnTimeout(next, junitTimeout);
		} else {
			statement = next;
		}

		return statement;
	}

/**
	 * Retrieves the configured JUnit {@code timeout} from the {@link Test
	 * @Test} annotation on the supplied {@link FrameworkMethod test method}.
	 * 
	 * @return the timeout, or {@code 0} if none was specified.
	 */
	protected long getJUnitTimeout(FrameworkMethod frameworkMethod) {
		Test testAnnotation = frameworkMethod.getAnnotation(Test.class);
		return (testAnnotation != null && testAnnotation.timeout() > 0 ? testAnnotation
				.timeout() : 0);
	}

	/**
	 * Retrieves the configured Spring-specific {@code timeout} from the
	 * {@link Timed @Timed} annotation on the supplied {@link FrameworkMethod
	 * test method}.
	 * 
	 * @return the timeout, or {@code 0} if none was specified.
	 */
	protected long getSpringTimeout(FrameworkMethod frameworkMethod) {
		AnnotationAttributes annAttrs = AnnotatedElementUtils
				.getAnnotationAttributes(frameworkMethod.getMethod(),
						Timed.class.getName());
		if (annAttrs == null) {
			return 0;
		} else {
			long millis = annAttrs.<Long> getNumber("millis").longValue();
			return millis > 0 ? millis : 0;
		}
	}

	/**
	 * Wraps the {@link Statement} returned by the parent implementation with a
	 * {@link RunBeforeTestMethodCallbacks} statement, thus preserving the
	 * default functionality but adding support for the Spring TestContext
	 * Framework.
	 * 
	 * @see RunBeforeTestMethodCallbacks
	 */
	@Override
	protected Statement withBefores(FrameworkMethod frameworkMethod,
			Object testInstance, Statement statement) {
		Statement junitBefores = super.withBefores(frameworkMethod,
				testInstance, statement);
		return new RunBeforeTestMethodCallbacks(junitBefores, testInstance,
				frameworkMethod.getMethod(), getTestContextManager());
	}

	/**
	 * Wraps the {@link Statement} returned by the parent implementation with a
	 * {@link RunAfterTestMethodCallbacks} statement, thus preserving the
	 * default functionality but adding support for the Spring TestContext
	 * Framework.
	 * 
	 * @see RunAfterTestMethodCallbacks
	 */
	@Override
	protected Statement withAfters(FrameworkMethod frameworkMethod,
			Object testInstance, Statement statement) {
		Statement junitAfters = super.withAfters(frameworkMethod, testInstance,
				statement);
		return new RunAfterTestMethodCallbacks(junitAfters, testInstance,
				frameworkMethod.getMethod(), getTestContextManager());
	}

	/**
	 * Supports Spring's {@link Repeat @Repeat} annotation by returning a
	 * {@link SpringRepeat} statement initialized with the configured repeat
	 * count or {@code 1} if no repeat count is configured.
	 * 
	 * @see SpringRepeat
	 */
	protected Statement withPotentialRepeat(FrameworkMethod frameworkMethod,
			Object testInstance, Statement next) {
		Repeat repeatAnnotation = AnnotationUtils.getAnnotation(
				frameworkMethod.getMethod(), Repeat.class);
		int repeat = (repeatAnnotation != null ? repeatAnnotation.value() : 1);
		return new SpringRepeat(next, frameworkMethod.getMethod(), repeat);
	}

}
