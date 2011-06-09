package awesome.persistence.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
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

import com.sun.xml.internal.rngom.ast.builder.Annotations;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;

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
		if (!properties.containsKey("entities")
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
	 * @throws EntityException
	 */
	public static void persist(Object entity) throws NotAEntity, SQLException,
			EntityException {
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();

		// Test if the provided class is valid
		if (!isEntity(c)){
			throw new NotAEntity("Not in the list of entities to persist - " + entity.getClass().getName());
		}
		
		// If a table does not exist create it
		if(!doesTableExist(c.getName().replace('$', '_').replace('.','_') )){
			createTable(entity);				
		}

		// Start building sql to insert vals into entity
		StringBuilder insertSql = new StringBuilder("INSERT INTO "
				+ c.getName().replace('$', '_').replace('.', '_') + "(");

		// Start building the values part of the sql string
		StringBuilder valsBuilder = new StringBuilder("VALUES(");

		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();

		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for(int fieldsIndex = 0; fieldsIndex < fields.length - 1; fieldsIndex++){
			
			// Get current field
			Field f = fields[fieldsIndex];
			
			// Get annotations for the field
			Annotation[] ans = f.getAnnotations();
			
			// Used to denote if a basic entity
			boolean basic = false;
			
			// Iteration over annotations and extract information
			for(int ansIndex = 0; ansIndex < ans.length; ansIndex++){
				Annotation a = ans[ansIndex];
				
				// Switch for annotation type
				if(a.annotationType().equals(Basic.class)){
					basic = true;
				}
			}
			
			// If not basic then dont process field
			if(!basic){
				continue;
			}
			
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
				getter = c.getMethod("get" + capitalize(f.getName()));
			} catch (Exception e) {
				// the method could not be accessed throw exception
				throw new EntityException("Error accessing getter for field '"
						+ f.getName() + "'\n 1. " + e);
			}

			// Attempt to get the value of the object from the fields getter
			try {
				f.setAccessible(true);
				if (type.equals("java.lang.String")) {
					String s = (String) f.get(entity);
					valsBuilder.append("'" + s + "'");
					
				} else if (type.equals("int")) {
					Integer i = (Integer) f.get(entity);
					valsBuilder.append(i.toString());
				} else if (type.equals("boolean")) {
					Boolean b = (Boolean) f.get(entity);
					if (b)
						valsBuilder.append("1");
					else
						valsBuilder.append("0");
					
				} else if (type.equals("double")) {
					Double d = (Double) f.get(entity);
					valsBuilder.append(d.toString());
					
				} else if (type.equals("float")) {
					Float fl = (Float) f.get(entity);
					valsBuilder.append(fl.toString());
					
				} else if (type.equals("char")) {
					Character ch = (Character) f.get(entity);
					valsBuilder.append("'" + ch.toString() + "'");
					
				} else {
					System.out
							.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
				}
			} catch (Exception e) {
				// the method could not be accessed throw exception
				throw new EntityException("Error accessing getter for field '"
						+ f.getName() + "'\n 2. " + e);
			}
			// Add apostrophes if not the last value
			if (fieldsIndex != fields.length - 2) {
				valsBuilder.append(", ");
				insertSql.append(", ");
			}
		}

		// Finish of sql string
		insertSql.append(") ");
		valsBuilder.append(" ) ");

		// Append the strings together
		insertSql.append(valsBuilder.toString());

		System.out.println(insertSql.toString());
		
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
	 * @param query
	 *            The query to execute on the database.
	 * @return List of objects that match the provided query.
	 * @throws AQLException
	 *             When the provided query is not valid AQL.
	 * @throws SQLException
	 * @throws EntityException
	 */
	public static List<Object> queryDB(String query) throws AQLException,
			SQLException, EntityException {

		// split on white spaces
		String[] args = query.split(" ");
		
		// get class for object
		Class<? extends Object> c;
		try {
			c = Class.forName(args[1]);
		} catch (ClassNotFoundException e1) {
			throw new AQLException("Provided classname is invalid - " + args[1]);
		}	
		
		// test that at least 2 words, were provided, the first one is fetch and the second one is a valid entity
		if(args.length < 2 || !args[0].toLowerCase().equals("fetch") ){
			throw new AQLException("Error in fetch statement.");
		}
		
		if(!isEntity(c)){
			throw new AQLException("Provided object is not an entity.");
		}
		
		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();
		
		// Reference for pk
		Field primaryKey = null;
		
		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for(int fieldsIndex = 0; fieldsIndex < fields.length - 1; fieldsIndex++){
			
			// Get current field
			Field f = fields[fieldsIndex];
			
			// Get annotations for the field
			Annotation[] ans = f.getAnnotations();
			
			// Iteration over annotations and extract information
			for(int ansIndex = 0; ansIndex < ans.length; ansIndex++){
				Annotation a = ans[ansIndex];
				
				// Switch for annotation type
				if(a.annotationType().equals(ID.class)){
					primaryKey = f;
					break;
				}
			}
		}
		
		// If no primary key found
		if(primaryKey == null){
			throw new EntityException("No primary key found for object");
		}

		String pid = primaryKey.getName();
		
		// Start building the sql string
		StringBuilder sql = new StringBuilder("SELECT " + pid + " FROM "
				+ args[1].replace("$", "_").replace(".", "_"));

		// Get database connection
		Connection dbcon = getConnection();

		// Get statement from connection
		Statement stmt = dbcon.createStatement();


		// Execute the query
		ResultSet res = stmt.executeQuery(sql.toString());

		// Create return list
		List<Object> resultList = new ArrayList<Object>();

		// Loop over sql results
		while (res.next()) {

			// Get the class of the object to persist
			Class<? extends Object> c1;
			try {
				c1 = Class.forName(args[1]);
			} catch (ClassNotFoundException e) {
				// Clean up
				stmt.close();
				dbcon.close();
				throw new EntityException("Could not find class " + args[1]);
			}

			// Create a new instance of the result object
			Object result;
			try {
				result = c1.newInstance();
			} catch (Exception e) {
				// Clean up
				stmt.close();
				dbcon.close();
				throw new EntityException("Could not instanciate class "
						+ args[1]);
			}
		
			Object val = null;
			
			// Get datatype of field
			String[] info = primaryKey.toString().split(" ");
			String type = info[info.length - 2];
			
			// Switch for field type, setting val to correct type
			if(type.equals("java.lang.String")){
				val = res.getString(primaryKey.getName());
			}else if(type.equals("int")){
				val = res.getInt(primaryKey.getName());
			}else if(type.equals("boolean")){
				val = res.getBoolean(primaryKey.getName());
			}else if(type.equals("double")){
				val = res.getDouble(primaryKey.getName());
			}else if(type.equals("float")){
				val = res.getFloat(primaryKey.getName());
			}else if(type.equals("char")){
				val = res.getString(primaryKey.getName()).charAt(0);
			}else{
				System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
				continue;
			}

			primaryKey.setAccessible(true);
			
			try {
				primaryKey.set(result, val);
			} catch (Exception e) {
				e.printStackTrace();
				// Clean up
				stmt.close();
				dbcon.close();
				// the method could not be accessed throw exception
				throw new EntityException(
						"Error accessing setter for field 'AwesomeId'");
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
	 * @param fieldName The field name to get
	 * @return The field for the object
	 * @throws SQLException
	 *             If an error occurred when interacting with the database
	 * @throws EntityException
	 *             If there was an error processing the entity
	 */
	public static Object getField(String className, Object primaryKey, String fieldName) throws SQLException, EntityException{
		
		// Get the class of the object
		Class<? extends Object> classN;
		
		try {
			classN = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO fix this
			return null;
		}
		
		// Find primary key
		// Get list of the declared fields in the class
		Field[] fields = classN.getDeclaredFields();
		
		// Holder for the field
		Field pkField = null;
		Field dataField = null;
		
		// Loop over all fields and build SQL string, not processing last index
		// Because it is always a reference to itself
		for(int fieldsIndex = 0; fieldsIndex < fields.length - 1; fieldsIndex++){
			
			// Get current field
			Field f = fields[fieldsIndex];
			
			// if is the field to get, remember at dataField
			if(f.getName().equals(fieldName)){
				dataField = f;
			}
			
			// Get annotations for the field
			Annotation[] ans = f.getAnnotations();
			
			// Iteration over annotations and find primary key
			for(int ansIndex = 0; ansIndex < ans.length; ansIndex++){
				Annotation a = ans[ansIndex];
				
				// Switch for annotation type
				if(a.annotationType().equals(ID.class)){
					pkField = f;
				}
			}
		}
		// If pk empty throw exception
		if(pkField == null){
			throw new EntityException("Could not find primary key for entity " + className);
		}
		
		// if data field empty throw exception
		if(dataField == null){
			throw new EntityException("Could not find primary key for entity " + className + " - " + fieldName);
		}
		
		// Split field info on white space
		String[] info = pkField.toString().split(" ");

		// Get type of field
		String type = info[info.length - 2];
		
		// Format primary key for database use
		String primaryKeyString = null;
		
		if(type.equals("")){
			primaryKeyString = "\"" + primaryKey +  "\"";
		}else{
			primaryKeyString = primaryKey.toString();
		}
		// Create SQL
		String sql = "SELECT " + fieldName + " FROM " + className.replace(".","_") + " WHERE " + pkField.getName() +  " = " + primaryKeyString;

		
		// Get database connection
		Connection dbcon = getConnection();

		// Get statement from the database connection
		Statement stmt = dbcon.createStatement();

		// Execute the query on the database
		ResultSet res = stmt.executeQuery(sql);

		// If there is no result, return null
		if (!res.next()) {
			// Clean up
			stmt.close();
			dbcon.close();
			System.out.println("fail to fetch");
			return null;
		}

		// Split field info on white space
		String[] dataInfo = dataField.toString().split(" ");

		// Get type of field
		String returnType = dataField.getType().getSimpleName();//dataInfo[info.length - 2];
		
		// Pointer for return object
		Object out = null;
		
		System.out.println("Field name: " + fieldName + ", " + dataField.getType().getSimpleName());
		//out = res.getObject(fieldName);
		// Switch for field type
		if (returnType.equals("String")) {
			out = res.getString(fieldName);
		} else if (returnType.equals("int")) {
			out = res.getInt(fieldName);
		} else if (returnType.equals("boolean")) {
			out = res.getBoolean(fieldName);
		} else if (returnType.equals("double")) {
			out = res.getDouble(fieldName);
		} else if (returnType.equals("float")) {
			out = res.getFloat(fieldName);
		} else if (returnType.equals("char")) {
			out = res.getString(fieldName).charAt(0);
		} else {
			System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
		}

		// Clean up
		stmt.close();
		dbcon.close();
		return out;
	}

	/**
	 * Tests if the given class name is a valid entity
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isEntity(Class<? extends Object> entity) {
		// Get entities string from properties
		String ent = properties.getProperty("entities");

		if (ent == null) {
			return false;
		}

		// Split on white space
		String[] list = ent.split(";");
		// Get entity class name
		String className = entity.getName();
		
		// loop over and check if the object name is contained in the entities list
		for(int index = 0; index < list.length; index++){
			// Test if equal, if so return true
			if (list[index].equals(className))
				return true;
		}

		// Entity not provided in properties file
		return false;
	}

	/**
	 * Deletes an object from the database
	 * 
	 * @param className The table to delete from.
	 * @param awesomeId The id to delete from the table
	 * @return True if the operation succeeded, false otherwise
	 * @throws SQLException If the operation was unsuccessful
	 * @throws EntityException 
	 */
	public static boolean deleteFromDb(Object obj) throws SQLException, EntityException{
		String className = obj.getClass().getName();
		
		// If there is no table then no deletion can be performed
		if (doesTableExist(className)) {
			return false;
		}
		
		// GET PRIMARY KEY AND THEN SET SQL
		
		// Get the fields for the objects class
		Field[] fields = obj.getClass().getDeclaredFields();
		
		Field primaryKey = null;
		// Iterate till the primary key is found
		for(int index = 0; index < fields.length; index++){
			Annotation[] anns = fields[index].getAnnotations();
			
			// Loop over annotations
			for(int ansIndex = 0; ansIndex < anns.length; ansIndex++){
				
				// If annotation is class set primary key and leave loop
				if(anns[ansIndex].annotationType().equals(ID.class)){
					primaryKey = fields[index];
					break;
				}
			}
		}
		
		// Test if primary key is null
		if(primaryKey == null)
			throw new EntityException("No primary key for entity - " + obj.getClass().toString());
		
		primaryKey.setAccessible(true);
		
		Object val = null;

		try {
			val = primaryKey.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EntityException("Primary key for obj could not be accessed.");
		}
		
		
		// Build the SQL string
		String sql = "DELETE FROM " + className.replace(".","_") + " WHERE " + primaryKey.getName()+ " = " + val.toString();
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
	 * @throws SQLException
	 *             Thrown if a connection to the database cannot be established
	 */
	private static Connection getConnection() throws SQLException {

		// Get url from properties file
		String url = properties.getProperty("url");

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
	 * @throws EntityException 
	 */
	private static void createTable(Object entity) throws SQLException, EntityException{
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();

		// Start sql string
		StringBuilder sql = new StringBuilder("CREATE TABLE " + c.getName().replace('$', '_').replace('.','_') + " (");
		//sql.append(" awesome_id INTEGER PRIMARY KEY , ");
		
		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();
		
		// Store the primary key
		boolean primaryKey = false;
		// Flag for if field has been added to sql
		boolean fieldAdded = false;
		
		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for (int index = 0; index < fields.length - 1; index++) {

			// Get current field
			Field f = fields[index];
			
			// Get the list of annotations
			Annotation[] ans = f.getAnnotations();			
			
			// Denotes if the field is basic
			boolean basic = false;
			boolean pk = false;
			
			// Iteration over annotations and extract information
			for(int ansIndex = 0; ansIndex < ans.length; ansIndex++){
				Annotation a = ans[ansIndex];
				
				// Switch for annotation type
				if(a.annotationType().equals(Basic.class)){
					basic = true;
				}else if(a.annotationType().equals(ID.class)){
					if(primaryKey){
						throw new EntityException("Primary key declared for two fiels.");
					}else{
						primaryKey = true;
						pk = true;
					}
				}
			}
			
			// If type is not basic ignore
			if(!basic){
				// If id was set then throw error for primary key not being annotated with basic
				if(pk)
					throw new EntityException("Primary key declared for non basic field - " + f.getName());
				continue;
			}
			
			// add name to SQL
			if(fieldAdded)
				sql.append(", " + f.getName());
			else
				sql.append(f.getName());
			
			// set flag for field added
			fieldAdded = true;
			
			// Get datatype of field
			String[] info = f.toString().split(" ");
			String type = info[info.length - 2];
			
			// Switch for field type, adding type of field to sql
			if(type.equals("java.lang.String")){
				sql.append(" VARCHAR(254)");
			} else if (type.equals("int")) {
				sql.append(" INT");
			} else if (type.equals("boolean")) {
				sql.append(" TINYINT(1)");
			} else if (type.equals("double")) {
				sql.append(" DOUBLE PRECISION");
			} else if (type.equals("float")) {
				sql.append(" FLOAT");
			} else if (type.equals("char")) {
				sql.append(" VARCHAR(1)");
			} else {
				System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
				continue;
			}
			
			// If the field is the primary key add to sql
			if(pk){
				sql.append(" PRIMARY KEY");
			}
		}
		
		// Finish SQL
		sql.append(")");
		
		System.out.println(sql.toString());
		
		// Get database connection
		Connection dbcon;
		try {
			dbcon = getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// Get statement from the database connection
		Statement stmt = dbcon.createStatement();
		
		// Execute command
		stmt.executeUpdate(sql.toString());
		
		//Clean up
		stmt.close();
		dbcon.close();
	}

	/**
	 * Capitalizes the first character of a given string
	 * 
	 * @param s
	 *            The string to capitalize.
	 * @return A capitalized version of the the string s.
	 */
    private static String capitalize(String s) {
    	// String empty, return s
        if (s.length() == 0) return s;
        
        // Capitalize first char and return
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
     * Queries the provided sqlite database and checks if a table for the provided
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
		ResultSet res = stmt
				.executeQuery("SELECT name FROM sqlite_master WHERE name='"
						+ tableName + "'");

		// result
		boolean out = false;

		// If there is a result then there is already a table
		if (res.next())
			out = true;

		// clean up
		stmt.close();
		dbcon.close();

		return out;
	}
}
