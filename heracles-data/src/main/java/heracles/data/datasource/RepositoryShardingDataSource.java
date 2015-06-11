package heracles.data.datasource;

import heracles.data.common.holder.StrategyHolder;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 分库数据源
 * 
 * @author kriswang
 * 
 */
public class RepositoryShardingDataSource extends AbstractRoutingDataSource implements
		RepositoryShardingDataSourceMBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryShardingDataSource.class);

	private Set<String> markDownSet = new CopyOnWriteArraySet<String>();

	@Override
	protected Object determineCurrentLookupKey() {

		String key = StrategyHolder.getRepositoryShardingKey();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("real key is " + key);
		}

		if (key != null && markDownSet.contains(key)) {
			if (logger.isDebugEnabled()) {
				logger.debug("get into mark down[" + key + "]");
			}

			StrategyHolder.removeRepositoryShardingStrategy();
			if (logger.isDebugEnabled()) {
				logger.debug("clean up sharding holder before throw access denied exception[" + key + "]");
			}
			throw new RuntimeException("access denied for " + key);
		}

		return key;
	}

	@PostConstruct
	public void register() {
		synchronized (this) {
			MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName objectName;
			try {
				objectName = new ObjectName("io.doeasy.data:type=" + this.toString());
				if (!mbeanServer.isRegistered(objectName)) {
					mbeanServer.registerMBean(this, objectName);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("io.doeasy.data:type=" + this.toString() + " registered successfully");
					}
				}
			} catch (InstanceAlreadyExistsException e) {
				LOGGER.error(e.getMessage());
			} catch (MBeanRegistrationException e) {
				LOGGER.error(e.getMessage());
			} catch (NotCompliantMBeanException e) {
				LOGGER.error(e.getMessage());
			} catch (MalformedObjectNameException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	@Override
	public void putKey(String key) {
		this.markDownSet.add(key);
	}

	@Override
	public void removeKey(String key) {
		this.markDownSet.remove(key);
	}

	@Override
	public Set<String> getMarkDownKeys() {
		return markDownSet;
	}
}
