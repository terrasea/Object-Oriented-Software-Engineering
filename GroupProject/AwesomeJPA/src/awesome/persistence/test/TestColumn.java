package awesome.persistence.test;

import junit.framework.Assert;

import org.junit.Test;

import awesome.persistence.annotations.Column;


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
		Assert.assertNotNull(column);
		Assert.assertEquals("", column.name());
		Assert.assertEquals(255, column.length());
		Assert.assertEquals(true, column.nullable());
		Assert.assertEquals(false, column.unique());
	}
	
	@Test
	public void testSetName() throws SecurityException, NoSuchFieldException {
		Column column = klass.getClass().getDeclaredField("test2").getAnnotation(Column.class);
		Assert.assertNotNull(column);
		Assert.assertEquals("TEST2", column.name());
	}
	
	@Test
	public void testSetLength() throws SecurityException, NoSuchFieldException {
		Column column = klass.getClass().getDeclaredField("test2").getAnnotation(Column.class);
		Assert.assertNotNull(column);
		Assert.assertEquals(20, column.length());
	}
	
	@Test
	public void testSetNullable() throws SecurityException, NoSuchFieldException {
		Column column = klass.getClass().getDeclaredField("test2").getAnnotation(Column.class);
		Assert.assertNotNull(column);
		Assert.assertEquals(false, column.nullable());
	}
	
	@Test
	public void testSetUnique() throws SecurityException, NoSuchFieldException {
		Column column = klass.getClass().getDeclaredField("test2").getAnnotation(Column.class);
		Assert.assertNotNull(column);
		Assert.assertEquals(true, column.unique());
	}
	
}
