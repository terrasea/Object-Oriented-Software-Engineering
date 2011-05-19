package awesome.persistence.test;

import junit.framework.Assert;

import org.junit.Test;

import awesome.persistence.FetchType;
import awesome.persistence.OneToMany;


public class TestOneToMany {
	class TestClass {
		@OneToMany
		String test;
		
		@OneToMany(fetch=FetchType.EAGER, mappedBy="test")
		String test2;
	}
	
	TestClass klass = new TestClass();
	
	
	@Test
	public void testDefaults() throws SecurityException, NoSuchFieldException {
		OneToMany otm = klass.getClass().getDeclaredField("test").getAnnotation(OneToMany.class);
		Assert.assertNotNull(otm);
		Assert.assertEquals(FetchType.LAZY, otm.fetch());
		Assert.assertEquals("", otm.mappedBy());
	}
	
	
	@Test
	public void testSetFetchType() throws SecurityException, NoSuchFieldException {
		OneToMany otm = klass.getClass().getDeclaredField("test2").getAnnotation(OneToMany.class);
		Assert.assertNotNull(otm);
		Assert.assertEquals(FetchType.EAGER, otm.fetch());
	}
	
	
	
	@Test
	public void testSetMappedBy() throws SecurityException, NoSuchFieldException {
		OneToMany otm = klass.getClass().getDeclaredField("test2").getAnnotation(OneToMany.class);
		Assert.assertNotNull(otm);
		Assert.assertEquals("test", otm.mappedBy());
	}
}
