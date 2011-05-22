package transform;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MyClassAdaptor extends ClassAdapter {

	public MyClassAdaptor(ClassVisitor arg0) {
		super(arg0);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name,
			String desc, String signature, String[] exceptions) {
		System.out.format("visitMethod: access: %d, name: %s, desc: %s, signature: %s", access, name, desc, signature);
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

}
