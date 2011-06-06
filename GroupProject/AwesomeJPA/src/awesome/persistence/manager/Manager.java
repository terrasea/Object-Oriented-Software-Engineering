package awesome.persistence.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * 
 * Manager class
 *
 */

public class Manager {
	
	// Properties extracted from the awesome.properties file
	private static Properties properties;
	/**
	 * Sets up the properties for the Manager
	 * @throws IOException Thrown if the awesome.properties file cannot be loaded
	 */
	public static void setProperties(String propertiesPath) throws IOException, PropertiesException{
		// Initialize properties
		properties = new Properties();
		
		// Load properties from file
		properties.load(new FileInputStream(propertiesPath));
		
		// Check the required properties are provided
		if(!properties.containsKey("user") && !properties.containsKey("password") && !properties.containsKey("url")){
			// Invalid properties file, throw exception
			throw new PropertiesException("Invalid properties file provided.");
		}
	}
	
	/**
	 * Sends an object to the database
	 * @param entity The entity to store in the database
	 * @throws NotAEntity 
	 */
	public static void persist(Object entity) throws NotAEntity {
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();
		if(isEntity(c.getName())) {
		// Get list of the declared fields in the class
		@SuppressWarnings("unused")
		Field[] fields =  c.getDeclaredFields();
		
		
		
		@SuppressWarnings("unused")
		Connection dbcon = null;
		try {
			dbcon = getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		@SuppressWarnings("unused")
		String sql = "CREATE ";
		} else {
			throw new NotAEntity("Not in the list of entities to persist");
		}
	}
	
	/**
	 * Queries the database using the provided AQL script.
	 * 
	 * @param query The query to execute on the database.
	 * @return List of objects that match the provided query.
	 */
	public static List<Object> queryDB(String query){
		
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isEntity(String name) {
		System.out.println("isEntity: "+ name);
		return true;
	}

	/**
	 * Gets a connection to the database provided in the awesome.properties file
	 * @return A valid connection to the database
	 * @throws SQLException Thrown if a connection to the database cannot be established
	 */
	private static Connection getConnection() throws SQLException {
		// Create properties for the connections
		Properties connectionProps = new Properties();
		
		// Add user name and passwod to the properties
		connectionProps.put("user", properties.getProperty("user"));
		connectionProps.put("password", properties.getProperty("password"));
		
		// Create the connection and return it
		return DriverManager.getConnection(properties.getProperty("url"),connectionProps);
	}
}
