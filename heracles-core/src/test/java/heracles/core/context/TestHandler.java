package heracles.core.context;

import heracles.core.context.annotation.PropertyHandler;
import heracles.core.context.property.PropertyManager;
import heracles.core.zookeeper.PropertyChangedHandler;

import java.util.Properties;

import javax.annotation.Resource;

@PropertyHandler
public class TestHandler implements PropertyChangedHandler {
	
	@Resource
	private PropertyManager propertyManager;
	
	@Override
	public void execute(Properties oldProps, Properties newProps) {
		propertyManager.getProperty("abc");
		propertyManager.getProperty("abc", "def");
	}
}
