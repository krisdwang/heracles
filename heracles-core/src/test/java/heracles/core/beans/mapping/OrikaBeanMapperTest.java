package heracles.core.beans.mapping;

import static org.junit.Assert.assertTrue;
import heracles.core.beans.mapping.orika.OrikaBeanMapper;
import ma.glasnost.orika.MappingContext.Factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-beanMapper.xml" })
@ActiveProfiles(value = "test")
public class OrikaBeanMapperTest {
	
	@Autowired
	private OrikaBeanMapper orikaBeanMapper;
	
	@Test
	public void testMap() {
		Factory factory = new Factory();
		Object sourceObject = new Object();
		Object destinationObject = null;
		destinationObject = orikaBeanMapper.map(sourceObject, Object.class);
		assertTrue(destinationObject != null);
		orikaBeanMapper.map(sourceObject, destinationObject);
		assertTrue(destinationObject != null);
		destinationObject = orikaBeanMapper.map(sourceObject, Object.class, factory.getContext());
		assertTrue(destinationObject != null);
    }
}
