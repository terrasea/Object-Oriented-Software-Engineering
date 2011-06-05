package main;

import java.util.HashSet;

public class Learn {
	private static int globalCount = 0;
	int id;
	public HashSet<String> fields = new HashSet<String>();
	
	
	public int getId() {
		if(!fields.contains("id")) {
			fields.add("id");
			id = Learn.globalCount++;
		}
		return id;
	}
}
