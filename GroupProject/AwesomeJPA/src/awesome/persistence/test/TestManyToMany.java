package awesome.persistence.test;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import awesome.persistence.annotations.FetchType;
import awesome.persistence.annotations.ManyToMany;


public class TestManyToMany {
	class Temp {
		
	}
	
	class TestClass {
		@ManyToMany
		Set<Temp> temp;
		
		@ManyToMany(fetch=FetchType.EAGER, mappedBy="temp")
		Set<Temp> temp2;
	}
	
	
	TestClass klass = new TestClass();
	
	@Test
	public void testDefaults() throws SecurityException, NoSuchFieldException {
		ManyToMany mtm = klass.getClass().getDeclaredField("temp").getAnnotation(ManyToMany.class);
		Assert.assertNotNull(mtm);
		Assert.assertEquals(FetchType.LAZY, mtm.fetch());
		Assert.assertEquals("", mtm.mappedBy());
	}
	
	
	
	@Test
	public void testSetFetchType() throws SecurityException, NoSuchFieldException {
		ManyToMany mtm = klass.getClass().getDeclaredField("temp2").getAnnotation(ManyToMany.class);
		Assert.assertNotNull(mtm);
		Assert.assertEquals(FetchType.EAGER, mtm.fetch());
	}
	
	
	@Test
	public void testSetMappedBy() throws SecurityException, NoSuchFieldException {
		ManyToMany mtm = klass.getClass().getDeclaredField("temp2").getAnnotation(ManyToMany.class);
		Assert.assertNotNull(mtm);
		Assert.assertEquals("temp", mtm.mappedBy());
	}
}
