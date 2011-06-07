package awesome.persistence.entity;

import java.lang.reflect.Field;
import java.util.HashSet;

import awesome.persistence.agent.FieldFetcher;
import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;



public class Instance {
	//HashSet<String> fields = new HashSet<String>();
	
	public Instance() {
		
	}
	@ID
	private int awesomeId = 1;
	
	
	public int getAwesomeId() {
		return awesomeId;
	}
	
	
	public void setAwesomeId(int id) {
		awesomeId = id;
	}
	
	@Basic
	private int field = 0;
	
	
	public int getField() {
		
		return field;
	}
	
	
	public void setField(int value) {
		field = value;
	}
	
//	@SuppressWarnings("unchecked")
//	public void printAttributes() {
//		for(Field field : this.getClass().getDeclaredFields()) {
//			System.out.println("Field: " + field.getName() + ", Type: " + field.getType());
//			if(field.getName().equals("fields")) {
//				HashSet<String> hashSet;
//				try {
//					hashSet = (HashSet<String>)field.get(this);
//					System.out.println("Fields: " + hashSet);
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}
//		try {
//			Field field = this.getClass().getDeclaredField("fields");
//			HashSet<String> hashSet = (HashSet<String>)field.get(this);
//			System.out.println("Fields: " + hashSet);
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
