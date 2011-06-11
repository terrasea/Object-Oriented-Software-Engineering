package awesome.persistence.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;
import awesome.persistence.annotations.ManyToOne;
import awesome.persistence.annotations.OneToMany;

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
		
		/*try {
			LazyInitAgent clt = new LazyInitAgent();
			String[] entities = properties.getProperty("entities").split(";");
			for(String entity: entities) {
				clt.addEntity(entity);
			}
			Transformer.addTransformer(clt);
			Transformer.startAgent();
		} catch (AgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	
	
	@SuppressWarnings("unchecked")
	private static <E> void handleOneToMany(Field f, E entity) throws NotAEntity, SQLException, EntityException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		Collection<Object> objs = null;
		try {
			objs = (Collection<Object>) f.get(entity);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(objs == null) {
			return;
		}
		Object obj = objs.size() > 0 ? objs.toArray()[0] : null;
		String objName = null;
		if(obj != null) {
			for(Field field: obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				ManyToOne mm2o = field.getAnnotation(ManyToOne.class);
				try {
					if(mm2o != null && field.get(entity).getClass().getName().equals(mm2o.target().getName())) {
						objName = field.getName();
						System.out.println("Name retrieved");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		System.out.println("One to many before loop");
		for(Object obj2: objs) {
			Field field = null;
			field = obj2.getClass().getDeclaredField(objName);
			field.setAccessible(true);
			field.set(obj2, entity);
			System.out.println("One to many add: "+ field.get(obj2));
			Manager.persist(obj2);
		}
	}
	
	
	private static void handleManyToOne(Field f, Collection<String> valsList, Object entity) throws IllegalArgumentException, EntityException, IllegalAccessException {
		f.setAccessible(true);
		Object fkObject = f.get(entity);
		if(fkObject == null) {
			valsList.add("NULL");
			return;
		}
		Field pkfield = Manager.getPrimaryKeyField(fkObject.getClass());
		if(pkfield == null) {
			valsList.add("NULL");
			return;
		}
		pkfield.setAccessible(true);
		System.out.println("1 Entity in many to one = " + entity + ", " + pkfield.getName());
		Object tmp = pkfield.getInt(fkObject);
		System.out.println("1a Entity in many to one = " + entity + ", " + pkfield.getName());
		if(tmp != null) {
			System.out.println("2 Entity in many to one = " + entity + ", " + pkfield.getName());
			String type = tmp.getClass().getCanonicalName();
			if (type.equals("java.lang.String")) {
				String s = (String) tmp;
				valsList.add("'" + s + "'");
				
			} else if (type.equals("int")) {
				System.out.println("3 Entity in many to one = " + entity + ", " + pkfield.getName());
				Integer i = (Integer) tmp;
				valsList.add(i.toString());
				System.out.println("4 Entity in many to one = " + entity + ", " + pkfield.getName());
			} else if (type.equals("boolean")) {
				Boolean b = (Boolean) tmp;
				if (b)
					valsList.add("1");
				else
					valsList.add("0");
				
			} else if (type.equals("double")) {
				Double d = (Double) tmp;
				valsList.add(d.toString());
				
			} else if (type.equals("float")) {
				Float fl = (Float) tmp;
				valsList.add(fl.toString());
				
			} else if (type.equals("char")) {
				Character ch = (Character) tmp;
				valsList.add("'" + ch.toString() + "'");
				
			}
		}
	}
	
	
	
	/**
	 * Sends an object to the database
	 * 
	 * @param entity
	 *            The entity to store in the database
	 * @throws EntityException
	 */
	public static void persist(Object entity) throws NotAEntity, SQLException, EntityException {
		// Get the class of the object
		Class<? extends Object> c = entity.getClass();

		// Test if the provided class is valid
		if (!isEntity(c)){
			throw new NotAEntity("Not in the list of entities to persist - " + entity.getClass().getName());
		}
		
		// If a table does not exist create it
		if(!doesTableExist(c.getName().replace('$', '_').replace('.','_') )){
			createTable(entity.getClass());				
		}

		// Flag to to signal if the command will be an update or insert
		boolean updating = false;
		
		// Test if the object exists in the database
		if(isPersisted(entity)){
			updating = true;
		}
		
		// Start building sql to insert vals into entity
		List<String> nameList = new ArrayList<String>();
		
		// Start building the values part of the sql string
		List<String> valsList =  new ArrayList<String>();
		
		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();
		
		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for(int fieldsIndex = 0; fieldsIndex < fields.length; fieldsIndex++){
			System.out.println("" + fieldsIndex + ": " + fields[fieldsIndex].getName());
			// Get current field
			Field f = fields[fieldsIndex];
			
			// Get annotations for the field
			Annotation[] ans = f.getAnnotations();
			
			// Used to denote if a basic entity
			boolean basic = false;
			boolean pk = false;
			boolean m2o = false;
			boolean o2m = false;
			
			// Iteration over annotations and extract information
			for(int ansIndex = 0; ansIndex < ans.length; ansIndex++){
				Annotation a = ans[ansIndex];
				
				// Switch for annotation type
				if(a.annotationType().equals(Basic.class)){
					basic = true;
				}
				
				// test for id class
				if(a.annotationType().equals(ID.class)) {
					pk = true;
				}
				
				if(a.annotationType().equals(ManyToOne.class)) {
					m2o = true;
				} else if(a.annotationType().equals(OneToMany.class)) {
					o2m = true;
				}
			}
			
			// If not basic then dont process field
			if(!basic && !pk && !m2o || o2m){
				continue;
			}
			
			// Add to sql column names
			nameList.add(f.getName());

			// Get type of field
			String type = f.getType().getCanonicalName();


			// Attempt to get the value of the object from the fields getter
			try {
				f.setAccessible(true);
				if (type.equals("java.lang.String")) {
					String s = (String) f.get(entity);
					valsList.add("'" + s + "'");
				}else if(type.equals("java.util.Date")){
					Date d = (Date) f.get(entity);
					valsList.add(Long.toString(d.getTime()));
					
					
				} else if (type.equals("int")) {
					Integer i = (Integer) f.get(entity);
					valsList.add(i.toString());
				} else if (type.equals("boolean")) {
					Boolean b = (Boolean) f.get(entity);
					if (b)
						valsList.add("1");
					else
						valsList.add("0");
					
				} else if (type.equals("double")) {
					Double d = (Double) f.get(entity);
					valsList.add(d.toString());
					
				} else if (type.equals("float")) {
					Float fl = (Float) f.get(entity);
					valsList.add(fl.toString());
					
				} else if (type.equals("char")) {
					Character ch = (Character) f.get(entity);
					valsList.add("'" + ch.toString() + "'");
					
				} else {
					
					if(o2m) {
						Manager.handleOneToMany(f, entity);
						System.out.println("One to many");
						continue;
					} else if(m2o) {
						Manager.handleManyToOne(f, valsList, entity);
						System.out.println("Many to one");
					}
					System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + type);
					// Set field to be accessable
					f.setAccessible(true);
					// Get the object
					Object plojo = f.get(entity);
					if(plojo != null) {
						System.out.println("Manager.persist plojo");
						// Store it in the database
						persist(plojo);
					} else {
						continue;
					}
					
					
					// get the primary key for the plojo
					Field primaryKey = getPrimaryKeyField(f.getType());
					
					// set primary key to accessible
					primaryKey.setAccessible(true);
					
					// Get the value of the primary key
					Object val = primaryKey.get(plojo);
					if(val != null) {
						// add value to vals string
						valsList.add(val.toString());
					} else {
						continue;
					}
				}
			} catch (Exception e) {
				// the method could not be accessed throw exception
				//throw new EntityException("Error accessing getter for field '"+ f.getName() + "'\n 2. " + e);
				e.printStackTrace();
				return;
			}

		}
		
		// Build up the output sql string
		StringBuilder sql = new StringBuilder();
		
		// If updating, build update statement, else build insert statement
		if(updating){
			// Start update statement
			sql.append("UPDATE " + c.getName().replace('$', '_').replace('.', '_'));
			sql.append(" SET ");
			
			// add  setting fields
			for(int index = 0; index < nameList.size(); index++){
				sql.append(nameList.get(index) + " = " + valsList.get(index));
				
				if(index != nameList.size() - 1)
					sql.append(",");
			}
			
			// Set where primary key = entity primary key
			sql.append(" WHERE ");
			
			// get the primary key
			Field primaryKey = getPrimaryKeyField(c);
			
			// set field to be accessible
			primaryKey.setAccessible(true);
			
			// add name to where clause
			sql.append(primaryKey.getName() + " = ");
			
			// get primary key value
			Object val = null;
			try {
				val = primaryKey.get(entity);
			} catch (Exception e) {
				throw new EntityException("Could not access primary key for entity - " + c.getCanonicalName());
			}
			// add primary key value to where clause
			sql.append(val.toString());
			
		}else{
			// build insert statement
			sql.append("INSERT INTO " + c.getName().replace('$', '_').replace('.', '_') + " ( ");
			// Loop over name list
			for(int index = 0; index < nameList.size(); index++){
				sql.append(nameList.get(index));
				
				if(index != nameList.size() - 1)
					sql.append(",");
			}
			// add values
			sql.append(") VALUES (");
			// Loop over values list
			for(int index = 0; index < valsList.size(); index++){
				sql.append(valsList.get(index));
				
				if(index != valsList.size() - 1)
					sql.append(",");
			}
			// finish SQL
			sql.append(")");
		}
		// Print sql
		System.out.println(sql.toString());
		
		// Get connection to the database
		Connection dbcon = getConnection();

		// Get a statement from the database connection
		Statement stmt = dbcon.createStatement();

		// Execute the sql on the database
		stmt.execute(sql.toString());

		// clean up
		stmt.close();
		dbcon.close();
	}

	/**
	 * Tests if an object has already been stored in the database, by checking if the primary
	 * key is in the db.
	 * 
	 * @param obj
	 * @return
	 * @throws EntityException
	 */
	private static boolean isPersisted(Object obj) throws EntityException{
		try {
			// Get the primary key for the object
			Field pk = getPrimaryKeyField(obj.getClass());
			// set the field to be accessible
			pk.setAccessible(true);
			// get the primary key field for the object
			Object res = getField(obj.getClass().getCanonicalName(), pk.get(obj) ,pk.getName());
			
			// if there is a primary key then the object is already persisted
			if(res != null)
				return true;
			
			// Object not persisted
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EntityException("Could not determine id entity was persited.");
		}
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
		
		// test if object provided is a valid entity
		if(!isEntity(c)){
			throw new AQLException("Provided object is not an entity.");
		}
		
		if(args.length > 2){
			if(!args[2].toLowerCase().equals("where"))
				throw new AQLException("Where clause expceted after the class name.");
		}
		
		// Reference for pk
		Field primaryKey = getPrimaryKeyField(c);
	
		String pid = primaryKey.getName();
		
		// Start building the sql string
		StringBuilder sql = new StringBuilder("SELECT " + pid + " FROM " + args[1].replace("$", "_").replace(".", "_"));

		for(int index = 2; index < args.length; index++){
			sql.append(" " + args[index]);
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
				throw new EntityException("Could not instanciate class " + args[1]);
			}
		
			Object val = null;
			
			// Get datatype of field
			String type = primaryKey.getType().getCanonicalName();
			
			// Switch for field type, setting val to correct type
			if(type.equals("java.lang.String")){
				val = res.getString(primaryKey.getName());
			}else if(type.equals("java.util.Date")){
				Long l = res.getLong(primaryKey.getName());
				val = new Date(l);
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

		// Clean up
		stmt.close();
		dbcon.close();
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
			throw new EntityException("Invalid class name provided - " + className);
		}

		// Check if valid entity
		if(!isEntity(classN)){
			throw new EntityException("Class name is not an entity - " + className);
		}
		
		// Get the field that we want to return the value for
		Field dataField;
		try {
			dataField = classN.getDeclaredField(fieldName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EntityException("Could not access the field " + fieldName);
		}

		// Get the primary key for the object
		Field primaryKeyField = getPrimaryKeyField(classN);
		
		// Get type of field
		String type = primaryKey.getClass().getCanonicalName();
		
		// Format primary key for database use
		String primaryKeyString = null;
		
		// add brackets for string types
		if(type.equals("java.lang.String") || type.equals("char")){
			primaryKeyString = "\"" + primaryKey +  "\"";
		}else if(type.equals("java.util.Date")){
			Date d = (Date)primaryKey;
			primaryKeyString = Long.toString(d.getTime());
		}else{
			primaryKeyString = primaryKey.toString();
		}
		
		System.out.println(dataField.getName());
		OneToMany o2m = dataField.getAnnotation(OneToMany.class);
		System.out.println(o2m);
		
		if(o2m != null) {
			Class<?> target = o2m.mappedBy();
			System.out.println(target.getName());
			String targetFK = null;
			for(Field field: target.getDeclaredFields()) {
				System.out.println("field: " + field.getName());
				ManyToOne m2o = field.getAnnotation(ManyToOne.class);
				if(m2o != null) {
					System.out.println("Target: " + m2o.target() + ", " + classN);
				}
				try {
					
					if(m2o != null && m2o.target().equals(classN)) {
						targetFK = field.getName();
						
						//Object coll = dataField.get(classN);
						//System.out.println(coll);
						break;
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(targetFK == null) {
				System.out.println("targetFK is null");
				return null;
			}
			try {
				String query = String.format("FETCH %s WHERE %s='%s'", target.getName(), targetFK, primaryKey);
				System.out.println(query);
				List<Object> entities = Manager.queryDB(query);
				
				//ArrayList<Object> tmp = entities;
				
				//return list of objects as a array 
				return entities;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Create SQL
		String sql = "SELECT " + fieldName + " FROM " + className.replace(".","_") + " WHERE " + primaryKeyField.getName() +  " = " + primaryKeyString;

		System.out.println(sql);
		
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
			System.out.println("Nothing to fetch field from db - " + fieldName);
			return null;
		}

		// Get type of field
		String returnType = dataField.getType().getCanonicalName();
		
		// Pointer for return object
		Object out = null;
		
		// Attempt to get object from sql results
		try{
			out = getObjFromRes(res, fieldName, returnType);
		}catch(Exception nonPrimative){
			// The result is not a primitive
			System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + returnType);
			
			// Create new instance of the field type
			try {
				out = dataField.getType().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				throw new EntityException("Cannot create new instance of - " + returnType);
			}
			
			// get the Primary key for the field type
			Field outPrimaryKey = getPrimaryKeyField(dataField.getType());
			
			// get access for the primary key
			outPrimaryKey.setAccessible(true);
			try{
				// set the primary key for the return obj
				outPrimaryKey.set(out, getObjFromRes(res, fieldName, outPrimaryKey.getType().getCanonicalName()));	
			}catch(Exception e){
				e.printStackTrace();
				throw new EntityException("Could not set primary key for - " + out.getClass().getCanonicalName());
			}
		}

		// Clean up
		stmt.close();
		dbcon.close();
		return out;
	}

	/**
	 * 
	 * @param res
	 * @param fieldName
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private static Object getObjFromRes(ResultSet res,String fieldName , String type) throws Exception{
		if (type.equals("java.lang.String")) {
			return res.getString(fieldName);
		} else if (type.equals("int")) {
			return res.getInt(fieldName);
		} else if (type.equals("boolean")) {
			return res.getBoolean(fieldName);
		} else if (type.equals("double")) {
			return res.getDouble(fieldName);
		} else if (type.equals("float")) {
			return res.getFloat(fieldName);
		} else if (type.equals("char")) {
			return res.getString(fieldName).charAt(0);
		} else {
			throw new Exception("Object type is non primative");
		}
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
		Field primaryKey = getPrimaryKeyField(obj.getClass());

		primaryKey.setAccessible(true);
		
		// Get value for the primary key field
		Object val = null;
		try {
			val = primaryKey.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EntityException("Primary key for obj could not be accessed.");
		}
		
		// Get type of field
		String type = primaryKey.getType().getCanonicalName();
		
		// Format primary key for database use
		String primaryKeyString = null;
		
		// add brackets for string types
		if(type.equals("java.lang.String") || type.equals("char")){
			primaryKeyString = "\"" + val +  "\"";
		}else if(type.equals("java.util.Date")){
			Date d = (Date)val;
			primaryKeyString = Long.toString(d.getTime());
		}else{
			primaryKeyString = val.toString();
		}
		
		// Build the SQL string
		String sql = "DELETE FROM " + className.replace(".","_") + " WHERE " + primaryKey.getName()+ " = " + primaryKeyString;
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
	private static void createTable(Class<? extends Object> c) throws SQLException, EntityException{

		// Start sql string
		StringBuilder sql = new StringBuilder("CREATE TABLE " + c.getName().replace('$', '_').replace('.','_') + " (");
		
		// Get list of the declared fields in the class
		Field[] fields = c.getDeclaredFields();
		
		// Store the primary key
		boolean primaryKey = false;
		// Flag for if field has been added to sql
		boolean fieldAdded = false;
		
		StringBuilder foreignKeys = new StringBuilder();
		
		// Loop over all fields and build sql string, not processing last index
		// Because it is always a reference to itself
		for (int index = 0; index < fields.length; index++) {

			// Get current field
			Field f = fields[index];
			
			// Get the list of annotations
			Annotation[] ans = f.getAnnotations();			
			
			// Denotes if the field is basic
			boolean basic = false;
			boolean pk = false;
			boolean o2m = false;
			boolean m2o = false;
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
				} else if(a.annotationType().equals(OneToMany.class)) {
					o2m = true;
				} else if(a.annotationType().equals(ManyToOne.class)) {
					m2o = true;
				}
			}
			
			// If type is not basic ignore
			if(!basic && !pk && !m2o || o2m){
				continue;
			}
			
			// add name to SQL
			if(fieldAdded)
				sql.append(", " + f.getName());
			else
				sql.append(f.getName());
			
//			if(m2o) {
//				sql.append("_fk");
//			}
			
			// set flag for field added
			fieldAdded = true;
			
			// Get datatype of field
			String type;
			try {
				type = convertToSqlType(  f.getType().getCanonicalName());
			} catch (Exception e) {
				System.out.println("OBJECT TYPE IS NOT PRIMATIVE - " + f.getType().getCanonicalName());
				createTable(f.getType());
				
				Field foreignKey = getPrimaryKeyField(f.getType());
				foreignKeys.append(", FOREIGN KEY (" + f.getName() + ") REFERENCES " + f.getType().getCanonicalName().replace('.', '_').replace('$','_') );
				try {
					type = convertToSqlType(foreignKey.getType().getCanonicalName());
				} catch (Exception e1) {
					throw new EntityException("Foreign key type is non primative - " + foreignKey.getType().getCanonicalName());
				}
			}
			
			sql.append(" " + type);
			
			// If the field is the primary key add to sql
			if(pk){
				sql.append(" PRIMARY KEY");
			}
		}
		
		// Finish SQL
		sql.append(foreignKeys.toString() + ")");
		
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
		ResultSet res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE name='"
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

	/**
	 * 
	 * @param javaType
	 * @return
	 * @throws Exception
	 */
	private static String convertToSqlType(String javaType) throws Exception {
		if(javaType.equals("java.lang.String")){
			return " VARCHAR(254)";
		}else if(javaType.equals("java.util.Date")){
			return " BIGINT";
		} else if (javaType.equals("int")) {
			return " INT";
		} else if (javaType.equals("boolean")) {
			return " TINYINT(1)";
		} else if (javaType.equals("double")) {
			return " DOUBLE PRECISION";
		} else if (javaType.equals("float")) {
			return " FLOAT";
		} else if (javaType.equals("char")) {
			return " VARCHAR(1)";
		} else {
			throw new Exception("Object type not a primative " + javaType);
		}
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 * @throws EntityException
	 */
	private static Field getPrimaryKeyField(Class<? extends Object> c) throws EntityException{
		// Get the fields for the objects class
		Field[] fields = c.getDeclaredFields();
		
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
			throw new EntityException("No primary key for entity - " + c.toString());
		
		return primaryKey;
	}
}
