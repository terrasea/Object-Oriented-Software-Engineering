package awesome.persistence.test;

import junit.framework.Assert;

import org.junit.Test;

import awesome.persistence.Entity;

public class TestEntity {
	@Entity(name="Test")
	class TestClass {
		
	}
	
	
	@Entity
	class TestClass2 {
		
	}
	
	
	
	@Test
	public void testDefaultName() {
		TestClass2 klass = new TestClass2();
		Entity entity = klass.getClass().getAnnotation(Entity.class);
		Assert.assertEquals("", entity.name());
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
