package tetz42.cello.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CellDef {
	// String name() default "";

	// int width() default UNDEFINED;

	String style() default "";

	boolean convert() default false;

	String convertSchema() default "";
}
