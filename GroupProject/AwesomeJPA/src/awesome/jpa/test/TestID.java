package awesome.jpa.test;

import junit.framework.Assert;

import org.junit.Test;

import awesome.jpa.ID;


public class TestID {
	class IDTest {
		@ID
		int id;
	}
	
	class IDTest2 {
		@ID(name="id2")
		int id;
	}
	
	@Test
	public void testNotNull() throws SecurityException, NoSuchFieldException {
		IDTest klass = new IDTest();
		ID id = klass.getClass().getDeclaredField("id").getAnnotation(ID.class);
		Assert.assertNotNull(id);
	}
	
	
	@Test
	public void testDefaultName() throws SecurityException, NoSuchFieldException {
		IDTest klass = new IDTest();
		ID id = klass.getClass().getDeclaredField("id").getAnnotation(ID.class);
		Assert.assertEquals("id", id.name());
	}
	
	
	@Test
	public void testSetName() throws SecurityException, NoSuchFieldException {
		IDTest2 klass = new IDTest2();
		ID id = klass.getClass().getDeclaredField("id").getAnnotation(ID.class);
		Assert.assertEquals("id2", id.name());
	}
}
