package heracles.jdbc.matrix;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.service.CustService;
import heracles.jdbc.mybatis.service.NestedService;

import java.math.BigDecimal;
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

/**
 * 使用MyBatis和JDBC两种方式上测试Matrix
 * @author kriswang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-matrix-test.xml" }, inheritLocations = true)
public class MatrixTest {

	@Resource
	private CustService custService;
	@Resource
	private DataSource dataSource;
	@Resource
	private DataSource dataSource1;
	@Resource
	private NestedService nestedService;

	@Test
	public void testCRUD1() {
		Cust cust1 = new Cust();
		cust1.setId(4001L);
		cust1.setName("cust4001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(4001L);
		Assert.assertEquals("cust4001", cust2.getName());

		cust2.setName("cust4011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(4001L);
		Assert.assertEquals("cust4011", cust3.getName());

		custService.deleteById(4001L);

		Cust cust4 = custService.selectById(4001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testCRUD2() {
		Cust cust1 = new Cust();
		cust1.setId(4002L);
		cust1.setName("cust4002");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(4004L);
		cust1.setName("cust4004");
		custService.insert(cust1);

		List<Long> ids = new ArrayList<Long>();
		ids.add(4002L);
		ids.add(4004L);
		List<Cust> custs = custService.selectByIds(ids);
		Assert.assertEquals(2, custs.size());

		custService.deleteById(4002L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(1, custs.size());

		custService.deleteById(4004L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(0, custs.size());
	}

	@Test
	public void testCRUD3() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into cust (id, name) values (4004, 'cust4004')");

		ResultSet rs = statement.executeQuery("select * from cust where id = 4004");
		while (rs.next()) {
			Assert.assertEquals(4004, rs.getInt(1));
			Assert.assertEquals("cust4004", rs.getString(2));
		}

		statement.executeUpdate("update cust set name = 'zhuzhen' where id = 4004");

		rs = statement.executeQuery("select * from cust where id = 4004");
		while (rs.next()) {
			Assert.assertEquals(4004, rs.getInt(1));
			Assert.assertEquals("zhuzhen", rs.getString(2));
		}

		statement.execute("delete from cust where id = 4004");

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD4() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into cust (id, name) values (4005, 'cust4005')");
		statement.execute("insert into cust (id, name) values (4006, 'cust4006')");
		statement.execute("insert into cust (id, name) values (4007, 'cust4007')");
		statement.execute("insert into cust (id, name) values (4008, 'cust4008')");
		statement.execute("insert into cust (id, name) values (4009, 'cust4009')");

		ResultSet rs = statement.executeQuery("select * from cust where id in (4005,4006,4007,4008,4009)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(5, i);

		statement.executeUpdate("update cust set id = 4017 where id = 4005");
		statement.executeUpdate("update cust set id = 4018 where id = 4006");
		statement.executeUpdate("update cust set id = 4019 where id = 4007");
		statement.executeUpdate("update cust set id = 4020 where id = 4008");
		statement.executeUpdate("update cust set id = 4021 where id = 4009");

		rs = statement.executeQuery("select * from cust where id in (4005,4006,4007,4008,4009)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs = statement.executeQuery("select * from cust where id in (4017,4018,4019,4020,4021)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(5, i);

		statement.execute("delete from cust where id = 4017");
		statement.execute("delete from cust where id = 4018");
		statement.execute("delete from cust where id = 4019");
		statement.execute("delete from cust where id = 4020");
		statement.execute("delete from cust where id = 4021");

		rs = statement.executeQuery("select * from cust where id in (4017,4018,4019,4020,4021)");
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
		statement.execute("insert into cust (id, name) values (4011, 'cust4011')");
		statement.execute("insert into cust (id, name) values (4012, 'cust4012')");
		statement.execute("insert into cust (id, name) values (4013, 'cust4013')");
		connection.rollback();

		ResultSet rs = statement.executeQuery("select * from cust where id in (4011,4012,4013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		statement.execute("insert into cust (id, name) values (4011, 'cust4014')");
		statement.execute("insert into cust (id, name) values (4012, 'cust4015')");
		statement.execute("insert into cust (id, name) values (4013, 'cust4016')");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (4011,4012,4013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.execute("delete from cust where id = 4011");
		statement.execute("delete from cust where id = 4012");
		statement.execute("delete from cust where id = 4013");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (4011,4012,4013)");
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
			statement.execute("insert into cust (id, name) values (4011, 'cust4011')");
			statement.execute("insert into cust (id, name, age) values (4012, 'cust4012')");
			statement.execute("insert into cust (id, name) values (4013, 'cust4013')");
		} catch (SQLException e) {
			connection.rollback();
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (4011,4012,4013)");
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
			statement.execute("insert into cust (id, name) values (4011, 'cust4011')");
			statement.execute("insert into cust (id, name) values (4012, 'cust4012')");
			statement.execute("insert into cust (id, name, age) values (4013, 'cust4013')");
		} catch (SQLException e) {
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (4011,4012,4013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(2, i);

		statement.execute("delete from cust where id = 4011");
		statement.execute("delete from cust where id = 4012");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (4011,4012,4013)");
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
	public void testcount1() {
		Cust cust1 = new Cust();
		cust1.setId(4101L);
		cust1.setName("cust4101");
		custService.insert(cust1);

		Cust cust2 = new Cust();
		cust2.setId(4102L);
		cust2.setName("cust4102");
		custService.insert(cust2);

		Cust cust3 = new Cust();
		cust3.setId(4103L);
		cust3.setName("cust4103");
		custService.insert(cust3);

		Cust cust4 = new Cust();
		cust4.setId(4104L);
		cust4.setName("cust4104");
		custService.insert(cust4);

		List<Long> ids = new ArrayList<Long>();
		ids.add(4101L);
		ids.add(4102L);
		ids.add(4103L);
		ids.add(4104L);

		Assert.assertEquals(4, custService.selectCount(ids));
		Assert.assertEquals(4104, custService.selectMax(ids));
		Assert.assertEquals(4101, custService.selectMin(ids));
		Assert.assertEquals(16410, custService.selectSum(ids));
		Assert.assertTrue(custService.selectAvg(ids).equals(new BigDecimal("4102.5000")));

		Assert.assertEquals(4L, custService.selectFuns(ids).get("counts"));
		Assert.assertEquals(4L, custService.selectFuns(ids).get("countf"));
		Assert.assertEquals(4104L, custService.selectFuns(ids).get("maxf"));
		Assert.assertEquals(4101L, custService.selectFuns(ids).get("minf"));
		Assert.assertTrue(custService.selectFuns(ids).get("sumf").equals(new BigDecimal("16410")));
		Assert.assertTrue(custService.selectFuns(ids).get("avgf").equals(new BigDecimal("4102.5000")));

		custService.deleteById(4101L);
		custService.deleteById(4102L);
		custService.deleteById(4103L);
		custService.deleteById(4104L);

		Assert.assertEquals(0, custService.selectCount(ids));
	}
}
