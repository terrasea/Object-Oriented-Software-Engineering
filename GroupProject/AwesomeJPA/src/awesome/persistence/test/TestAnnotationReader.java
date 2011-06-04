package awesome.persistence.test;

import static org.junit.Assert.assertTrue;
import awesome.persistence.annotations.Column;
import awesome.persistence.annotations.AnnotationReader;
import org.junit.Test;

public class TestAnnotationReader {
	class TestClass {
		@Column
		int test;
		
		@Column(name="TEST2", length=20, nullable=false, unique=true)
		String test2;
	}
	
	TestClass test = new TestClass();
	
	@Test
	public void simpleTest(){
		AnnotationReader.processAnnotations(test);
		assertTrue(true);
	}
}
