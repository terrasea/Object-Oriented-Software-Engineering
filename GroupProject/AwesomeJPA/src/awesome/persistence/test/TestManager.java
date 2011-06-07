package awesome.persistence.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;


import awesome.persistence.manager.Manager;
import awesome.persistence.manager.PropertiesException;

/**
 * 
 * Test class for Manager
 *
 */
public class TestManager {

	private String propertiesPath = "C:/Users/Ferg/Desktop/OO/GroupProject/AwesomeJPA/src/awesome/persistence/test/awesome.properties";
	private String invalidPropertiesPath = "C:/Users/Ferg/Desktop/OO/GroupProject/AwesomeJPA/src/awesome/persistence/test/awesomeInvalid.properties";

	@Test
	public void testConstructor(){
		try {
			Manager.setProperties(propertiesPath);
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
		Manager.setProperties("awesome.propertiesNOFILE");
		
	}
	
	@Test(expected=PropertiesException.class)
	public void testConstructorEmptyFile()throws IOException, PropertiesException{
		Manager.setProperties(invalidPropertiesPath);
	}
	
	@Test
	public void testPersist() throws Exception{
		Manager.setProperties(propertiesPath);
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(100);
		p.setPString("HELLO WORLD");
		
		Manager.persist(p);

		List<Object> results = Manager.queryDB("FETCH " + p.getClass().getName());
		
		for(int index = 0; index < results.size(); index++){
			Primatives res = (Primatives) results.get(index);
			System.out.println(res.getPString());
		}
		
		System.out.println(results.size());
	}
}
