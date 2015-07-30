package heracles.jdbc.integrate;

import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.service.CustService;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
@ContextConfiguration(locations = { "classpath:spring-group-atom.xml" }, inheritLocations = true)
public class IntegrateTest3 {

	@Resource
	private CustService custService;
	@Resource
	private DataSource dataSource;

	@Test
	public void testCRUD1() {
		custService.deleteById(77001L);
		
		Cust cust1 = new Cust();
		cust1.setId(77001L);
		cust1.setName("cust7001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(77001L);
		Assert.assertEquals("cust7001", cust2.getName());

		cust2.setName("cust7011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(77001L);
		Assert.assertEquals("cust7011", cust3.getName());

		custService.deleteById(77001L);

		Cust cust4 = custService.selectById(77001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testCRUD2() {
		Cust cust1 = new Cust();
		cust1.setId(7002L);
		cust1.setName("cust7002");
		custService.insert(cust1);

		cust1 = new Cust();
		cust1.setId(7003L);
		cust1.setName("cust7003");
		custService.insert(cust1);

		List<Long> ids = new ArrayList<Long>();
		ids.add(7002L);
		ids.add(7003L);
		List<Cust> custs = custService.selectByIds(ids);
		Assert.assertEquals(2, custs.size());

		custService.deleteById(7002L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(1, custs.size());

		custService.deleteById(7003L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(0, custs.size());
	}

	@Test
	public void testCRUD3() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("delete from cust where id = 7004");
		statement.execute("insert into cust (id, name) values (7004, 'cust7004')");

		ResultSet rs = statement.executeQuery("select * from cust where id = 7004");
		while (rs.next()) {
			Assert.assertEquals(7004, rs.getInt(1));
			Assert.assertEquals("cust7004", rs.getString(2));
		}
		
		statement.execute("delete from cust where id = 7004");
	}
	
	@Test
	public void testCRUD4() throws SQLException {
		String sql = "insert into cust (id, name) values (?, ?)";
		Connection connection = dataSource.getConnection();
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setInt(1, 7005);
		statement.setString(2, "cust7005");
		statement.execute();

		ResultSet rs = statement.executeQuery("select * from cust where id = 7005");
		while (rs.next()) {
			Assert.assertEquals(7005, rs.getInt(1));
			Assert.assertEquals("cust7005", rs.getString(2));
		}
		
		statement.execute("delete from cust where id = 7005");
	}
}
