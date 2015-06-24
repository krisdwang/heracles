package heracles.data.mybatis.service;

import heracles.data.mybatis.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml" }, inheritLocations = true)
public class UserServiceTest {
	@Resource(name = "userService")
	private UserService userService;

	@Test
	public void testCRUD() {
		// delete
		userService.deleteById(1L);
		userService.deleteById(2L);
		userService.deleteById(101L);
		userService.deleteById(102L);
		userService.deleteById(201L);
		userService.deleteById(202L);
		// save
		User user1 = new User();
		user1.setId(1L);
		user1.setName("user_tom1");
		userService.save(user1);

		User user2 = new User();
		user2.setId(2L);
		user2.setName("user_tom2");
		userService.save(user2);

		User user101 = new User();
		user101.setId(101L);
		user101.setName("user_tom101");
		userService.save(user101);

		User user102 = new User();
		user102.setId(102L);
		user102.setName("user_tom102");
		userService.save(user102);

		User user201 = new User();
		user201.setId(201L);
		user201.setName("user_tom201");
		userService.save(user201);

		User user202 = new User();
		user202.setId(202L);
		user202.setName("user_tom202");
		userService.save(user202);

		// find in slave
		Assert.assertNull(userService.findById(1L));
		Assert.assertNull(userService.findById(2L));
		Assert.assertNull(userService.findById(101L));
		Assert.assertNull(userService.findById(102L));
		Assert.assertNull(userService.findById(201L));
		Assert.assertNull(userService.findById(202L));

		// find in master
		Assert.assertEquals("user_tom1", userService.findByIdWithNewWriteTrans(1L).getName());
		Assert.assertEquals("user_tom2", userService.findByIdWithNewWriteTrans(2L).getName());
		Assert.assertEquals("user_tom101", userService.findByIdWithNewWriteTrans(101L).getName());
		Assert.assertEquals("user_tom102", userService.findByIdWithNewWriteTrans(102L).getName());
		Assert.assertEquals("user_tom201", userService.findByIdWithNewWriteTrans(201L).getName());
		Assert.assertEquals("user_tom202", userService.findByIdWithNewWriteTrans(202L).getName());

		// update
		user1.setName("user_john1");
		userService.update(user1);

		user2.setName("user_john2");
		userService.update(user2);

		user101.setName("user_john101");
		userService.update(user101);

		user102.setName("user_john102");
		userService.update(user102);

		user201.setName("user_john201");
		userService.update(user201);

		user202.setName("user_john202");
		userService.update(user202);

		// find in master
		Assert.assertEquals("user_john1", userService.findByIdWithNewWriteTrans(1L).getName());
		Assert.assertEquals("user_john2", userService.findByIdWithNewWriteTrans(2L).getName());
		Assert.assertEquals("user_john101", userService.findByIdWithNewWriteTrans(101L).getName());
		Assert.assertEquals("user_john102", userService.findByIdWithNewWriteTrans(102L).getName());
		Assert.assertEquals("user_john201", userService.findByIdWithNewWriteTrans(201L).getName());
		Assert.assertEquals("user_john202", userService.findByIdWithNewWriteTrans(202L).getName());

		// delete
		userService.deleteById(1L);
		userService.deleteById(2L);
		userService.deleteById(101L);
		userService.deleteById(102L);
		userService.deleteById(201L);
		userService.deleteById(202L);
	}

	@Test
	public void testFindPage() {
		// delete
		userService.deleteById(1L);
		userService.deleteById(3L);
		userService.deleteById(5L);
		// save
		User user1 = new User();
		user1.setId(1L);
		user1.setName("user_tom");
		userService.save(user1);

		User user3 = new User();
		user3.setId(3L);
		user3.setName("user_tom");
		userService.save(user3);

		User user5 = new User();
		user5.setId(5L);
		user5.setName("user_tom");
		userService.save(user5);

		User user = new User();
		user.setId(1L);
		user.setName("user_tom");

		Pageable pageable = new PageRequest(0, 2, new Sort("id", "name"));

		Page<User> page = userService.findPage(user, pageable);
		Assert.assertEquals(0, page.getTotalElements());
		Assert.assertEquals(0, page.getTotalPages());

		page = userService.findPageWithNewTrans(user, pageable);
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(3, page.getTotalElements());
		Assert.assertFalse(page.hasPreviousPage());
		Assert.assertTrue(page.isFirstPage());
		Assert.assertTrue(page.hasNextPage());
		Assert.assertFalse(page.isLastPage());
		Assert.assertEquals(2, page.getContent().size());
		Assert.assertEquals(0, page.getNumber());
		Assert.assertEquals(2, page.getNumberOfElements());

		page = userService.findPageWithNewTrans(user, pageable);
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(3, page.getTotalElements());
		Assert.assertFalse(page.hasPreviousPage());
		Assert.assertTrue(page.isFirstPage());
		Assert.assertTrue(page.hasNextPage());
		Assert.assertFalse(page.isLastPage());
		Assert.assertEquals(2, page.getContent().size());
		Assert.assertEquals(0, page.getNumber());
		Assert.assertEquals(2, page.getNumberOfElements());

		// delete
		userService.deleteById(1L);
		userService.deleteById(3L);
		userService.deleteById(5L);
	}

	@Test
	public void testFindByIds() {
		// delete
		userService.deleteById(1L);
		userService.deleteById(2L);
		userService.deleteById(3L);
		// save
		User user1 = new User();
		user1.setId(1L);
		user1.setName("user_tom");
		userService.save(user1);

		User user2 = new User();
		user2.setId(2L);
		user2.setName("user_tom");
		userService.save(user2);

		User user3 = new User();
		user3.setId(3L);
		user3.setName("user_tom");
		userService.save(user3);

		Set<Long> ids = new HashSet<Long>();
		ids.add(1L);
		ids.add(2L);
		ids.add(3L);

		Assert.assertEquals(0, userService.findByIds(ids).size());
		Assert.assertEquals(0, userService.findByIds(new ArrayList<Long>(ids)).size());
		Assert.assertEquals(2, userService.findByIdsWithNewTrans(ids).size());

		ids = new HashSet<Long>();
		ids.add(2L);
		ids.add(3L);
		ids.add(4L);

		Assert.assertEquals(1, userService.findByIdsWithNewTrans(ids).size());

		// delete
		userService.deleteById(1L);
		userService.deleteById(2L);
		userService.deleteById(3L);
	}
}
