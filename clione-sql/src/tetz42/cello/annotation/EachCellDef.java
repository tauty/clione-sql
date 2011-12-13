package tetz42.cello.annotation;

import static tetz42.cello.CelloUtil.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EachCellDef {
	//	String name() default "";

	int width() default UNDEFINED;

	String style() default "";
}
