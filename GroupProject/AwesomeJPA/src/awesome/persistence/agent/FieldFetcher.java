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
		String[] tmpArray = klass.getClass().getName().split("\\.");
		String table = tmpArray[tmpArray.length - 1];
		Type type = fieldID.getType();
		tmpArray = type.toString().split("\\.");
		String typeDesc = tmpArray[tmpArray.length - 1];
		int awesomeId = (Integer) klass.getClass().getDeclaredMethod("getId").invoke(klass);
		Object value = Manager.getField(table, awesomeId, field);
		klass.getClass().getDeclaredMethod("set"+ field.substring(0, 1).toUpperCase() + field.substring(1), Class.forName(type.toString())).invoke(klass, value);
//		if (typeDesc.matches("(int)|(long)|(Integer)|(Long)")) {
//			List<Object> obj = Manager.queryDB(String.format("SELECT %s FROM %s WHERE %s = %s",
//					field, table, "id", ));
//			if(obj != null) {
//				fieldID.set(klass, obj.get(0));
//			}
//		} else if (typeDesc.matches("(double)|(float)|(Double)|(Float)")) {
//			System.out.println("Fetch double or float");
//		} else if (typeDesc.matches("(boolean)|(Boolean)")) {
//			System.out.println("Fetch boolean");
//
//		} else if (typeDesc.matches("String")) {
//
//		}

		System.out.println("FieldFetcher: " + fieldID.get(klass) + ", "
				+ fieldID.getType() + ", " + typeDesc);
	}
	
	
	private static <J> J getFromDB(String id, String table, String field) throws SQLException {
		// Get the driver class
	    try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Unable to connect to database.");
		}
	    // Create the connection and return it
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		
		
		return null;
		
	}
}
