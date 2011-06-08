package entity;

import awesome.persistence.annotations.Basic;

public class Coffee {
	@Basic
	private int awesomeId;
	
	
	public void setAwesomeId(Integer id) {
		this.awesomeId = id;
	}
	
	
	public int getAwesomeId() {
		return awesomeId;
	}
	
	@Basic
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
	
	
//	@Basic
//	int milk;
//	
//	public int getMilk() {
//		return milk;
//	}
//	
//	
//	public void setMilk(int milk) {
//		this.milk = milk;
//	}
}
