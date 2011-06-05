package awesome.persistence.agent;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class LazyInitAdaptor extends ClassAdapter {

	private String owner;
	private boolean isInterface;
	
	public LazyInitAdaptor(ClassVisitor cv) {
		super(cv);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		owner = name;
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
	}
	
	
	@Override
	public void visitEnd() {
		if (!isInterface) {
			FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC, "fields", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Boolean;>;", null);
			if (fv != null) {
				fv.visitEnd();
			}
		}
		cv.visitEnd();
	}

}
