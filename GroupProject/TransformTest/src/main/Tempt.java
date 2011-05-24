package main;

public class Tempt {
	
	public Tempt() {
		
		
	}
	
	public void tempt() {
		System.out.println("You want it don't you?");
	}
	
	
	public void answer(String answer) {
		System.out.println("Tempt ClassLoader: " + Tempt.class.getClassLoader());
		System.out.println(answer);
	}
}
