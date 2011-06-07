package awesome.persistence.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
	 * @param entity
	 *            The entity to store in the database
	 * @throws NotAEntity
	 */
	public static void persist(Object entity) throws SQLException, NotAEntity {
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();
		
		// Test if the provided class is valid
		if (isEntity(c.getName())) {

			// Test if table exists
			String sql = "IF EXISTS(SELECT [awesome_id] FROM " 
				+ c.getName().replace('$', '_').replace('.','_') 
				+ ")";
			
			// Create table
			createTable(entity);
			
			// Insert into table
			StringBuilder insertSql = new StringBuilder("INSERT INTO " 
					+ c.getName().replace('$', '_').replace('.','_')
					+ "(");
			
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
				
				try {
					getter = c.getMethod("get" + capitalize(f.getName()), null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} 
				
				if(type.equals("java.lang.String")){
					try {
						String s = (String) getter.invoke(entity, null);
						valsBuilder.append("'" + s + "'");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}else if(type.equals("int")){
					try {
						Integer i = (Integer) getter.invoke(entity, null);
						valsBuilder.append(i.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}else if(type.equals("boolean")){
					try {
						Boolean b = (Boolean) getter.invoke(entity, null);
						if(b)
							valsBuilder.append("1");
						else
							valsBuilder.append("0");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}else if(type.equals("double")){
					try {
						Double d = (Double) getter.invoke(entity, null);
						valsBuilder.append(d.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}else if(type.equals("float")){
					try {
						Float fl = (Float) getter.invoke(entity, null);
						valsBuilder.append(fl.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}else if(type.equals("char")){
					try {
						Character ch = (Character) getter.invoke(entity, null);
						valsBuilder.append("'" + ch.toString() + "'");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}else{
					System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
				}
				
				if(index != fields.length - 2){
					valsBuilder.append(", ");
					insertSql.append(", ");
				}
			}
			insertSql.append(") ");
			valsBuilder.append(" ) ");
			
			String outputSql = insertSql.toString() + valsBuilder.toString();
			System.out.println("OUT - " + outputSql);
		} else {
			throw new NotAEntity("Not in the list of entities to persist");
		}
		Connection dbcon;
		try {
			dbcon = getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Queries the database using the provided AQL script.
	 * 
	 * @param query
	 *            The query to execute on the database.
	 * @return List of objects that match the provided query.
	 */
	public static List<Object> queryDB(String query) {

		return null;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isEntity(String name) {
		System.out.println("isEntity: " + name);
		return true;
	}

	/**
	 * Gets a connection to the database provided in the awesome.properties file
	 * 
	 * @return A valid connection to the database
	 * @throws Exception
	 *             Thrown if a connection to the database cannot be established
	 */
	private static Connection getConnection() throws Exception {
		// Get url from properties file
		String url = properties.getProperty("url");
		// Remove quotations
		url = url.substring(1, url.length() - 1);
		// Get the driver class
	    Class.forName("org.sqlite.JDBC");
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
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + c.getName().replace('$', '_').replace('.','_') + " (");
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
			
			System.out.println("type - " + info[info.length - 2]);
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
	
    private static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
