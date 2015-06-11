package heracles.core.zookeeper;

import java.util.Properties;

/**
 * 
 * @author kriswang
 * 
 */
public interface PropertyChangedHandler {
	void execute(Properties oldProps, Properties newProps);
}
