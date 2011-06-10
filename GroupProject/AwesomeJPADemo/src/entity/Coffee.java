package entity;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;
import awesome.persistence.annotations.ManyToOne;

public class Coffee {
//	@ID
//	private int awesomeId;
//	
//	
//	public void setAwesomeId(Integer id) {
//		this.awesomeId = id;
//	}
//	
//	
//	public int getAwesomeId() {
//		return awesomeId;
//	}
	
	
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
	
	
	@ManyToOne(target = Test.class)
	Test test;
	
	
	public Test getTest() {
		return test;
	}
	
	
	
	public void setTest(Test test) {
		this.test = test;
	}
}
