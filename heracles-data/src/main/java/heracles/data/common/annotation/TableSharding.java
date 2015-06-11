package heracles.data.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分表注解
 * 
 * @author kriswang Zhu
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TableSharding {
	/**
	 * 分表参数名
	 */
	String strategy();

	/**
	 * 分表参数
	 */
	String key();
}
