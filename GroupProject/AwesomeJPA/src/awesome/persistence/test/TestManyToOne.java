package awesome.persistence.test;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import awesome.persistence.FetchType;
import awesome.persistence.ManyToMany;
import awesome.persistence.ManyToOne;


public class TestManyToOne {
	class TestClass {
		@ManyToOne
		Set<String> test;
		
		@ManyToOne(fetch=FetchType.EAGER, optional=false)
		Set<String> test2;
	}
	
	
	TestClass klass = new TestClass();
	
	
	@Test
	public void testDefault() throws SecurityException, NoSuchFieldException {
		ManyToOne mto = klass.getClass().getDeclaredField("test").getAnnotation(ManyToOne.class);
		Assert.assertNotNull(mto);
		Assert.assertEquals(FetchType.LAZY, mto.fetch());
		Assert.assertEquals(true, mto.optional());
	}
	
	
	@Test
	public void testSetFetchType() throws SecurityException, NoSuchFieldException {
		ManyToOne mto = klass.getClass().getDeclaredField("test2").getAnnotation(ManyToOne.class);
		Assert.assertNotNull(mto);
		Assert.assertEquals(FetchType.EAGER, mto.fetch());
	}
	
	
	@Test
	public void testSetOptional() throws SecurityException, NoSuchFieldException {
		ManyToOne mto = klass.getClass().getDeclaredField("test2").getAnnotation(ManyToOne.class);
		Assert.assertNotNull(mto);
		Assert.assertEquals(false, mto.optional());
	}
}
