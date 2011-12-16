package tetz42.cello.annotation;

import static tetz42.cello.CelloUtil.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tetz42.cello.ICell;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderDef {
	String title();

	// String name() default "";

	int width() default UNDEFINED;

	String style() default ICell.HEADER_STYLE;

	boolean convert() default false;

	String convertSchema() default "";
}
