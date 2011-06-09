package awesome.persistence.test;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;

public class Primatives {
	@Basic
	private String pString;
	
	@Basic
	@ID
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
