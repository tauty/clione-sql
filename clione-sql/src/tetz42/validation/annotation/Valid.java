package tetz42.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tetz42.validation.Format;
import tetz42.validation.Required;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Valid {
	String name() default "";

	Required required() default Required.FALSE;

	// String[] contains() default {};
	// String[] notContains() default {};
	//
	// long[] containsNum() default {};
	// long[] notContainsNum() default {};

	long length() default Long.MIN_VALUE;

	long maxLength() default Long.MIN_VALUE;

	long minLength() default Long.MIN_VALUE;

	// long byteLength() default Long.MIN_VALUE;
	// long maxByteLength() default Long.MIN_VALUE;
	// long minByteLength() default Long.MIN_VALUE;

	Format format() default Format.ANY;
	// String match() default "";
	// String unmatch() default "";

	// String maxString() default "";
	// String minString() default "";
	// long max() default Long.MIN_VALUE;
	// long min() default Long.MIN_VALUE;
	// double maxDouble() default Double.NaN;
	// double minDouble() default Double.NaN;
}
