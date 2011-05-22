package transform;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MyClassAdaptor extends ClassAdapter {

	public MyClassAdaptor(ClassVisitor arg0) {
		super(arg0);
		System.out.println(arg0.toString());
		//this.visitSource(source, debug);
		
	}

	
	@Override
	public void visitSource(String source, String debug) {
		System.out.format("visitSource: source: %s, debug: %s\n", source, debug);
		cv.visitSource(source, debug);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name,
			String desc, String signature, String[] exceptions) {
		System.out.format("visitMethod: access: %d, name: %s, desc: %s, signature: %s\n", access, name, desc, signature);
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

}
