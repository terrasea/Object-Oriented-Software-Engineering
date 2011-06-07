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
		String table = klass.getClass().getName().replace('$', '_').replace('.','_')
		//String table = tmpArray[tmpArray.length - 1];
		Type type = fieldID.getType();
		String[] tmpArray = type.toString().split("\\.");
		String typeDesc = tmpArray[tmpArray.length - 1];
		int awesomeId = (Integer) klass.getClass().getDeclaredMethod(
				"getAwesomeId").invoke(klass);
		Object value = Manager.getField(table, awesomeId, field);
		klass.getClass().getDeclaredMethod(
				"set" + field.substring(0, 1).toUpperCase()
						+ field.substring(1), Class.forName(type.toString()))
				.invoke(klass, value);

		System.out.println("FieldFetcher: " + fieldID.get(klass) + ", "
				+ fieldID.getType() + ", " + typeDesc);
	}

	private static <J> J getFromDB(String id, String table, String field)
			throws SQLException {
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
