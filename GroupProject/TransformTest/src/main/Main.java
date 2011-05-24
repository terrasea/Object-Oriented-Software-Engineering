package main;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {
	Tempt tempt = new Tempt();

	public Main() {
		System.out.println(Main.class.getClassLoader());
		printHello("Hello");

		tempt.tempt();

		tempt.answer("Not on you life");
		printCL();
	}
<<<<<<< HEAD

=======
	
	
	
	
>>>>>>> 02f2cdd0639137a1e8ad6b1efcb44b1de6343c46
	private void printHello(String msg) {
		System.out.println(msg);
	}

	public void printCL() {
		System.out.println("Bar ClassLoader: " + Main.class.getClassLoader());
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("premain");
		inst.addTransformer(new ClassFileTransformer() {
			public byte[] transform(ClassLoader l, String name, Class<?> c,
					ProtectionDomain d, byte[] b)
					throws IllegalClassFormatException {
				ClassReader cr = new ClassReader(b);
				ClassWriter cw = new ClassWriter(cr, 0);
				// ClassVisitor cc = new CheckClassAdapter(cw);
				// ClassVisitor tv =
				// new TraceClassVisitor(cc, new PrintWriter(System.out));

				// AddTimerAdapter adapter = new AddTimerAdapter(cw);
				// cr.accept(adapter, 0);

				return cw.toByteArray();
			}
		});
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
<<<<<<< HEAD
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		if (args.length > 0) {
			System.out.println(args[0]);
		}
		if (Class.class.getClassLoader() == null && args.length == 0) {
			System
					.setProperty("java.system.class.loader",
							"main.MyClassLoader");
			try {
				MyClassLoader loader = (MyClassLoader) ClassLoader
						.getSystemClassLoader();
				loader.addFileToClasspath("TransformTest.jar");

				// MyClassLoader loader = new
				// MyClassLoader(Main.class.getClassLoader());
				// Class inst = loader.loadClass("main.Main");
				// Constructor con = inst.getConstructor();
				// con.newInstance();
				// Method printCL = inst.getMethod("printCL", null);
				// printCL.invoke(null, new Object[0]);
				// Class mainArgType[] = { (new String[0]).getClass() };
				//
				// Method mainm = inst.getMethod("main", mainArgType);
				// String[] args2 = {"One"};
				// Object argsArray[] = {args2};
				// mainm.invoke(null, argsArray);
				// } else {

				Main main = new Main();
				main.printHello("Goodbye");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		// System.out.println(main.test);
=======
	public static void main(String[] args) {
		StubClassLoader.getSystemClassLoader();
		Main main = new Main();
		main.printHello("Goodbye");
		
		
		//System.out.println(main.test);
>>>>>>> 02f2cdd0639137a1e8ad6b1efcb44b1de6343c46
	}

}
