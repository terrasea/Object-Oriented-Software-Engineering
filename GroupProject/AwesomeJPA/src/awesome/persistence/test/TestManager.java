package awesome.persistence.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import awesome.persistence.manager.AQLException;
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
	//private String propertiesPath = "src/awesome/persistence/test/awesome.properties";
	private String invalidPropertiesPath = "C:/Users/Ferg/Desktop/OO/GroupProject/AwesomeJPA/src/awesome/persistence/test/awesomeInvalid.properties";
	//private String invalidPropertiesPath = "src/awesome/persistence/test/awesomeInvalid.properties";

	/**
	 * Deletes the database file for each test run.
	 * @throws Exception If the database could not be deleted
	 */
	@Before
	public void setUp() throws Exception{
		System.out.println("\n\n");
		// Create file obj for the database file
		File f = new File("test.db");
		// counter for delete count
		int i = 0;
		
		// while the file exisits try to delete it
		while(f.exists()){
			// attempt to delete the file
			if(f.delete())
				break;
			
			// increment counter
			i++;
			// sleep for a bit for handles to be released
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Delete limit reached throw exception
			if(i > 10){
				throw new Exception("Could not delete DB");
			}
		}
		
	}
	
	/**
	 * Tests the setting of the properties file with a valid file.
	 * @throws PropertiesException If the properties are malformed
	 * @throws IOException If the file could noawesome/persistence/test/t be accessed
	 */
	@Test
	public void testSetPropertiesValid() throws IOException, PropertiesException{
		// Attempt to set properties, fail if any expception thrown
		Manager.setProperties(propertiesPath);
	}
	
	/**
	 * Tests the set properties with an invalid filename
	 * @throws IOException Should be thrown
	 * @throws PropertiesException Should not be thrown
	 */
	@Test(expected=IOException.class)
	public void testSetPropertiesNoFile() throws IOException, PropertiesException{
		Manager.setProperties("awesome.propertiesNOFILE");
		
	}
	
	/**
	 * Tests the setting of the properties with an invalid properties file
	 * @throws IOException Should not be thrown
	 * @throws PropertiesException Should be thrown
	 */
	@Test(expected=PropertiesException.class)
	public void testSetPropertiesEmptyFile()throws IOException, PropertiesException{
		Manager.setProperties(invalidPropertiesPath);
	}
	
	/**
	 * Tests the persisting and retrieving of the primitive object from the database
	 * @throws Exception
	 */
	@Test
	public void testPersist() throws Exception{
		System.out.println("TP");
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
		
		assertTrue(results.size() == 1);
		
		Primatives res = (Primatives) results.get(0);
		assertTrue(res.getPInt() == 100);
		//assertTrue(res.getPString().equals("HELLO WORLD"));

	}
	
	/**
	 * Tests the getting of individual fields from an object in the database
	 * @throws IOException
	 * @throws PropertiesException
	 * @throws NotAEntity
	 * @throws SQLException
	 * @throws EntityException
	 */
	@Test
	public void testGetField() throws IOException, PropertiesException, NotAEntity, SQLException, EntityException{
		Manager.setProperties(propertiesPath);
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(1);
		p.setPString("HELLO WORLD");
		
		Manager.persist(p);
		
		String s = (String) Manager.getField(p.getClass().getName(), 1, "pString");
		Assert.assertTrue(s.equals("HELLO WORLD"));
		
		int i = (Integer) Manager.getField(p.getClass().getName(), 1, "pInt");
		Assert.assertTrue(i == 1);
		
		boolean b = (Boolean) Manager.getField(p.getClass().getName(), 1, "pBool");
		Assert.assertTrue(b);
		
		double d = (Double) Manager.getField(p.getClass().getName(), 1, "pDouble");
		Assert.assertTrue(d == 100.11);
		
		float f = (Float) Manager.getField(p.getClass().getName(), 1, "pFloat");
		Assert.assertTrue(f == new Float(0.1));
		
		char c = (Character) Manager.getField(p.getClass().getName(), 1, "pChar");
		assertTrue(c == 'c');
	}
	
	/**
	 * Tests the deletion of objects from the database. 
	 * 
	 * @throws IOException
	 * @throws PropertiesException
	 * @throws NotAEntity
	 * @throws SQLException
	 * @throws EntityException
	 */
	@Test
	public void testDelete() throws IOException, PropertiesException, NotAEntity, SQLException, EntityException{
		Manager.setProperties(propertiesPath);
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(100);
		p.setPString("HELLO WORLD");
		
		Manager.persist(p);
		
		Assert.assertTrue(Manager.deleteFromDb(p));
	}
	
	/**
	 * Tests the storing and retrieving of a complex plojo from the database.
	 * @throws IOException
	 * @throws PropertiesException
	 * @throws NotAEntity
	 * @throws SQLException
	 * @throws EntityException
	 */
	@Test
	public void testComplexObject() throws IOException, PropertiesException, NotAEntity, SQLException, EntityException{
		Manager.setProperties(propertiesPath);
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(100);
		p.setPString("HELLO WORLD");
		
		Complex c = new Complex();
		c.setPrim(p);
		c.setMyString("HELLO ALL");
		c.setMyInt(100);
		
		Manager.persist(c);
		Primatives prims = (Primatives) Manager.getField(c.getClass().getCanonicalName(), c.getMyString(), "prim");
		assertTrue(prims.getPInt() == 100);
	}
	
	@Test
	public void coffeeTest() throws NotAEntity, SQLException, EntityException, IOException, PropertiesException, AQLException{
		Manager.setProperties(propertiesPath);
		Coffee c = new Coffee();
		c.setMilk(true);
		c.setStrength(100);
		
		c.setName("Strong");
		
		Manager.persist(c);
		
		List<Object> results = Manager.queryDB("FETCH " + c.getClass().getCanonicalName());
		
		assertTrue(results.size() == 1);
		
		Coffee res = (Coffee) results.get(0);
		assertTrue(res.getName().equals("Strong"));
	}
	
	@Test
	public void teaTest() throws NotAEntity, SQLException, EntityException, AQLException{
		Tea t = new Tea();
		
		t.setAwesomeId(100);
		t.setMilk(true);
		t.setName("yum tea");
		t.setStrength(10000);
		
		Manager.persist(t);
		List<Object> results = Manager.queryDB("FETCH " + t.getClass().getCanonicalName());
		assertTrue(results.size() == 1);
		
		Tea res = (Tea)results.get(0);
		assertTrue(res.getAwesomeId() == 100);
	}
	
	@Test
	public void updateTest() throws IOException, PropertiesException, NotAEntity, SQLException, EntityException, AQLException{
		Manager.setProperties(propertiesPath);
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(100);
		p.setPString("HELLO WORLD");
		
		Manager.persist(p);
		p.setPString("NOT HELLO WORLD");
		Manager.persist(p);
		
		List<Object> results = Manager.queryDB("FETCH " + p.getClass().getCanonicalName());
		assertTrue(results.size() == 1);
		
		String s = (String) Manager.getField(p.getClass().getName(), 100, "pString");
		Assert.assertTrue(s.equals("NOT HELLO WORLD"));
	}
	

	@Test
	public void whereTest() throws NotAEntity, SQLException, EntityException, AQLException{
		Primatives p = new Primatives();
		p.setPBool(true);
		p.setPChar('c');
		p.setPDouble(100.110);
		p.setPFloat(new Float(0.1));
		p.setPInt(1);
		p.setPString("HELLO WORLD");
		
		Manager.persist(p);
		
		Primatives p2 = new Primatives();
		p2.setPBool(true);
		p2.setPChar('c');
		p2.setPDouble(100.110);
		p2.setPFloat(new Float(0.1));
		p2.setPInt(2);
		p2.setPString("NOT HELLO WORLD");
		
		Manager.persist(p2);
		
		Primatives p3 = new Primatives();
		p3.setPBool(true);
		p3.setPChar('c');
		p3.setPDouble(100.110);
		p3.setPFloat(new Float(0.1));
		p3.setPInt(3);
		p3.setPString("NOT HELLO WORLD");
		
		Manager.persist(p3);
		
		List<Object> results = Manager.queryDB("FETCH " + p.getClass().getCanonicalName() + " WHERE pString = 'NOT HELLO WORLD'");
		assertTrue(results.size() == 2);
		
		Primatives res1 = (Primatives) results.get(0);
		Primatives res2 = (Primatives) results.get(1);
		
		int x = res1.getPInt();
		int y = res2.getPInt();

		if(x == 2)
			assertTrue(y == 3);
		else{
			assertTrue(x == 3);
			assertTrue(y == 2);
		}
	}
}
