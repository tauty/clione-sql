package tetz42.util.tablequery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tetz42.util.tableobject.tables.TableObject1.HeaderInfo;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderDef {
	String title();

	String name() default "";

	int width() default HeaderInfo.UNDEFINED;
}