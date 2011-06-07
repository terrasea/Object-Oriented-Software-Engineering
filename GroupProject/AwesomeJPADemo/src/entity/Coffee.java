package entity;

import awesome.persistence.annotations.Basic;

public class Coffee {
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
	
	
	@Basic
	boolean milk;
	
	public boolean getMilk() {
		return milk;
	}
	
	
	public void setMilk(boolean milk) {
		this.milk = milk;
	}
}
