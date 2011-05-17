package awesome.jpa.test;

import junit.framework.Assert;

import org.junit.Test;

import awesome.jpa.Entity;

public class TestEntity {
	@Entity(name="Test")
	class TestClass {
		
	}
	
	
	@Test
	public void testName() {
		TestClass klass = new TestClass();
		Entity entity = klass.getClass().getAnnotation(Entity.class);
		Assert.assertEquals("Test", entity.name());
	}
	
	
	@Test
	public void testNotNull() {
		TestClass klass = new TestClass();
		Entity entity = klass.getClass().getAnnotation(Entity.class);
		Assert.assertNotNull(entity);
	}
}
