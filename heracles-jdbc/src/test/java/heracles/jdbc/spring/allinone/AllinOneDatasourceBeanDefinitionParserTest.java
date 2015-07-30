package heracles.jdbc.spring.allinone;

import heracles.core.context.property.PropertyHolder;
import heracles.jdbc.atom.AtomDataSource;
import heracles.jdbc.group.GroupDataSource;
import heracles.jdbc.matrix.MatrixDataSource;
import heracles.jdbc.matrix.model.RuleListModel;
import heracles.jdbc.matrix.model.RuleModel;
import heracles.jdbc.mybatis.entity.Cust;
import heracles.jdbc.mybatis.service.CustService;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-applicationContext-allinone-test1.xml" }, inheritLocations = true)
public class AllinOneDatasourceBeanDefinitionParserTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Resource
	private CustService custService;

	@Resource
	private RuleListModel ruleList;

	@Resource
	private AtomDataSource ds1_atomread2;

	@Resource
	private AtomDataSource ds2_atomwrite;

	@Resource
	private AtomDataSource ds1_atomwrite;

	@Test
	public void testCRUD1() {
		custService.deleteById(11001L);

		Cust cust1 = new Cust();
		cust1.setId(11001L);
		cust1.setName("cust11001");
		custService.insert(cust1);

		Cust cust2 = custService.selectById(11001L);
		Assert.assertEquals("cust11001", cust2.getName());

		cust1.setName("cust11011rrr");
		custService.update(cust1);

		Cust cust3 = custService.selectById(11001L);
		Assert.assertEquals("cust11011rrr", cust3.getName());

		custService.deleteById(11001L);

		Cust cust4 = custService.selectById(11001L);
		Assert.assertNull(cust4);
	}

	@Test
	public void testParser() {
		System.out.println(">>>>>>>>>>>>>>>>"
				+ PropertyHolder.getProperties().getProperty("/resource/RDBMS/matrix/matrixdataSource"));

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1:" + applicationContext.getBean("ds1").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_lb:" + applicationContext.getBean("ds1_lb").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_write:" + applicationContext.getBean("ds1_write").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_atomwrite:"
				+ applicationContext.getBean("ds1_atomwrite").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_read2:" + applicationContext.getBean("ds1_read2").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_read1:" + applicationContext.getBean("ds1_read1").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_atomread2:"
				+ applicationContext.getBean("ds1_atomread2").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds1_atomread1:"
				+ applicationContext.getBean("ds1_atomread1").getClass());

		// ///////////////////////////

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2:" + applicationContext.getBean("ds2").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_lb:" + applicationContext.getBean("ds2_lb").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_write:" + applicationContext.getBean("ds2_write").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_atomwrite:"
				+ applicationContext.getBean("ds2_atomwrite").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_read2:" + applicationContext.getBean("ds2_read2").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_read1:" + applicationContext.getBean("ds2_read1").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_atomread2:"
				+ applicationContext.getBean("ds2_atomread2").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ds2_atomread1:"
				+ applicationContext.getBean("ds2_atomread1").getClass());

		MatrixDataSource dataSource = (MatrixDataSource) applicationContext.getBean("dataSource");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>dataSource:" + dataSource.getClass());

		RuleListModel ruleList = (RuleListModel) applicationContext.getBean("ruleList");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>ruleList:" + ruleList.getClass());

		RuleModel rule0 = (RuleModel) applicationContext.getBean("rule0");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>rule0:" + rule0.getClass());

		Map<String, DataSource> dataSources = dataSource.getDataSources();
		Set<Entry<String, DataSource>> set = dataSources.entrySet();
		for (Entry<String, DataSource> entry : set) {
			GroupDataSource ds = (GroupDataSource) entry.getValue();
			Map<String, DataSource> targetDataSources = ds.getTargetDataSources();
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>entry.getKey():" + entry.getKey() + ">>>>>>>>>>>>>>"
					+ targetDataSources.size());
			Set<Entry<String, DataSource>> targetSet = targetDataSources.entrySet();
			for (Entry<String, DataSource> tEntry : targetSet) {
				AtomDataSource value = (AtomDataSource) tEntry.getValue();

				try {
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>tEntry.getKey():" + tEntry.getKey() + ">>>>>>>>>>>>>>"
							+ value.getClass() + ">>>>>>>>>>>>>>" + value.getTargetDataSource().getClass()
							+ ">>>>>>>>>>" + value.getTargetDataSource().getConnection().getMetaData().getURL());
				} catch (SQLException e) {
				}
			}
		}

		// try {
		// Connection connection = dataSource.getConnection();
		// Statement statement = connection.createStatement();
		// statement.execute("insert into cust (id, name) values (8004, 'cust8004')");
		//
		// ResultSet rs = statement.executeQuery("select * from cust where id = 8004");
		// while (rs.next()) {
		// Assert.assertEquals(8004, rs.getInt(1));
		// Assert.assertEquals("cust8004", rs.getString(2));
		// }
		//
		// statement.execute("delete from cust where id = 8004");
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

	}

}
