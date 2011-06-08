package awesome.persistence.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	 * 
	 * @throws IOException
	 *             Thrown if the awesome.properties file cannot be loaded
	 */
	public static void setProperties(String propertiesPath) throws IOException,
			PropertiesException {
		// Initialize properties
		properties = new Properties();

		// Load properties from file
		properties.load(new FileInputStream(propertiesPath));

		// Check the required properties are provided
		if (!properties.containsKey("user")
				&& !properties.containsKey("password")
				&& !properties.containsKey("url")) {
			// Invalid properties file, throw exception
			throw new PropertiesException("Invalid properties file provided.");
		}
	}

	/**
	 * Sends an object to the database
	 * 
	 * @param entity The entity to store in the database
	 * @throws EntityException 
	 */
	public static void persist(Object entity) throws NotAEntity, SQLException, EntityException {
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();
		
		// Test if the provided class is valid
		if (!isEntity(c.getName())) {
			throw new NotAEntity("Not in the list of entities to persist");
		}
		
		// Test if a table exists for the entity
		if(!doesTableExist(c.getName().replace('$', '_').replace('.','_') )){
			// Create table
			createTable(entity);				
		}
		
		// Start building sql to insert vals into entity
		StringBuilder insertSql = new StringBuilder("INSERT INTO " 
				+ c.getName().replace('$', '_').replace('.','_')
				+ "(");
		
		// Start building the values part of the sql string
		StringBuilder valsBuilder = new StringBuilder("VALUES(");
		
		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();
		
		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for(int index = 0; index < fields.length - 1; index++){
			
			// Get current field
			Field f = fields[index];
			
			// Add to sql column names
			insertSql.append(f.getName());

			// Split field info on white space
			String[] info = f.toString().split(" ");

			// Get type of field
			String type = info[info.length - 2];

			// Method of the getter for the field
			Method getter;

			// Attempt to get the getter for the field
			try {
				getter = c.getMethod("get" + capitalize(f.getName()), (Class<?>[]) null);
			} catch (Exception e) {
				// the method could not be accessed throw exception
				throw new EntityException("Error accessing getter for field '" + f.getName() + "'");
			}
			
			// Attempt to get the value of the object from the fields getter
			try {
				if (type.equals("java.lang.String")) {
					String s = (String) getter.invoke(entity, (Object[]) null);
					valsBuilder.append("'" + s + "'");
				} else if (type.equals("int")) {
						Integer i = (Integer) getter.invoke(entity, (Object[]) null);
						valsBuilder.append(i.toString());
				} else if (type.equals("boolean")) {
					Boolean b = (Boolean) getter.invoke(entity, (Object[]) null);
					if (b)
						valsBuilder.append("1");
					else
						valsBuilder.append("0");
				} else if (type.equals("double")) {
					Double d = (Double) getter.invoke(entity, (Object[]) null);
					valsBuilder.append(d.toString());
				} else if (type.equals("float")) {
					Float fl = (Float) getter.invoke(entity, (Object[]) null);
					valsBuilder.append(fl.toString());
				} else if (type.equals("char")) {
					Character ch = (Character) getter.invoke(entity, (Object[]) null);
					valsBuilder.append("'" + ch.toString() + "'");
				} else {
					System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
				}
			} catch (Exception e) {
				// the method could not be accessed throw exception
				throw new EntityException("Error accessing getter for field '" + f.getName() + "'");
			}
			// Add apostrophes if not the last value
			if (index != fields.length - 2) {
				valsBuilder.append(", ");
				insertSql.append(", ");
			}
		}
		
		// Finish of sql string
		insertSql.append(") ");
		valsBuilder.append(" ) ");
		
		// Append the strings together
		insertSql.append(valsBuilder.toString());

		// Get connection to the database
		Connection dbcon = getConnection();

		// Get a statement from the database connection
		Statement stmt = dbcon.createStatement();
		
		// Execute the sql on the database
		stmt.execute(insertSql.toString());
		
		// clean up
		stmt.close();
		dbcon.close();
	}

	/**
	 * Queries the database using the provided AQL script.
	 * 
	 * @param query The query to execute on the database.
	 * @return List of objects that match the provided query.
	 * @throws AQLException When the provided query is not valid AQL.
	 * @throws SQLException 
	 * @throws EntityException 
	 */
	public static List<Object> queryDB(String query) throws AQLException, SQLException, EntityException {
		
		// split on white spaces
		String[] args = query.split(" ");
		
		// test that at least 2 words, were provided, the first one is fetch and the second one is a valid entity
		if(args.length < 2 || !args[0].toLowerCase().equals("fetch") || !isEntity(args[1])){
			throw new AQLException("Error in fetch statement.");
		}
		
		// Start building the sql string
		StringBuilder sql = new StringBuilder("SELECT awesome_id FROM " + args[1].replace("$","_").replace(".","_"));
		
		// If the args is longer than 2 then there are where clauses
		if(args.length > 2){
			// Where clause is expected at position 3 in the array
			if(!args[2].toLowerCase().equals("where")){
				throw new AQLException("WHERE clause not provided after entity name.");
			}
			
			// If length is equal to 3 then there is no clauses provided for the where clause
			if(args.length == 3){
				throw new AQLException("No conditions provided for where cluase.");
			}
			
			// Build up the where clauses
			sql.append(" WHERE ");
			for(int index = 3; index < args.length; index++){
				sql.append(" " + args[index]);
			}
		}
		
		// Get database connection
		Connection dbcon = getConnection();
		
		// Get statement from connection
		Statement stmt = dbcon.createStatement();
		
		System.out.println(sql.toString());
		
		// Execute the query
		ResultSet res = stmt.executeQuery(sql.toString());
		
		// Create return list
		List<Object> resultList = new ArrayList<Object>();
		
		// Loop over sql results
		while(res.next()){
			
			// Get the class of the object to persist
			Class<? extends Object> c;
			try {
				c = Class.forName(args[1]);
			} catch (ClassNotFoundException e) {
				//Clean up 
				stmt.close();
				dbcon.close();
				throw new EntityException("Could not find class " + args[1]);
			}
			
			// Create a new instance of the result object
			Object result;
			try {
				result = c.newInstance();
			} catch (Exception e) {
				//Clean up 
				stmt.close();
				dbcon.close();
				throw new EntityException("Could not instanciate class " + args[1]);
			}
			
			// Method of the getter for the field
			Method setter;

			// Attempt to get the setter for the field
			try {
				setter = c.getMethod("setAwesomeId", Integer.class);
			} catch (Exception e) {
				e.printStackTrace();
				//Clean up 
				stmt.close();
				dbcon.close();
				// the method could not be accessed throw exception
				throw new EntityException("Error accessing setter for field 'AwesomeId'");
			}
			
			try {
				setter.invoke(result, res.getInt("awesome_id"));
			} catch (Exception e) {
				//Clean up 
				stmt.close();
				dbcon.close();
				throw new EntityException("Unable to set awesomeId for result object id = " + res.getInt("awesome_id"));
			}

			// Add result to result list
			resultList.add(result);
		}
		
		// return results
		return resultList;
	}

	/**
	 * Gets a individual field for an object from the database.
	 * 
	 * @param className The class name the field is for, used to lookup correct table.
	 * @param awesomeId The awesome_id of the object in the database
	 * @param field The field name to get
	 * @return The field for the object
	 * @throws SQLException If an error occurred when interacting with the database
	 * @throws EntityException If there was an error processing the entity
	 */
	public static Object getField(String className, int awesomeId, String field) throws SQLException, EntityException{
		// Create SQL
		String sql = "SELECT awesome_id, " + field + " FROM " + className.replace(".","_") + " WHERE awesome_id = " + awesomeId;

		// Get database connection
		Connection dbcon = getConnection();
		
		// Get statement from the database connection
		Statement stmt = dbcon.createStatement();
		
		// Execute the query on the database
		ResultSet res =  stmt.executeQuery(sql);
		
		// If there is no result, return null
		if(!res.next()){
			// Clean up
			stmt.close();
			dbcon.close();
			return null;
		}
		
		Class<? extends Object> c;
		
		// Get the class of the object
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// Clean up
			stmt.close();
			dbcon.close();
			return null;
		}
		
		Object out = null;
		
		// Get list of fields
		Field[] fields = c.getDeclaredFields();
		
		// Iterate over the fields until the right field is found
		for(int index = 0; index < fields.length; index++){
			Field f = fields[index];
			
			if(f.getName().equals(field)){
				
				// Split field info on white space
				String[] info = f.toString().split(" ");

				// Get type of field
				String type = info[info.length - 2];
				
				// Switch for field type
				if (type.equals("java.lang.String")) {
					out = res.getString(field);
				} else if (type.equals("int")) {
					out = res.getInt(field);
				} else if (type.equals("boolean")) {
					out = res.getBoolean(field);
				} else if (type.equals("double")) {
					out = res.getDouble(field);
				} else if (type.equals("float")) {
					out = res.getFloat(field);
				} else if (type.equals("char")) {
					out = res.getString(field).charAt(0);
				} else {
					System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
				}
				
				// field found exit loop
				break;
			}
		}
		// Clean up
		stmt.close();
		dbcon.close();
		return out;
	}
	
	/**
	 * Tests if the given class name is a valid entity
	 * @param name
	 * @return
	 */
	public static boolean isEntity(String name) {
		return true;
	}

	/**
	 * Deletes an object from the database
	 * @param className The table to delete from.
	 * @param awesomeId The id to delete from the table
	 * @return True if the operation succeeded, false otherwise
	 * @throws SQLException If the operation was unsuccessful
	 */
	public static boolean deleteFromDb(String className, int awesomeId) throws SQLException{
		// If there is no table then no deletion can be performed
		if(doesTableExist(className)){
			return false;
		}
		
		// Build the sql string
		String sql = "DELETE FROM " + className.replace(".","_") + " WHERE awesome_id = " + awesomeId;
		System.out.println(sql);
		// Get connection to the database
		Connection dbcon = getConnection();
		
		// Get statement from the connection
		Statement stmt = dbcon.createStatement();
		
		// Execute the sql
		stmt.execute(sql);
		
		// clean up
		stmt.close();
		dbcon.close();
		return true;
	}
	
	/**
	 * Gets a connection to the database provided in the awesome.properties file
	 * 
	 * @return A valid connection to the database
	 * @throws SQLException Thrown if a connection to the database cannot be established
	 */
	private static Connection getConnection() throws SQLException{
		
		// Get url from properties file
		String url = properties.getProperty("url");
		
		// Remove quotations
		url = url.substring(1, url.length() - 1);
		
		// Get the driver class
	    try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Unable to connect to database.");
		}
		
	    // Create the connection and return it
		return DriverManager.getConnection(url);
	}
	
	/**
	 * Create a table for the entity in the database
	 * 
	 * @param entity The object to make a table in the database for.
	 */
	private static void createTable(Object entity) throws SQLException{
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();
		
		// Start sql string
		StringBuilder sql = new StringBuilder("CREATE TABLE " + c.getName().replace('$', '_').replace('.','_') + " (");
		sql.append(" awesome_id INTEGER PRIMARY KEY , ");
		
		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();
		
		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for(int index = 0; index < fields.length - 1; index++){
			
			// Get current field
			Field f = fields[index];
			
			sql.append(f.getName());
			
			String[] info = f.toString().split(" ");
			
			String type = info[info.length - 2];
				
			if(type.equals("java.lang.String")){
				sql.append(" VARCHAR(254)");
			}else if(type.equals("int")){
				sql.append(" INT");
			}else if(type.equals("boolean")){
				sql.append(" TINYINT(1)");
			}else if(type.equals("double")){
				sql.append(" DOUBLE PRECISION");
			}else if(type.equals("float")){
				sql.append(" FLOAT");
			}else if(type.equals("char")){
				sql.append(" VARCHAR(1)");
			}else{
				System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
			}
			
			if(index != fields.length - 2){
				sql.append(", ");
			}
			
		}
		
		sql.append(")");
		
		Connection dbcon;
		try {
			dbcon = getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Statement stmt = dbcon.createStatement();
		stmt.executeUpdate(sql.toString());
		dbcon.close();
	}
	
	/**
	 * Capitalizes the first character of a given string
	 * @param s The string to capitalize.
	 * @return A capitalized version of the the string s.
	 */
    private static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
     * Queries the provided sqlite database abd checks if a table for the provided
     * entity exists.
     * 
     * @param tableName The table name to check for
     * @return True if the table exists, false otherwise.
     * @throws Exception
     */
	private static Boolean doesTableExist(String tableName) throws SQLException {
		// Get database connection
		Connection dbcon = getConnection();
		
		// Get statement from connection
		Statement stmt = dbcon.createStatement();
		
		// Query database for table
		ResultSet res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE name='"+ tableName + "'");
		
		boolean out = false;
		// If there is a results then there is already a table
		if (res.next())
			out =  true;
			
		// clean up
		stmt.close();
		dbcon.close();
		
		return out;
	}
}
