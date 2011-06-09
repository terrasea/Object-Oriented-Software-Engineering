package awesome.persistence.test;

public class Complex {

	private int myInt;
	private String myString;
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
