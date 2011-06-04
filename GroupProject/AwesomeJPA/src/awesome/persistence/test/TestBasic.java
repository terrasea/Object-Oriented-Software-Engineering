package awesome.persistence.test;

import org.junit.Assert;
import org.junit.Test;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.FetchType;


public class TestBasic {
	class BasicClass {
		@Basic
		int test;
		
		@Basic(fetch=FetchType.EAGER)
		int test2;
		
		@Basic(optional=false)
		int test3;
		
		
		@Basic(fetch=FetchType.EAGER, optional=false)
		int test4;
	}
	
	BasicClass klass = new BasicClass();
	
	@Test
	public void testDefault() throws SecurityException, NoSuchFieldException {
		Basic basic = klass.getClass().getDeclaredField("test").getAnnotation(Basic.class);
		Assert.assertNotNull(basic);
		Assert.assertEquals(FetchType.LAZY, basic.fetch());
		Assert.assertEquals(true, basic.optional());
	}
	
	
	@Test
	public void testSetFetch() throws SecurityException, NoSuchFieldException {
		Basic basic = klass.getClass().getDeclaredField("test2").getAnnotation(Basic.class);
		Assert.assertNotNull(basic);
		Assert.assertEquals(FetchType.EAGER, basic.fetch());
	}
	
	
	@Test
	public void testSetOptional() throws SecurityException, NoSuchFieldException {
		Basic basic = klass.getClass().getDeclaredField("test3").getAnnotation(Basic.class);
		Assert.assertNotNull(basic);
		Assert.assertEquals(false, basic.optional());
	}
	
	
	
	@Test
	public void testSetBoth() throws SecurityException, NoSuchFieldException {
		Basic basic = klass.getClass().getDeclaredField("test4").getAnnotation(Basic.class);
		Assert.assertNotNull(basic);
		Assert.assertEquals(FetchType.EAGER, basic.fetch());
		Assert.assertEquals(false, basic.optional());
	}
	
	
}
