package awesome.persistence.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;



/**
 * 
 * Class that has the method to extract the annotation data from the provided object
 *
 */
public class AnnotationReader {

	@SuppressWarnings("rawtypes")
	public static void readAnnotations(Object obj){
		
		// Get the class of the object
		Class c = obj.getClass();
		
		// Get list of the declared fields in the class
		Field[] fields =  c.getDeclaredFields();

		for(int index = 0; index < fields.length; index++){
			Field f = fields[index];
			System.out.println("Printing annotations for - " + f.toString());
			
			Annotation[] annotations = f.getAnnotations();
			
			for(int index2 = 0; index2 < annotations.length; index2++){
				System.out.println("\t" + annotations[index2].toString());
			}
		}
	}
	
}
