package awesome.persistence.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;



public class LazyInitAgent implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] bytes) throws IllegalClassFormatException {
		ClassReader creader = new ClassReader(bytes);
		ClassWriter cwriter = new ClassWriter(creader, 0);
		ClassAdapter visitor = new LazyInitAdaptor(cwriter);
		creader.accept(visitor, 0);
		
		return cwriter.toByteArray();
	}

	
	
}
