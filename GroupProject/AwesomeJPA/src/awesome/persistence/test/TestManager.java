package awesome.persistence.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.Entity;
import awesome.persistence.manager.Manager;
import awesome.persistence.manager.PropertiesException;

/**
 * 
 * Test class for Manager
 *
 */
public class TestManager {

	@Entity
	class Primatives{
		@Basic
		private String pString;
		@Basic
		private int pInt;
		@Basic
		private boolean pBool;
		@Basic
		private double pDouble;
		@Basic
		private float pFloat;
		@Basic
		private char pChar;
		
		public Primatives(){}
		
		public void setPString(String pString){
			this.pString = pString;
		}
		
		public String getPString(){
			return this.pString;
		}
		
		public void setPInt(int pInt){
			this.pInt = pInt;
		}
		
		public int getPInt(){
			return this.pInt;
		}
		
		public void setPBool(boolean pBool){
			this.pBool = pBool;
		}
		
		public boolean getPBool(){
			return this.pBool;
		}
		
		public void setPDouble(double pDouble){
			this.pDouble = pDouble;
		}
		
		public double getPDouble(){
			return this.pDouble;
		}
		
		public void setPFloat(float pFloat){
			this.pFloat = pFloat;
		}
		
		public float getPFloat(){
			return this.pFloat;
		}
		
		public void setPChar(char pChar){
			this.pChar = pChar;
		}
		
		public char getPChar(){
			return this.pChar;
		}
	}
	
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
	
	@Test
	public void testPersist(){
		
	}
}
