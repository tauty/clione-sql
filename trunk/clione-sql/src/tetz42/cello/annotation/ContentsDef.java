package tetz42.cello.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tetz42.cello.ICell;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentsDef {

	String style() default ICell.CELL_STYLE;

	boolean convert() default false;

	String convertSchema() default "";
}
