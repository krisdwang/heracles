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
@ContextConfiguration(locations = { "classpath:spring-matrix.xml" }, inheritLocations = true)
public class IntegrateTest4 {

	@Resource
	private CustService custService;
	@Resource
	private DataSource dataSource;

	@Test
	public void testCRUD1() {
		
		custService.deleteById(8001L);
		
		Cust cust1 = new Cust();
		cust1.setId(8001L);
		cust1.setName("cust8001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(8001L);
		Assert.assertEquals("cust8001", cust2.getName());

		cust2.setName("cust8011");
		custService.update(cust2);

		Cust cust3 = custService.selectById(8001L);
		Assert.assertEquals("cust8011", cust3.getName());

		custService.deleteById(8001L);

		Cust cust4 = custService.selectById(8001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testCRUD2() {
		custService.deleteById(8002L);
		custService.deleteById(8003L);
		
		Cust cust1 = new Cust();
		cust1.setId(8002L);
		cust1.setName("cust1002");
		custService.insert(cust1);

		Cust cust2 = new Cust();
		cust2 = new Cust();
		cust2.setId(8003L);
		cust2.setName("cust1003");
		custService.insert(cust2);

		List<Long> ids = new ArrayList<Long>();
		ids.add(8002L);
		ids.add(8003L);
		List<Cust> custs = custService.selectByIds(ids);
		Assert.assertEquals(2, custs.size());

		custService.deleteById(8002L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(1, custs.size());

		custService.deleteById(8003L);

		custs = custService.selectByIds(ids);
		Assert.assertEquals(0, custs.size());
	}

	@Test
	public void testCRUD3() throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement statement = connection.createStatement();
		statement.execute("insert into cust (id, name) values (8004, 'cust8004')");

		ResultSet rs = statement.executeQuery("select * from cust where id = 8004");
		while (rs.next()) {
			Assert.assertEquals(8004, rs.getInt(1));
			Assert.assertEquals("cust8004", rs.getString(2));
		}
		
		statement.execute("delete from cust where id = 8004");
	}
	
	@Test
	public void testCRUD4() throws SQLException {
		String sql = "insert into cust (id, name) values (?, ?)";
		Connection connection = dataSource.getConnection();
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setInt(1, 8005);
		statement.setString(2, "cust8005");
		statement.execute();

		ResultSet rs = statement.executeQuery("select * from cust where id = 8005");
		while (rs.next()) {
			Assert.assertEquals(8005, rs.getInt(1));
			Assert.assertEquals("cust8005", rs.getString(2));
		}
		
		statement.execute("delete from cust where id = 8005");
	}
}
