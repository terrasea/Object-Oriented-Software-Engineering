package awesome.persistence.agent;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;

public class LazyInitFieldAdaptor implements FieldVisitor {

	private FieldVisitor fv = null;
	
	public LazyInitFieldAdaptor(FieldVisitor fv) {
		this.fv = fv;
		System.out.println("LazyInitFieldAdaptor");
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return fv.visitAnnotation(desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		System.out.println("Field adaptor - Attribute: " + attr);
		fv.visitAttribute(attr);
	}

	@Override
	public void visitEnd() {
		fv.visitEnd();
	}

}
