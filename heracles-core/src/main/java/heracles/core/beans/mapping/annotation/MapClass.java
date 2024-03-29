package heracles.core.beans.mapping.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping the class
 * @author kriswang
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapClass {
	/**
	 * Set the class which need to be mapped with the annotated class
	 * @return
	 */
	String value() default "";
}
