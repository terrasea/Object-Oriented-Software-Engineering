package transform;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodHandle;
import org.objectweb.asm.MethodVisitor;

public class MyMethodVisitor implements MethodVisitor {

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitCode() {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack,
			Object[] stack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitIincInsn(int var, int increment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitInsn(int opcode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitInvokeDynamicInsn(String name, String desc,
			MethodHandle bsm, Object... bsmArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLabel(Label label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLdcInsn(Object cst) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLineNumber(int line, Label start) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature,
			Label start, Label end, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		// TODO Auto-generated method stub

	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter,
			String desc, boolean visible) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt,
			Label... labels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler,
			String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		// TODO Auto-generated method stub

	}

}
