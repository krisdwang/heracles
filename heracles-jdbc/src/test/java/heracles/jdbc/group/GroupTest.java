package heracles.jdbc.group;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.service.CustService;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-group-test.xml" }, inheritLocations = true)
public class GroupTest {

	@Resource
	private CustService custService;
	@Resource
	private DataSource dataSource;
	@Resource
	private DataSource dataSource1;

	@Test
	public void testCRUD1() {
		custService.deleteById(2001L);
		
		Cust cust1 = new Cust();
		cust1.setId(2001L);
		cust1.setName("cust2001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(2001L);
		Assert.assertEquals("cust2001", cust2.getName());

		cust2.setName("cust2011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(2001L);
		Assert.assertEquals("cust2011", cust3.getName());

		custService.deleteById(2001L);

		Cust cust4 = custService.selectById(2001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testCRUD2() {
		Cust cust1 = new Cust();
		cust1.setId(2002L);
		cust1.setName("cust2002");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(2003L);
		cust1.setName("cust2003");
		custService.insert(cust1);

		List<Long> ids = new ArrayList<Long>();
		ids.add(2002L);
		ids.add(2003L);
		List<Cust> custs = custService.selectByIds(ids);
		Assert.assertEquals(2, custs.size());

		custService.deleteById(2002L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(1, custs.size());

		custService.deleteById(2003L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(0, custs.size());
	}

	@Test
	public void testCRUD3() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into cust (id, name) values (2004, 'cust2004')");

		ResultSet rs = statement.executeQuery("select * from cust where id = 2004");
		while (rs.next()) {
			Assert.assertEquals(2004, rs.getInt(1));
			Assert.assertEquals("cust2004", rs.getString(2));
		}

		statement.executeUpdate("update cust set name = 'zhuzhen' where id = 2004");

		rs = statement.executeQuery("select * from cust where id = 2004");
		while (rs.next()) {
			Assert.assertEquals(2004, rs.getInt(1));
			Assert.assertEquals("zhuzhen", rs.getString(2));
		}

		statement.execute("delete from cust where id = 2004");

		rs.close();
		statement.close();
		connection.close();
	}

	@Test
	public void testCRUD4() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into cust (id, name) values (2005, 'cust2005')");
		statement.execute("insert into cust (id, name) values (2006, 'cust2006')");
		statement.execute("insert into cust (id, name) values (2007, 'cust2007')");

		ResultSet rs = statement.executeQuery("select * from cust where id in (2005,2006,2007)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.executeUpdate("update cust set id = 2009 where id = 2005");
		statement.executeUpdate("update cust set id = 2010 where id = 2006");
		statement.executeUpdate("update cust set id = 2011 where id = 2007");

		rs = statement.executeQuery("select * from cust where id in (2005,2006,2007)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs = statement.executeQuery("select * from cust where id in (2009,2010,2011)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.execute("delete from cust where id = 2009");
		statement.execute("delete from cust where id = 2010");
		statement.execute("delete from cust where id = 2011");

		rs = statement.executeQuery("select * from cust where id in (2009,2010,2011)");
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
		statement.execute("insert into cust (id, name) values (2011, 'cust2011')");
		statement.execute("insert into cust (id, name) values (2012, 'cust2012')");
		statement.execute("insert into cust (id, name) values (2013, 'cust2013')");
		connection.rollback();

		ResultSet rs = statement.executeQuery("select * from cust where id in (2011,2012,2013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		statement.execute("insert into cust (id, name) values (2011, 'cust2014')");
		statement.execute("insert into cust (id, name) values (2012, 'cust2015')");
		statement.execute("insert into cust (id, name) values (2013, 'cust2016')");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (2011,2012,2013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(3, i);

		statement.execute("delete from cust where id = 2011");
		statement.execute("delete from cust where id = 2012");
		statement.execute("delete from cust where id = 2013");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (2011,2012,2013)");
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
			statement.execute("insert into cust (id, name) values (2011, 'cust2011')");
			statement.execute("insert into cust (id, name, age) values (2012, 'cust2012')");
			statement.execute("insert into cust (id, name) values (2013, 'cust2013')");
		} catch (SQLException e) {
			connection.rollback();
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (2011,2012,2013)");
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
			statement.execute("insert into cust (id, name) values (2011, 'cust2011')");
			statement.execute("insert into cust (id, name) values (2012, 'cust2012')");
			statement.execute("insert into cust (id, name, age) values (2013, 'cust2013')");
		} catch (SQLException e) {
		}

		ResultSet rs = statement.executeQuery("select * from cust where id in (2011,2012,2013)");
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(2, i);

		statement.execute("delete from cust where id = 2011");
		statement.execute("delete from cust where id = 2012");
		connection.commit();

		rs = statement.executeQuery("select * from cust where id in (2011,2012,2013)");
		i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(0, i);

		rs.close();
		statement.close();
		connection.close();
	}
}
