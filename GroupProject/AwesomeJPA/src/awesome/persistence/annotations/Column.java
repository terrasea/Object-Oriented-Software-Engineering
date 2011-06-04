package awesome.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Definition for the "column" annotation, used to denote
 * fields that represent columns in the database table
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	// Column name
	String name() default "";
	// ???????
	int length() default 255;
	// Whether the column is treated as nullable in the database
	boolean nullable() default true;
	// Whether the column is treated as unique in the database
	boolean unique() default false;
}
