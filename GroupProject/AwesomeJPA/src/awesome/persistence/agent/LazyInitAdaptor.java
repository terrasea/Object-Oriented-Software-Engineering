package awesome.persistence.agent;

import java.util.HashMap;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
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
	public FieldVisitor visitField( int access, 
									String name, 
									String desc, 
									String signature, 
									Object value) {
		
		System.out.println("visitField: " + name);
		return cv.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
				exceptions);
		if (isEntity && mv != null && name.equals("<init>")) {
			mv = new LazyInitMethodAdapter(mv, owner, 0);
		} else {
			//transform getters
			if(isEntity && mv != null && name.startsWith("get") && 
					isEntityField(name)) {
				System.out.println("Is a getter: " + 
						name.substring(3));
				mv = new LazyInitGetterAdaptor(mv, owner, name, 
						getEntityType(name));
			}
		}
		return mv;
	}
	
	private String getEntityType(String name) {
		// TODO Auto-generated method stub
		return "I";
	}


	private boolean isEntityField(String name) {
		System.out.println("isEntityField: " + name);
		return name.equals("getField");
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
		private String owner;
		private int nextPosition;
		/**
		 * 
		 * @param mv
		 * @param name - the fully qualified name of the class being transformed
		 */
		public LazyInitMethodAdapter(MethodVisitor mv, String name, 
				int nextPosition) {
			super(mv);
			System.out.println("LazyInitMethodAdapter");
			owner = name;
			this.nextPosition = nextPosition;
		}
		
		
		@Override
		public void visitCode() {
			System.out.println("LazyInitMethodAdapter: visitCode");
			mv.visitCode();
			//load the this variable and put it on the local method  
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			//now put the field from the this pointer onto the operand stack
			mv.visitTypeInsn(Opcodes.NEW, "java/util/HashSet");
			
			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashSet", 
					"<init>", "()V");
			mv.visitFieldInsn(Opcodes.PUTFIELD, owner, "fields", 
					"Ljava/util/HashSet;");
		}

		@Override
		public void visitInsn(int opcode) {
			if ((opcode >= Opcodes.IRETURN && 
					opcode <= Opcodes.RETURN) || 
					opcode == Opcodes.ATHROW) {
				
			}
			mv.visitInsn(opcode);
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			mv.visitMaxs(maxStack + 4, maxLocals);
		}
	}
	
	
	class LazyInitGetterAdaptor extends MethodAdapter {

		private String className;
		private String fieldName;
		private String fieldType;
		public LazyInitGetterAdaptor(MethodVisitor arg0, String className, 
				String methodName, String fieldType) {
			super(arg0);
			this.className = className;
			this.fieldName = String.format("%s%s", 
					String.format("%c", methodName.charAt(3)).toLowerCase(),
					methodName.substring(4));
			this.fieldType = fieldType;
			System.out.println("LazyInitGetterAdaptor: " + this.fieldName);
		}
		
		@Override
		public void visitCode() {
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, className, "fields",
					"Ljava/util/HashSet;");
			mv.visitLdcInsn("field");
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashSet", 
					"contains", "(Ljava/lang/Object;)Z");
			Label l0 = new Label();
			mv.visitJumpInsn(Opcodes.IFNE, l0);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, className, "fields", 
					"Ljava/util/HashSet;");
			mv.visitLdcInsn(fieldName);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashSet", 
					"add", "(Ljava/lang/Object;)Z");
			mv.visitInsn(Opcodes.POP);
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitInsn(Opcodes.ICONST_1);
			mv.visitFieldInsn(Opcodes.PUTFIELD, 
					className, 
					fieldName, 
					fieldType);
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		}
		
		
		@Override
		public void visitInsn(int opcode) {
			if ((opcode >= Opcodes.IRETURN && 
					opcode <= Opcodes.RETURN) || 
					opcode == Opcodes.ATHROW) {
				
			}
			mv.visitInsn(opcode);
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			
			mv.visitMaxs(maxStack + 4, maxLocals);
		}
	}
}
