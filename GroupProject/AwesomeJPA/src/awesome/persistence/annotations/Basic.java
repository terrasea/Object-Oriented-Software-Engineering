package awesome.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * Definition for "Basic" annotation
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Basic {
	// How to treat the field at load time
	FetchType fetch() default FetchType.LAZY;
	// ???????
	boolean optional() default true;
}
