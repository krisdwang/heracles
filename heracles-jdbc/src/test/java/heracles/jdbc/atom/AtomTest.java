package heracles.jdbc.atom;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.service.CustService;
import heracles.jdbc.mybatis.service.NestedService;

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
@ContextConfiguration(locations = { "classpath:spring-atom-test.xml" }, inheritLocations = true)
public class AtomTest {

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
		cust1.setId(1001L);
		cust1.setName("cust1001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(1001L);
		Assert.assertEquals("cust1001", cust2.getName());

		cust2.setName("cust1011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(1001L);
		Assert.assertEquals("cust1011", cust3.getName());

		custService.deleteById(1001L);

		Cust cust4 = custService.selectById(1001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testCRUD2() {
		Cust cust1 = new Cust();
		cust1.setId(1002L);
		cust1.setName("cust1002");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(1003L);
		cust1.setName("cust1003");
		custService.insert(cust1);

		List<Long> ids = new ArrayList<Long>();
		ids.add(1002L);
		ids.add(1003L);
		List<Cust> custs = custService.selectByIds(ids);
		Assert.assertEquals(2, custs.size());

		custService.deleteById(1002L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(1, custs.size());

		custService.deleteById(1003L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(0, custs.size());
	}

	@Test
	public void testCRUD3() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("delete from cust where id = 1004");
		
		statement.execute("insert into cust (id, name) values (1004, 'cust1004')");

		ResultSet rs = statement.executeQuery("select * from cust where id = 1004");
		while (rs.next()) {
			Assert.assertEquals(1004, rs.getInt(1));
			Assert.assertEquals("cust1004", rs.getString(2));
		}

		statement.executeUpdate("update cust set name = 'zhuzhen' where id = 1004");

		rs = statement.executeQuery("select * from cust where id = 1004");
		while (rs.next()) {
			Assert.assertEquals(1004, rs.getInt(1));
			Assert.assertEquals("zhuzhen", rs.getString(2));
		}

		statement.execute("delete from cust where id = 1004");

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD4() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into cust (id, name) values (1005, 'cust1005')");
		statement.execute("insert into cust (id, name) values (1006, 'cust1006')");
		statement.execute("insert into cust (id, name) values (1007, 'cust1007')");

		ResultSet rs = statement.executeQuery("select * from cust where id in (1005,1006,1007)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.executeUpdate("update cust set id = 1009 where id = 1005");
		statement.executeUpdate("update cust set id = 1010 where id = 1006");
		statement.executeUpdate("update cust set id = 1011 where id = 1007");

		rs = statement.executeQuery("select * from cust where id in (1005,1006,1007)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs = statement.executeQuery("select * from cust where id in (1009,1010,1011)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.execute("delete from cust where id = 1009");
		statement.execute("delete from cust where id = 1010");
		statement.execute("delete from cust where id = 1011");

		rs = statement.executeQuery("select * from cust where id in (1009,1010,1011)");
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
		statement.execute("insert into cust (id, name) values (1011, 'cust1011')");
		statement.execute("insert into cust (id, name) values (1012, 'cust1012')");
		statement.execute("insert into cust (id, name) values (1013, 'cust1013')");
		connection.rollback();

		ResultSet rs = statement.executeQuery("select * from cust where id in (1011,1012,1013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		statement.execute("insert into cust (id, name) values (1011, 'cust1014')");
		statement.execute("insert into cust (id, name) values (1012, 'cust1015')");
		statement.execute("insert into cust (id, name) values (1013, 'cust1016')");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (1011,1012,1013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.execute("delete from cust where id = 1011");
		statement.execute("delete from cust where id = 1012");
		statement.execute("delete from cust where id = 1013");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (1011,1012,1013)");
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
			statement.execute("insert into cust (id, name) values (1011, 'cust1011')");
			statement.execute("insert into cust (id, name, age) values (1012, 'cust1012')");
			statement.execute("insert into cust (id, name) values (1013, 'cust1013')");
		} catch (SQLException e) {
			connection.rollback();
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (1011,1012,1013)");
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
			statement.execute("insert into cust (id, name) values (1011, 'cust1011')");
			statement.execute("insert into cust (id, name) values (1012, 'cust1012')");
			statement.execute("insert into cust (id, name, age) values (1013, 'cust1013')");
		} catch (SQLException e) {
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (1011,1012,1013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(2, i);

		statement.execute("delete from cust where id = 1011");
		statement.execute("delete from cust where id = 1012");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (1011,1012,1013)");
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
}
