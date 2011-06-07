package awesome.persistence.agent;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldFetcher {
	public static <C> void set(C klass, String field) throws Exception {
		Field fieldID = klass.getClass().getDeclaredField(field);
		String table = klass.getClass().getName();
		Type type = fieldID.getType();
		String[] tmpArray = type.toString().split("\\.");
		String typeDesc = tmpArray[tmpArray.length-1];
		if(typeDesc.matches("(int)|(long)|(Integer)")) {
			fieldID.set(klass, -3);	
		} else if(type.toString().matches("(double)|(float)")) {
			System.out.println("Fetch double or float");
		} else if(type.toString().matches("boolean")) {
			System.out.println("Fetch boolean");
			
		}
		
		System.out.println("FieldFetcher: " + fieldID.get(klass) + ", " + fieldID.getType() + ", " + typeDesc);
	}
}
