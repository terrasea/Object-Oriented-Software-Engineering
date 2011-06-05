package main;

import java.util.HashMap;
import java.util.HashSet;

public class Tempt {
	
	public Tempt() {
		
		
	}
	
	
	public HashMap<String, Boolean> fields = new HashMap<String, Boolean>();
	
	
	public void tempt() {
		//if()
		System.out.println("You want it don't you?");
	}
	
	
	public void answer(String answer) {
		System.out.println("Tempt ClassLoader: " + Tempt.class.getClassLoader());
		System.out.println(answer);
	}
}
