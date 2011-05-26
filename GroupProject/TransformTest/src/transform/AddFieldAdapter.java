package transform;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddFieldAdapter extends ClassAdapter {

	public AddFieldAdapter(ClassVisitor arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	private int fAcc;
	private String fName;
//	private String fDesc;
//	private boolean isFieldPresent;

	private String klassName;

	public AddFieldAdapter(ClassVisitor cv, int fAcc, String fName, String fDesc) {
		super(cv);
		this.fAcc = fAcc;
		this.fName = fName;
//		this.fDesc = fDesc;

		// this.visit(version, access, name, signature, superName, interfaces)
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		this.klassName = name;
		System.out.format("Modifying %s class\n", klassName);
	}

	@Override
	public void visitSource(String source, String debug) {
		System.out
				.format("visitSource: source: %s, debug: %s\n", source, debug);
		cv.visitSource(source, debug);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		System.out.format(
				"visitMethod: access: %d, name: %s, desc: %s, signature: %s\n",
				access, name, desc, signature);
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if (name.equals(fName)) {
			//isFieldPresent = true;
		}

		System.out
				.format(
						"FieldMonitor: access: %d, name: %s, desc: %s, sig: %s, value: %s\n",
						access, name, desc, signature, value.toString());

		return cv.visitField(access, name, desc, signature, value);
	}

	@Override
	public void visitEnd() {
		// if (!isFieldPresent && this.klassName.equals("main/Tempt")) {
		/*
		 * FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null); if
		 * (fv != null) { System.out.println("Added field " + fName);
		 * fv.visitEnd(); }
		 */
		if (this.klassName.equals("main/Tempt")) {
			MethodVisitor mv = cv.visitMethod(fAcc, "get" + fName, "()V", null,
					null);
			if (mv != null) {
				System.out.println("Adding new method");
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitFieldInsn(Opcodes.GETFIELD, this.klassName, "f", "I");
				mv.visitInsn(Opcodes.IRETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			// }
		}
		cv.visitEnd();
	}
}
