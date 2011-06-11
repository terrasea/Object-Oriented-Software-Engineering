package awesome.persistence.test;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;

/**
 * 
 * Test object use in the TestManager class
 *
 */
public class Coffee {	
	@ID
	private String name;
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Basic
	int strength;
	
	public int getStrength() {
		return strength;
	}
	
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	
	@Basic
	boolean milk;
	
	public boolean getMilk() {
		return milk;
	}
	
	
	public void setMilk(boolean milk) {
		this.milk = milk;
	}
}
