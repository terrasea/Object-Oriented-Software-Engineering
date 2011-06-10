package awesome.persistence.agent;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import awesome.persistence.annotations.ID;
import awesome.persistence.manager.EntityException;
import awesome.persistence.manager.Manager;

public class FieldFetcher {
	@SuppressWarnings("unchecked")
	public static <C> void set(C klass, String field) throws Exception {
		Field fieldID = klass.getClass().getDeclaredField(field);
		String table = klass.getClass().getName();//.replace('$', '_').replace('.','_');
		//String table = tmpArray[tmpArray.length - 1];
		//Type type = fieldID.getType();
		
		//String[] tmpArray = type.toString().split("\\.");
		//String typeDesc = tmpArray[tmpArray.length - 1];
		Field[] fields = klass.getClass().getDeclaredFields();
		Object id = null;
		for(Field f : fields) {
			if(f.isAnnotationPresent(ID.class)) {
				f.setAccessible(true);
				id = f.get(klass);
			}
		}
		
		if (id == null) {
			throw new EntityException("No id for entity present");
		}
		Object value = Manager.getField(table, id, field);
		
		fieldID.setAccessible(true);
		if(value != null) {
			if(value instanceof Collection) {
				Collection<Object> c = ((Collection<Object>)fieldID.get(klass));
				c.addAll((Collection<Object>)value);
			} else {
				fieldID.set(klass, value);
			}
		} else {
//		System.out.println("FieldFetcher: " + fieldID.get(klass) + ", "
//				+ fieldID.getType() + ", " + value);
		}
	}
}
