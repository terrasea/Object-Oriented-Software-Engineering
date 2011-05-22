package main;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import transform.AddTimerAdapter;

public class Main {
	Tempt tempt = new Tempt();
	
	public Main() {
		printHello("Hello");
		
		tempt.tempt();
		
		tempt.answer("Not on you life");
	}
	
	private void printHello(String msg) {
		System.out.println(msg);
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("premain");
		inst.addTransformer(new ClassFileTransformer() {
			public byte[] transform(ClassLoader l, String name, Class<?> c,
					ProtectionDomain d, byte[] b)
					throws IllegalClassFormatException {
				ClassReader cr = new ClassReader(b);
				ClassWriter cw = new ClassWriter(cr, 0);
				//ClassVisitor cc = new CheckClassAdapter(cw);
				//ClassVisitor tv =
				//    new TraceClassVisitor(cc, new PrintWriter(System.out));
				
				AddTimerAdapter adapter = new AddTimerAdapter(cw);
				cr.accept(adapter, 0);
				
				
				return cw.toByteArray();
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.printHello("Goodbye");
		
		
		//System.out.println(main.test);
	}

}
