package heracles.data.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分库注解
 * 
 * @author kriswang
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RepositorySharding {
	/**
	 * 分库策略名
	 */
	String strategy();

	/**
	 * 分库参数
	 */
	String key();
}
