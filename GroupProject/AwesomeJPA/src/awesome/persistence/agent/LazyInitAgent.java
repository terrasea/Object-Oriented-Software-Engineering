package awesome.persistence.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;




public class LazyInitAgent implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] b) throws IllegalClassFormatException {
		//System.out.println("Lazy Init Agent");
		System.out.println("transform: " + className);
		ClassReader cr = new ClassReader(b);
		ClassWriter cw = new ClassWriter(cr, 0);
		if( className.endsWith("Instance") ) {
			
			System.out.println("transform3: " + className);
			cr.accept(new LazyInitAdaptor(cw), 0);
			System.out.println("transform4: " + className);
			return cw.toByteArray();
		}
		System.out.println("transform2: " + className);
		return null;
	}

	
	
}
