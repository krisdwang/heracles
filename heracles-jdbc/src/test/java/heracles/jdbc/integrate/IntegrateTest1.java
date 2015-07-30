package heracles.jdbc.integrate;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.entity.Order;
import heracles.jdbc.mybatis.service.CustService;
import heracles.jdbc.mybatis.service.NestedService;
import heracles.jdbc.mybatis.service.OrderService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-integrate-test.xml" }, inheritLocations = true)
public class IntegrateTest1 {

	@Resource
	private CustService custService;
	@Resource
	private OrderService orderService;
	@Resource
	private DataSource dataSource;
	@Resource
	private NestedService nestedService;

	@Test
	public void testCRUD1() {
		custService.deleteById(11001L);
		
		Cust cust1 = new Cust();
		cust1.setId(11001L);
		cust1.setName("cust11001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(11001L);
		Assert.assertEquals("cust11001", cust2.getName());

		cust2.setName("cust11011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(11001L);
		Assert.assertEquals("cust11011", cust3.getName());

		custService.deleteById(11001L);

		Cust cust4 = custService.selectById(11001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testCRUD2() {
		custService.deleteById(11022L);
		custService.deleteById(11023L);
		custService.deleteById(11024L);
		custService.deleteById(11025L);
		
		Cust cust1 = new Cust();
		cust1.setId(11022L);
		cust1.setName("cust11002");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(11023L);
		cust1.setName("cust11003");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(11024L);
		cust1.setName("cust11004");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(11025L);
		cust1.setName("cust11005");
		custService.insert(cust1);

		List<Long> ids = new ArrayList<Long>();
		ids.add(11022L);
		ids.add(11023L);
		ids.add(11024L);
		ids.add(11025L);
		List<Cust> custs = custService.selectByIds(ids);
		Assert.assertEquals(4, custs.size());

		custService.deleteById(11022L);
		custs = custService.selectByIds(ids);
		Assert.assertEquals(3, custs.size());

		custService.deleteById(11023L);
		custs = custService.selectByIds(ids);
		Assert.assertEquals(2, custs.size());

		custService.deleteById(11024L);
		custs = custService.selectByIds(ids);
		Assert.assertEquals(1, custs.size());

		custService.deleteById(11025L);
		custs = custService.selectByIds(ids);
		Assert.assertEquals(0, custs.size());
	}

	@Test
	public void testCRUD3() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("delete from cust where id = 11004");
		
		statement.execute("insert into cust (id, name) values (11004, 'cust11004')");

		ResultSet rs = statement.executeQuery("select * from cust where id = 11004");
		while (rs.next()) {
			Assert.assertEquals(11004, rs.getInt(1));
			Assert.assertEquals("cust11004", rs.getString(2));
		}

		statement.executeUpdate("update cust set name = 'zhuzhen' where id = 11004");

		rs = statement.executeQuery("select * from cust where id = 11004");
		while (rs.next()) {
			Assert.assertEquals(11004, rs.getInt(1));
			Assert.assertEquals("zhuzhen", rs.getString(2));
		}

		statement.execute("delete from cust where id = 11004");

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD4() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("delete from cust where id = 11005");
		statement.execute("delete from cust where id = 11006");
		statement.execute("delete from cust where id = 11007");
		statement.execute("delete from cust where id = 11008");
		statement.execute("delete from cust where id = 11009");
		statement.execute("delete from cust where id = 11010");
		statement.execute("delete from cust where id = 11011");
		statement.execute("delete from cust where id = 11012");
		
		statement.execute("insert into cust (id, name) values (11005, 'cust11005')");
		statement.execute("insert into cust (id, name) values (11006, 'cust11006')");
		statement.execute("insert into cust (id, name) values (11007, 'cust11007')");
		statement.execute("insert into cust (id, name) values (11008, 'cust11008')");

		ResultSet rs = statement.executeQuery("select * from cust where id in (11005,11006,11007,11008)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(4, i);

		statement.executeUpdate("update cust set id = 11009 where id = 11005");
		statement.executeUpdate("update cust set id = 11010 where id = 11006");
		statement.executeUpdate("update cust set id = 11011 where id = 11007");
		statement.executeUpdate("update cust set id = 11012 where id = 11008");

		rs = statement.executeQuery("select * from cust where id in (11005,11006,11007,11008)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs = statement.executeQuery("select * from cust where id in (11009,11010,11011,11012)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(4, i);

		statement.execute("delete from cust where id = 11005");
		statement.execute("delete from cust where id = 11006");
		statement.execute("delete from cust where id = 11007");
		statement.execute("delete from cust where id = 11008");
		statement.execute("delete from cust where id = 11009");
		statement.execute("delete from cust where id = 11010");
		statement.execute("delete from cust where id = 11011");
		statement.execute("delete from cust where id = 11012");

		rs = statement.executeQuery("select * from cust where id in (11005,11006,11007,11008)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD5() throws SQLException {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		Statement statement = connection.createStatement();
		
		statement.execute("delete from cust where id = 11011");
		statement.execute("delete from cust where id = 11012");
		statement.execute("delete from cust where id = 11013");
		
		statement.execute("insert into cust (id, name) values (11011, 'cust11011')");
		statement.execute("insert into cust (id, name) values (11012, 'cust11012')");
		statement.execute("insert into cust (id, name) values (11013, 'cust11013')");
		connection.rollback();

		ResultSet rs = statement.executeQuery("select * from cust where id in (11011,11012,11013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		statement.execute("insert into cust (id, name) values (11011, 'cust11014')");
		statement.execute("insert into cust (id, name) values (11012, 'cust11015')");
		statement.execute("insert into cust (id, name) values (11013, 'cust11016')");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (11011,11012,11013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.execute("delete from cust where id = 11011");
		statement.execute("delete from cust where id = 11012");
		statement.execute("delete from cust where id = 11013");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (11011,11012,11013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD6() throws SQLException {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		Statement statement = connection.createStatement();
		try {
			statement.execute("insert into cust (id, name) values (11011, 'cust11011')");
			statement.execute("insert into cust (id, name, age) values (11012, 'cust11012')");
			statement.execute("insert into cust (id, name) values (11013, 'cust11013')");
		} catch (SQLException e) {
			connection.rollback();
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (11011,11012,11013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD7() throws SQLException {
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(true);
		Statement statement = connection.createStatement();
		try {
			statement.execute("insert into cust (id, name) values (11011, 'cust11011')");
			statement.execute("insert into cust (id, name) values (11012, 'cust11012')");
			statement.execute("insert into cust (id, name, age) values (11013, 'cust11013')");
		} catch (SQLException e) {
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (11011,11012,11013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(2, i);

		statement.execute("delete from cust where id = 11011");
		statement.execute("delete from cust where id = 11012");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (11011,11012,11013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs.close();
		statement.close();
		connection.close();
	}

	@Test(expected = UncategorizedSQLException.class)
	public void testCRUD8() {
		nestedService.commit();
		nestedService.noTrans();
		nestedService.readOnly();
		nestedService.rollback();
	}

	@Test(expected = UncategorizedSQLException.class)
	public void testCRUD9() {
		nestedService.twoTableCommit();
		nestedService.twoTableNoTrans();
		nestedService.twoTableRollback();
	}

	@Test
	public void testCRUD10() {
		nestedService.twoTableRequiresNew();
	}

	@Test
	public void testCRUD11() {
		nestedService.twoTableNested();
	}

	@Test
	public void testCRUD88() {
		custService.deleteById(5L);
		orderService.deleteById(5L);
		
		Cust cust = new Cust();
		cust.setId(5L);
		cust.setName("cust5L");
		custService.insert(cust);

		Cust cust2 = custService.selectById(5L);
		Assert.assertEquals("cust5L", cust2.getName());

		Order order = new Order();
		order.setId(5L);
		order.setName("order5L");
		orderService.insert(order);

		Order order2 = orderService.selectById(5L);
		Assert.assertEquals("order5L", order2.getName());

		Cust cust7 = custService.selectByJoin();
		Assert.assertEquals("cust5L", cust7.getName());

		custService.deleteById(5L);
		orderService.deleteById(5L);

		Cust cust4 = custService.selectById(5L);
		Assert.assertNull(cust4);

		Order order4 = orderService.selectById(5L);
		Assert.assertNull(order4);
	}
}
