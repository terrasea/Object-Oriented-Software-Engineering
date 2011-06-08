package main;

import java.util.HashMap;

public class Tempt {
	
	public Tempt() {
		fields.put("id", false);
	}
	
	
	public HashMap<String, Boolean> fields = new HashMap<String, Boolean>();
	
	
	public void tempt() {
		if(fields.get("id")) {
			return;
		}
		System.out.println("You want it don't you?");
	}
	
	
	public void answer(String answer) {
		System.out.println("Tempt ClassLoader: " + Tempt.class.getClassLoader());
		System.out.println(answer);
	}
}
