package heracles.data.mybatis.plugin;

import heracles.data.mybatis.converter.SqlConverter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * 分表拦截器
 * 
 * @author kriswang
 * 
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class ShardingPlugin implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(ShardingPlugin.class);

	private SqlConverter sqlConverter;

	public SqlConverter getSqlConverter() {
		return sqlConverter;
	}

	public void setSqlConverter(SqlConverter sqlConverter) {
		this.sqlConverter = sqlConverter;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		String debugInfo = "[" + invocation.toString() + "]";

		if (log.isDebugEnabled()) {
			log.debug("get into sharding plugin" + debugInfo);
		}

		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

		String sql = statementHandler.getBoundSql().getSql();
		if (log.isDebugEnabled()) {
			log.debug("original sql:" + sql);
		}

		String targetSql = sqlConverter.convert(sql, statementHandler);
		if (log.isDebugEnabled()) {
			log.debug("converted sql:" + targetSql);
		}

		if (!sql.equals(targetSql)) {
			Field field = BoundSql.class.getDeclaredField("sql");
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, statementHandler.getBoundSql(), targetSql);
		}

		Object object = invocation.proceed();

		if (log.isDebugEnabled()) {
			log.debug("get out sharding plugin" + debugInfo);
		}

		return object;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

}
