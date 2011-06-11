package awesome.persistence.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;


import awesome.persistence.agent.LazyInitAdaptor;

public class LazyInitAgent implements ClassFileTransformer {

	private HashSet<String> entities = new HashSet<String>();
	
	
	public void addEntity(String name) {
		System.out.println("adding " + name);
		entities.add(name);
	}
	
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] b) throws IllegalClassFormatException {
		
		ClassReader cr = new ClassReader(b);
		ClassWriter cw = new ClassWriter(cr, 0);
		//String klassName = className.replace("/", ".");
		//if( entities.contains(klassName)) {
		//	System.out.println("transforming class " + klassName);
			cr.accept(new LazyInitAdaptor(cw, entities), 0);
			return cw.toByteArray();
		//}
		
		//return null;
	}

	
	
}
