package awesome.persistence.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import awesome.persistence.manager.Manager;




public class LazyInitAgent implements ClassFileTransformer {

	private HashSet<String> entities = new HashSet<String>();
	
	
	public void addEntity(String name) {
		entities.add(name);
	}
	
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] b) throws IllegalClassFormatException {
		System.out.println("Lazy Init Agent " + className);// + Manager.isEntity(className));
		ClassReader cr = new ClassReader(b);
		ClassWriter cw = new ClassWriter(cr, 0);
		String[] tmp = className.split("/");
		if( entities.contains(tmp[tmp.length-1]) ) {
			
			cr.accept(new LazyInitAdaptor(cw), 0);
			return cw.toByteArray();
		}
		return null;
	}

	
	
}
