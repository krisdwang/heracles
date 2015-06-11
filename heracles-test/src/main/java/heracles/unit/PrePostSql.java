package heracles.unit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface PrePostSql {
	/**
	 * 自定义beforeTest执行SQL
	 * 
	 */
	public String preSqlFile() default "";

	/**
	 * 自定义afterTest执行SQL
	 * 
	 */
	public String postSqlFile() default "";
}
