package awesome.persistence.agent;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


import awesome.persistence.manager.Manager;

public class LazyInitAdaptor extends ClassAdapter {

	private String owner;
	private boolean isEntity;
	
	
	public LazyInitAdaptor(ClassVisitor cv) {
		super(cv);
		System.out.println("Lazy Init Adaptor");
	}
	
	
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		owner = name;
		System.out.println("Lazy Init Adaptor");
		isEntity = Manager.isEntity(name);
	}
	
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
				exceptions);
		if (isEntity && mv != null && name.equals("<init>")) {
			mv = new LazyInitMethodAdapter(mv, owner);
		} else {
			//transform getters
			if(isEntity && mv != null && name.startsWith("get")) {
				
			}
		}
		return mv;
	}
	
	@Override
	public void visitEnd() {
		if (isEntity) {
			FieldVisitor fv;
			fv = cv.visitField(Opcodes.ACC_PUBLIC, 
					"fields", 
					"Ljava/util/HashSet;", 
					"Ljava/util/HashSet<Ljava/lang/String;>;", 
					null);
			
			if (fv != null) {
				fv.visitEnd();
			}
		}
		cv.visitEnd();
	}
	


	class LazyInitMethodAdapter extends MethodAdapter {
		String owner;
		/**
		 * 
		 * @param mv
		 * @param name - the fully qualified name of the class being transformed
		 */
		public LazyInitMethodAdapter(MethodVisitor mv, String name) {
			super(mv);
			System.out.println("LazyInitMethodAdapter");
			owner = name;
		}
		
		
		@Override
		public void visitCode() {
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitTypeInsn(Opcodes.NEW, "java/util/HashSet");
			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
			mv.visitFieldInsn(Opcodes.PUTFIELD, owner, "fields", "Ljava/util/HashSet;");
		}

		@Override
		public void visitInsn(int opcode) {
			if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
				
			}
			mv.visitInsn(opcode);
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			mv.visitMaxs(maxStack + 4, maxLocals);
		}
	}
}
