package heracles.data.zk.handler;

import heracles.core.zookeeper.PropertyChangedHandler;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MarkDownPropertyChangedHandler implements PropertyChangedHandler, ApplicationContextAware {

	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;
	

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;		
	}

	@Override
	public void execute(Properties oldProps, Properties newProps) {
		
	}
}
