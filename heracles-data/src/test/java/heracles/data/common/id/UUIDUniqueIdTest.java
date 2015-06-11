package heracles.data.common.id;

import heracles.data.common.id.IdGenerator;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" }, inheritLocations = true)
public class UUIDUniqueIdTest {

	@Resource(name = "uuidUniqueId")
	private IdGenerator idGenerator;

	@Test
	public void test() {
		Assert.assertNotNull(idGenerator.getId());
	}

}
