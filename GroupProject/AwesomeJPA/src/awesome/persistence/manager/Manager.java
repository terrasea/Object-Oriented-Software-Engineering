package awesome.persistence.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import awesome.persistence.annotations.Entity;

/**
 * 
 * Manager class
 *
 */

public class Manager {
	
	// Properties extracted from the awesome.properties file
	private Properties properties;
	
	/**
	 * Constructor for the manager, 
	 * @throws IOException Thrown if the awesome.properties file cannot be loaded
	 */
	public Manager(String propertiesPath) throws IOException, PropertiesException{
		// Initialise properties
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
	 */
	public void persist(Object entity) {
		
	}
	
	/**
	 * Starts a transaction with the database
	 */
	public void startTransaction() {
		
	}
	
	/**
	 * Commits the current transaction to the database
	 */
	public void commitTransaction() {
		
	}
	
	/**
	 * Roll back the last transaction on the database
	 */
	public void rollBack() {
		
	}
	
	/**
	 * 
	 * @param entity
	 */
	protected void transformEntity(Entity entity) {
		
	}

	/**
	 * Gets a connection to the database provided in the awesome.properties file
	 * @return A valid connection to the database
	 * @throws SQLException Thrown if a connection to the database cannot be established
	 */
	private Connection getConnection() throws SQLException {
		// Create properties for the connections
		Properties connectionProps = new Properties();
		
		// Add user name and passwod to the properties
		connectionProps.put("user", properties.getProperty("user"));
		connectionProps.put("password", properties.getProperty("password"));
		
		// Create the connection and return it
		return DriverManager.getConnection(properties.getProperty("url"),connectionProps);
	}
}
