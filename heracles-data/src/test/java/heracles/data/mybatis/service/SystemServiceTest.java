package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.User;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" }, inheritLocations = true)
public class SystemServiceTest {

	@Resource(name = "systemService")
	private SystemService systemService;

	@Test
	public void testSave() {
		User user = new User();
		user.setId((long) (Math.random() * 100));
		user.setName("system1_tom1");
		user = systemService.save(user);
		Assert.assertEquals("system1_tom1", user.getName());
	}

	@Test
	public void testSaveWithNewReadTrans() {
		User user = new User();
		user.setId((long) (Math.random() * 100));
		user.setName("system1_tom1");
		user = systemService.saveWithNewReadTrans(user);
		Assert.assertNull(user);
	}

	@Test
	public void testSaveWithNewWrtieTrans() {
		User user1 = new User();
		user1.setId((long) (Math.random() * 100));
		user1.setName("system1_tom1");

		User user2 = new User();
		user2.setId(203L);
		user2.setName("system1_tom203");

		systemService.saveWithNewWriteTrans(user1, user2);
	}

	@Test
	public void testSaveWithNewTransException() {
		User user1 = new User();
		user1.setId((long) (Math.random() * 100));
		user1.setName("system1_tom1");

		User user2 = new User();
		user2.setId(222L);
		user2.setName("system1_tom222system1_tomsystem1_tomsystem1_tomsystem1_tom");

		systemService.saveWithNewTransException(user1, user2);

		User user = systemService.findByIdWithNewWriteTrans(user1.getId());
		Assert.assertEquals("system1_tom1", user.getName());

		user = systemService.findByIdWithNewWriteTrans(222L);
		Assert.assertNull(user);

		systemService.deleteById(user1.getId());
	}
}
