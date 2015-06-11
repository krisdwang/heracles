package heracles.unit;

import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

@RunWith(DbInitRunner.class)
@TestExecutionListeners({ ServletTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		DbInitTestExecutionListener.class,
		InitSpringProfilesExecutionListener.class})
@CreateDb
public abstract class BaseDbTest extends BaseTest {

}
