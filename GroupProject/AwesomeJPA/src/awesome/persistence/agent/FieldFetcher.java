package awesome.persistence.agent;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import awesome.persistence.manager.Manager;

public class FieldFetcher {
	public static <C> void set(C klass, String field) throws Exception {
		Field fieldID = klass.getClass().getDeclaredField(field);
		String table = klass.getClass().getName().replace('$', '_').replace('.','_');
		//String table = tmpArray[tmpArray.length - 1];
		Type type = fieldID.getType();
		
		String[] tmpArray = type.toString().split("\\.");
		String typeDesc = tmpArray[tmpArray.length - 1];
		int awesomeId = (Integer) klass.getClass().getDeclaredMethod(
				"getAwesomeId").invoke(klass);
		Object value = Manager.getField(table, awesomeId, field);
		fieldID.setAccessible(true);
		if(value != null) {
			fieldID.set(klass, value);
		} 
	}
}
