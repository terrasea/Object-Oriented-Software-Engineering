package awesome.persistence.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import awesome.persistence.manager.Manager;




public class LazyInitAgent implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] b) throws IllegalClassFormatException {
		//System.out.println("Lazy Init Agent");
		ClassReader cr = new ClassReader(b);
		ClassWriter cw = new ClassWriter(cr, 0);
		if( Manager.isEntity(className) ) {
			
			cr.accept(new LazyInitAdaptor(cw), 0);
			return cw.toByteArray();
		}
		return null;
	}

	
	
}
