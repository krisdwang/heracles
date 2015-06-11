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
@Target(ElementType.TYPE)
public @interface CreateDb {

	// /**
	// * 创建db配置文件位置
	// *
	// */
	// String propertiesFile() default "";
	//
	// /**
	// * 初始化dbSQL文件位置
	// *
	// */
	// String sqlFile() default "";
}
