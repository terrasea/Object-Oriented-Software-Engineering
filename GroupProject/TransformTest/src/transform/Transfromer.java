package transform;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Transfromer implements ClassFileTransformer {

	private static Instrumentation instrumentation = null;
	
	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] b) throws IllegalClassFormatException {
		ClassReader cr = new ClassReader(b);
		ClassWriter cw = new ClassWriter(cr, 0);
		// ClassVisitor cc = new CheckClassAdapter(cw);
		// ClassVisitor tv =
		// new TraceClassVisitor(cc, new PrintWriter(System.out));

		AddTimerAdapter adapter = new AddTimerAdapter(cw);
		cr.accept(adapter, 0);

		System.out.println("transform");
		return cw.toByteArray();
	}
	
	
	
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("premain");
		inst.addTransformer(new Transfromer());
			
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		instrumentation = inst;
		System.out.println("agent running");
		premain(agentArgs, inst);
	}

	public static void redefineClasses(ClassDefinition... definitions)
			throws Exception {
		if (instrumentation == null) {
			throw new RuntimeException(
					"Agent has not been started. Do not have handle to instrumentation");
		}

		instrumentation.redefineClasses(definitions);
	}

}
