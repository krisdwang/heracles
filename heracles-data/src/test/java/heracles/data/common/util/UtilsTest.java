package heracles.data.common.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class UtilsTest {

	@Test
	public void testAntIsMatch() {
		Assert.assertTrue(heracles.data.spring.util.Utils.isMatch("delete*", "deleteById"));
		Assert.assertTrue(heracles.data.spring.util.Utils.isMatch("d*", "db1_w"));
		Assert.assertFalse(heracles.data.spring.util.Utils.isMatch("w*", "db1_w"));
	}

	@Test
	public void testIsMatch() {
		Assert.assertTrue(Utils.isMatch("deleteById", "delete*"));
		Assert.assertFalse(Utils.isMatch(null, "delete*"));
		Assert.assertFalse(Utils.isMatch("deleteById", null));
	}

	@Test
	public void testIsRead() {
		Assert.assertTrue(Utils.isRead("read"));
		Assert.assertFalse(Utils.isRead(null));
	}

	@Test
	public void testIsWrite() {
		Assert.assertTrue(Utils.isWrite("write"));
		Assert.assertFalse(Utils.isWrite(null));
	}

	@Test
	public void testGetMatchTableName() {
		Assert.assertEquals("user", Utils.getMatchTableName("agfasdfa$[user]$asdfasdfas"));
		Assert.assertEquals("user", Utils.getMatchTableName("agfasdfa$[user]$asdf$[order]$asdfas"));
	}

	@Test
	public void testGetMatchTableNameWithNull() {
		Assert.assertNull(Utils.getMatchTableName("agfasdfaasdfasdfas"));
	}

	@Test(expected = RuntimeException.class)
	public void testGetMatchTableNameWithException2() {
		Assert.assertEquals("user", Utils.getMatchTableName(null));
	}

	@Test(expected = RuntimeException.class)
	public void testGetMatchTableNameWithException3() {
		Assert.assertEquals("user", Utils.getMatchTableName(""));
	}

	@Test(expected = RuntimeException.class)
	public void testGetMatchTableNameWithException4() {
		Assert.assertEquals("user", Utils.getMatchTableName("  "));
	}

	@Test(expected = RuntimeException.class)
	public void testGetRegexTableNameWithException1() {
		Assert.assertEquals("user", Utils.getRegexTableName(null));
	}

	@Test(expected = RuntimeException.class)
	public void testGetRegexTableNameWithException2() {
		Assert.assertEquals("user", Utils.getRegexTableName(""));
	}

	@Test(expected = RuntimeException.class)
	public void testGetRegexTableNameWithException3() {
		Assert.assertEquals("user", Utils.getRegexTableName(" "));
	}

	@Test
	public void testGetRegexTableName() {
		Assert.assertEquals("\\$\\[user\\]\\$", Utils.getRegexTableName("user"));
	}

	@Test
	public void testGetShardingTableName() {
		Assert.assertEquals("agfasdfauser1asdfasdfas",
				Utils.getShardingTableName("user", "user1", "agfasdfa$[user]$asdfasdfas"));
	}

	@Test(expected = RuntimeException.class)
	public void testGetShardingTableNameWithException1() {
		Assert.assertEquals("agfasdfauser1asdfasdfas",
				Utils.getShardingTableName(null, "user1", "agfasdfa$[user]$asdfasdfas"));
	}

	@Test(expected = RuntimeException.class)
	public void testGetShardingTableNameWithException2() {
		Assert.assertEquals("agfasdfauser1asdfasdfas",
				Utils.getShardingTableName("user", "", "agfasdfa$[user]$asdfasdfas"));
	}

	@Test(expected = RuntimeException.class)
	public void testGetShardingTableNameWithException3() {
		Assert.assertEquals("agfasdfauser1asdfasdfas", Utils.getShardingTableName("user", "", "  "));
	}

	@Test
	public void testGetOrderBy() {
		Assert.assertNull(Utils.getOrderBy(null));
		Sort sort = new Sort(Direction.DESC, "id", "name");
		Assert.assertEquals("id desc, name desc", Utils.getOrderBy(sort));

		sort = new Sort("id");
		Assert.assertEquals("id", Utils.getOrderBy(sort));

		Order order1 = new Order(Direction.DESC, "id");
		Order order2 = new Order(Direction.ASC, "name");
		List<Order> orders = new ArrayList<Order>();
		orders.add(order1);
		orders.add(order2);
		sort = new Sort(orders);
		Assert.assertEquals("id desc, name", Utils.getOrderBy(sort));

		order1 = new Order(Direction.ASC, "id");
		order2 = new Order(Direction.DESC, "name");
		orders = new ArrayList<Order>();
		orders.add(order1);
		orders.add(order2);
		sort = new Sort(orders);
		Assert.assertEquals("id, name desc", Utils.getOrderBy(sort));

		sort = new Sort(Direction.DESC, "name");
		Assert.assertEquals("name desc", Utils.getOrderBy(sort));

		sort = new Sort(Direction.ASC, "id");
		Assert.assertEquals("id", Utils.getOrderBy(sort));
	}

	@Test(expected = RuntimeException.class)
	public void testTrimSqlWithException1() {
		Utils.trimSql(null);
	}

	@Test(expected = RuntimeException.class)
	public void testTrimSqlWithException2() {
		Utils.trimSql("");
	}

	@Test(expected = RuntimeException.class)
	public void testTrimSqlWithException3() {
		Utils.trimSql(" ");
	}

	@Test
	public void testTrimSql() {
		Assert.assertEquals("abc adfas", Utils.trimSql("abc\n\tadfas\t\n  "));
		Assert.assertEquals("abc adfas", Utils.trimSql("abc\tadfas\t "));
		Assert.assertEquals("abc adfas", Utils.trimSql("abc\nadfas\n  "));
	}

	@Test
	public void testGetSpelValue() {
		Object[] objects = { 1L, 2L, 3L };
		String[] paraNames = { "one", "two", "three" };
		Assert.assertEquals(1L, ((Long) Utils.getSpelValue(objects, paraNames, "#one", null)).longValue());
		Assert.assertEquals(2L, ((Long) Utils.getSpelValue(objects, paraNames, "#two", null)).longValue());
		Assert.assertEquals(3L, ((Long) Utils.getSpelValue(objects, paraNames, "#three", null)).longValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSpelValueWithException1() {
		Object[] objects = { 1L, 2L, 3L, 4L };
		String[] paraNames = { "one", "two", "three" };
		Utils.getSpelValue(objects, paraNames, "#one", null);
	}

	@Test(expected = RuntimeException.class)
	public void testGetSpelValueWithException2() {
		Utils.getSpelValue(null, null, "#one", null);
	}

	@Test(expected = RuntimeException.class)
	public void testGetSpelValueWithException3() {
		Object[] objects = {};
		Utils.getSpelValue(objects, null, "#one", null);
	}

	@Test(expected = RuntimeException.class)
	public void testGetSpelValueWithException4() {
		Object[] objects = { 1L, 2L, 3L, 4L };
		String[] paraNames = {};
		Utils.getSpelValue(objects, paraNames, "#one", null);
	}

	@Test(expected = RuntimeException.class)
	public void testGetSpelValueWithException5() {
		Object[] objects = { 1L, 2L, 3L, 4L };
		String[] paraNames = { "one", "two", "three" };
		Utils.getSpelValue(objects, paraNames, "", null);
	}
}
