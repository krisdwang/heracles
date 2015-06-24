package heracles.data.matrixdatasource;

import heracles.core.context.property.PropertyHolder;
import heracles.data.common.model.MatrixDatasourceModel;
import heracles.data.mybatis.entity.Depart;
import heracles.data.mybatis.service.DepartService;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:matrixdatasource/applicationContext-matrixdatasource-test1.xml" }, inheritLocations = true)
public class MatrixDatasourceBeanDefinitionParserTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Resource(name = "departService")
	private DepartService departService;

	@Test
	public void testGetMatrixDatasourceModel() {
		MatrixDatasourceModel matrixDatasourceModel = JSON.parseObject(
				PropertyHolder.getProperties().getProperty("/resource/RDBMS/matrix/dataSource1"),
				MatrixDatasourceModel.class);

		Assert.assertNotNull(matrixDatasourceModel);
		Assert.assertTrue(matrixDatasourceModel.getMatrixName().equals("dataSource1"));
		Assert.assertTrue(matrixDatasourceModel.getGroups().get(0).getGroupName().equals("rwds1"));
		Assert.assertTrue(matrixDatasourceModel.getGroups().get(0).getAtoms().get(0).getAtomName().equals("write01"));

	}

	@Test
	public void testSave() {
		departService.deleteById(201L); // write01,read101,read102

		Depart depart201 = new Depart();
		depart201.setId(201L);
		depart201.setName("depart_tom201");
		departService.save(depart201);

		Assert.assertNull(departService.findById(201L));

		// delete
		departService.deleteById(201L); // write01,read101,read102
	}

	@Test
	public void testCRUD() {
		// delete
		departService.deleteById(1L);
		departService.deleteById(2L);
		departService.deleteById(101L);
		departService.deleteById(102L);
		departService.deleteById(201L);
		departService.deleteById(202L);
		// save
		Depart depart1 = new Depart();
		depart1.setId(1L);
		depart1.setName("depart_tom1");
		departService.save(depart1);

		Depart depart2 = new Depart();
		depart2.setId(2L);
		depart2.setName("depart_tom2");
		departService.save(depart2);

		Depart depart101 = new Depart();
		depart101.setId(101L);
		depart101.setName("depart_tom101");
		departService.save(depart101);

		Depart depart102 = new Depart();
		depart102.setId(102L);
		depart102.setName("depart_tom102");
		departService.save(depart102);

		Depart depart201 = new Depart();
		depart201.setId(201L);
		depart201.setName("depart_tom201");
		departService.save(depart201);

		Depart depart202 = new Depart();
		depart202.setId(202L);
		depart202.setName("depart_tom202");
		departService.save(depart202);

		// find in slave
		Assert.assertNull(departService.findById(1L));
		Assert.assertNull(departService.findById(2L));
		Assert.assertNotNull(departService.findById(101L)); // write02
		Assert.assertNotNull(departService.findById(102L)); // write02
		Assert.assertNull(departService.findById(201L)); // write01,read101,read102
		Assert.assertNull(departService.findById(202L)); // write01,read101,read102

		// update
		depart1.setName("depart_john1");
		departService.update(depart1);

		depart2.setName("depart_john2");
		departService.update(depart2);

		depart101.setName("depart_john101");
		departService.update(depart101);

		depart102.setName("depart_john102");
		departService.update(depart102);

		depart201.setName("depart_john201");
		departService.update(depart201);

		depart202.setName("depart_john202");
		departService.update(depart202);

		// delete
		departService.deleteById(1L);
		departService.deleteById(2L);
		departService.deleteById(101L);
		departService.deleteById(102L);
		departService.deleteById(201L);
		departService.deleteById(202L);
	}

	@Test
	public void testParser() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>rwds1:" + applicationContext.getBean("rwds1").getClass());
		//System.out.println(">>>>>>>>>>>>>>>>>>>>>>rwds2:" + applicationContext.getBean("rwds2").getClass());

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>rwds1_write:" + applicationContext.getBean("rwds1_write").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>rwds1_read0:" + applicationContext.getBean("rwds1_read0").getClass());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>rwds1_read1:" + applicationContext.getBean("rwds1_read1").getClass());

		DataSource dataSource1 = (DataSource) applicationContext.getBean("dataSource1");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>dataSource1:" + dataSource1.getClass());

	}

}
