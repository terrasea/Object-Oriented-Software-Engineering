package awesome.jpa;

public @interface Basic {
	FetchType fetch() default FetchType.LAZY;
	boolean optional() default true;
}
