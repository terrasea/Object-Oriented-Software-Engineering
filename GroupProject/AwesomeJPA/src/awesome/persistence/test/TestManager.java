package awesome.persistence.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


import awesome.persistence.manager.EntityException;
import awesome.persistence.manager.Manager;
import awesome.persistence.manager.NotAEntity;
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
	
	//@Test
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
	
	@Test
	public void testGetField() throws IOException, PropertiesException, NotAEntity, SQLException, EntityException{
		Manager.setProperties(propertiesPath);
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(100);
		p.setPString("HELLO WORLD");
		
		Manager.persist(p);
		
		String s = (String) Manager.getField(p.getClass().getName(), 1, "pString");
		Assert.assertTrue(s.equals("HELLO WORLD"));
		
		int i = (Integer) Manager.getField(p.getClass().getName(), 1, "pInt");
		Assert.assertTrue(i == 100);
		
		boolean b = (Boolean) Manager.getField(p.getClass().getName(), 1, "pBool");
		Assert.assertTrue(b);
		
		double d = (Double) Manager.getField(p.getClass().getName(), 1, "pDouble");
		Assert.assertTrue(d == 100.11);
		
		float f = (Float) Manager.getField(p.getClass().getName(), 1, "pFloat");
		Assert.assertTrue(f == new Float(0.1));
	}
}
