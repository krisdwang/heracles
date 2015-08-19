package heracles.core.zookeeper.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import heracles.core.zookeeper.PropertyChangedHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-context.xml" })
@ActiveProfiles(value="test")
public class PropertyChangedDispatcherTest {
	
	private PropertyChangedDispatcher propertyChangedDispatcher;
	
	private NodeCache nodeCache;  
	
	private void initClient() {
		nodeCache = mock(NodeCache.class);
		try {
			//when(nodeCache.getCurrentData()).thenReturn(new ChildData("/"+anyString(), (Stat)any(), (byte[])any()));
			when(nodeCache.getCurrentData()).thenReturn(mock(ChildData.class));
			when(nodeCache.getCurrentData().getData()).thenReturn("abc=def".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	public void testNodeChanged() {
		initClient();
		List<PropertyChangedHandler> handlers = new ArrayList<PropertyChangedHandler>();
		PropertyChangedHandler handler = new PropertyChangedHandler() {
			@Override
			public void execute(Properties oldProps, Properties newProps) {
				
			}			
		};
		handlers.add(handler);
		propertyChangedDispatcher = new PropertyChangedDispatcher(handlers, "", nodeCache, "dir");
		try {
			propertyChangedDispatcher.nodeChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
