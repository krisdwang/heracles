package heracles.data.common.annotation;

import heracles.data.common.util.ReadWriteType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 读写分离注解
 * 
 * @author kriswang Zhu
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ReadWrite {
	/**
	 * 读写分离类型
	 */
	ReadWriteType type() default ReadWriteType.WRITE;
}
