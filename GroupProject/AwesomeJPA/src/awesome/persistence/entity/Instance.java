package awesome.persistence.entity;

import java.lang.reflect.Field;
import java.util.HashSet;

import awesome.persistence.annotations.Entity;


@Entity
public class Instance {
	//HashSet<String> fields = new HashSet<String>();
	
	public Instance() {
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void printAttributes() {
		for(Field field : this.getClass().getDeclaredFields()) {
			System.out.println("Field: " + field.getName() + ", Type: " + field.getType());
			try {
				HashSet<String> hashSet = (HashSet<String>)field.get(this);
				System.out.println("Fields: " + hashSet);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
