package awesome.persistence.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import awesome.persistence.manager.Manager;
import awesome.persistence.manager.PropertiesException;

/**
 * 
 * Test class for Manager
 *
 */
public class TestManager {

	
	@Test
	public void testConstructor(){
		Manager m;
		try {
			m = new Manager("awesome.properties");
			assertTrue(m != null);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (PropertiesException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test(expected=IOException.class)
	public void testConstructorNoFile() throws IOException, PropertiesException{
		Manager m = new Manager("awesome.propertiesNOFILE");
		fail();
	}
	
	@Test(expected=PropertiesException.class)
	public void testConstructorEmptyFile()throws IOException, PropertiesException{
		Manager m = new Manager("awesomeInvalid.properties");
		fail();
	}
}
