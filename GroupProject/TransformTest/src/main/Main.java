package main;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import transform.MyClassAdaptor;

public class Main {

	public Main() {
		printHello();
	}
	
	private void printHello() {
		System.out.println("hello");
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("premain");
		inst.addTransformer(new ClassFileTransformer() {
			public byte[] transform(ClassLoader l, String name, Class<?> c,
					ProtectionDomain d, byte[] b)
					throws IllegalClassFormatException {
				ClassReader cr = new ClassReader(b);
				ClassWriter cw = new ClassWriter(cr, 0);
				ClassVisitor cv = new MyClassAdaptor(cw);
				cr.accept(cv, 0);
				return cw.toByteArray();
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.printHello();
	}

}
