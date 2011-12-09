package tetz42.util.tablequery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tetz42.util.tableobject.tables.TableObject1.HeaderInfo;

@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnDef {
	String title();

	int width() default HeaderInfo.UNDEFINED;
}
