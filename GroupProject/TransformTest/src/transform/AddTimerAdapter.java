package transform;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

public class AddTimerAdapter extends ClassAdapter {
	private String owner;
	private boolean isInterface;

	public AddTimerAdapter(ClassVisitor cv) {
		super(cv);
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		owner = name;
		isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
				exceptions);
		if (!isInterface && mv != null && !name.equals("<init>")) {
			mv = new AddTimerMethodAdapter(mv);
		}
		return mv;
	}

	@Override
	public void visitEnd() {
		if (!isInterface) {
			FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "timer",
					"J", null, null);
			if (fv != null) {
				fv.visitEnd();
			}
		}
		cv.visitEnd();
	}

	class AddTimerMethodAdapter extends MethodAdapter {
		public AddTimerMethodAdapter(MethodVisitor mv) {
			super(mv);
		}

		@Override
		public void visitCode() {
			mv.visitCode();
			mv.visitFieldInsn(Opcodes.GETSTATIC, owner, "timer", "J");
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
					"currentTimeMillis", "()J");
			mv.visitInsn(Opcodes.LSUB);
			mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, "timer", "J");
		}

		@Override
		public void visitInsn(int opcode) {
			if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
				mv.visitFieldInsn(Opcodes.GETSTATIC, owner, "timer", "J");
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
						"currentTimeMillis", "()J");
				mv.visitInsn(Opcodes.LADD);
				mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, "timer", "J");
			}
			mv.visitInsn(opcode);
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			mv.visitMaxs(maxStack + 4, maxLocals);
		}
	}
}
