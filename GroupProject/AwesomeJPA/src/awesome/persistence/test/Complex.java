package awesome.persistence.test;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;

/**
 * 
 * Test object use in the TestManager class, tests one to one relationships between
 * entities, Complex has a relationship with a Primative entity.
 *
 */
public class Complex {
	@Basic
	private int myInt;
	@ID
	private String myString;
	@Basic
	private Primatives prim;
	
	public Complex(){}
	
	public int getMyInt(){
		return this.myInt;
	}
	
	public void setMyInt(int myInt){
		this.myInt = myInt;
	}
	
	public String getMyString(){
		return this.myString;
	}
	
	public void setMyString(String myString){
		this.myString = myString;
	}
	
	public Primatives getPrim(){
		return this.prim;
	}
	
	public void setPrim(Primatives prim){
		this.prim = prim;
	}
}
