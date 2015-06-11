package heracles.data.spring;

import heracles.data.datasource.RepositoryShardingDataSource;
import heracles.data.datasource.interceptor.AnnotationReadWriteDataSourceInterceptor;
import heracles.data.datasource.interceptor.RepositoryShardingDataSourceInterceptor;
import heracles.data.datasource.interceptor.TableShardingDataSourceInterceptor;
import heracles.data.mybatis.plugin.ShardingPlugin;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.alibaba.druid.pool.DruidDataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-xsd-druid.xml" }, inheritLocations = true)
public class XsdDruidTest {
	// @Autowired
	// private DruidDataSource rwds1_write;
	// @Autowired
	// private DruidDataSource rwds1_read0;
	// @Autowired
	// private DruidDataSource rwds1_read1;
	// @Autowired
	// private LoadBalanceStrategy<String> rwds1_lb;
	// @Autowired
	// private LoadBalanceStrategy<String> rwds2_lb;
	// @Autowired
	// private LoadBalanceStrategy<String> rwds3_lb;
	// @Autowired
	// private ReadWriteDataSourceKey rwds1_key;
	// @Autowired
	// private ReadWriteDataSourceKey rwds2_key;
	// @Autowired
	// private ReadWriteDataSourceKey rwds3_key;
	@Autowired
	private DruidDataSource rwds1;
	@Autowired
	private DruidDataSource rwds2;
	@Autowired
	private DruidDataSource rwds3;
	@Autowired
	private RepositoryShardingDataSource dataSource;
	@Autowired
	private TransactionInterceptor transactionInterceptor;
	@Autowired
	private TransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor;
	@Autowired
	private RepositoryShardingDataSourceInterceptor repositoryShardingDataSourceInterceptor;
	@Autowired
	private AnnotationReadWriteDataSourceInterceptor annotationReadWriteDataSourceInterceptor;
	@Autowired
	private TableShardingDataSourceInterceptor tableShardingDataSourceInterceptor;
	@Autowired
	private BeanNameAutoProxyCreator serviceBeanNameAutoProxyCreator;
	@Autowired
	private BeanNameAutoProxyCreator repositoryBeanNameAutoProxyCreator;
	@Autowired
	private SqlSessionFactoryBean sqlSessionFactory;
	@Autowired
	private SqlSessionFactoryBean myBatisSqlSessionFactory;
	@Autowired
	private ShardingPlugin shardingPlugin;

	@Test
	public void test() {
		// Assert.assertNotNull(rwds1_write);
		// Assert.assertNotNull(rwds1_read0);
		// Assert.assertNotNull(rwds1_read1);
		// Assert.assertNotNull(rwds1_lb);
		// Assert.assertNotNull(rwds2_lb);
		// Assert.assertNotNull(rwds3_lb);
		// Assert.assertNotNull(rwds1_key);
		// Assert.assertNotNull(rwds2_key);
		// Assert.assertNotNull(rwds3_key);
		Assert.assertNotNull(rwds1);
		Assert.assertNotNull(rwds2);
		Assert.assertNotNull(rwds3);
		Assert.assertNotNull(dataSource);
		Assert.assertNotNull(transactionInterceptor);
		Assert.assertNotNull(transactionAttributeSourceAdvisor);
		Assert.assertNotNull(repositoryShardingDataSourceInterceptor);
		Assert.assertNotNull(annotationReadWriteDataSourceInterceptor);
		Assert.assertNotNull(tableShardingDataSourceInterceptor);
		Assert.assertNotNull(serviceBeanNameAutoProxyCreator);
		Assert.assertNotNull(repositoryBeanNameAutoProxyCreator);
		Assert.assertNotNull(sqlSessionFactory);
		Assert.assertNotNull(myBatisSqlSessionFactory);
		Assert.assertNotNull(shardingPlugin);
	}
}
