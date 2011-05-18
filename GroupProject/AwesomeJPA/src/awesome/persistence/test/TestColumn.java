package awesome.persistence.test;

import org.junit.Test;

import awesome.persistence.Basic;
import awesome.persistence.Column;


public class TestColumn {
	class TestClass {
		@Column
		int test;
		
		
		@Column(name="TEST2", length=20, nullable=false, unique=true)
		String test2;
		
	}
	
	TestClass klass = new TestClass();
	
	@Test
	public void testDefaults() throws SecurityException, NoSuchFieldException {
		Column column = klass.getClass().getDeclaredField("test").getAnnotation(Column.class);
	}
}
