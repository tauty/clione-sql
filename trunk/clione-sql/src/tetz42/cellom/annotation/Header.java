package tetz42.cellom.annotation;

import static tetz42.cellom.CelloUtil.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tetz42.cellom.ICell;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Header {
	String title();

	int width() default UNDEFINED;

	String style() default ICell.HEADER_STYLE;

	boolean convert() default false;

	String convertSchema() default "";
}
