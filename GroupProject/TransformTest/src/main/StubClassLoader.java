package main;

import java.io.IOException;
import java.net.URL;

import org.objectweb.asm.ClassWriter;

class StubClassLoader extends java.lang.ClassLoader {
	public StubClassLoader(ClassLoader parent) {
		super(parent);
		System.out.println("StubClassLoader");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class findClass(String name) throws ClassNotFoundException {
		if (name.endsWith("_Stub")) {
			ClassWriter cw = new ClassWriter(0);
			byte[] b = cw.toByteArray();
			
			return defineClass(name, b, 0, b.length);
		}
		
		System.out.println("ClassLoader");
		return super.findClass(name);
	}
	
//	@Override
//	public Class<?> loadClass(String name, boolean resolve)
//			throws ClassNotFoundException {
//		// if ("mp.MyProgramLauncher".equals(name)) {
//		return getClass(name);
//		// }
//		// return super.loadClass(name, resolve);
//	}
//	
//	private Class<?> getClass(String name) throws ClassNotFoundException {
//		String file = name.replace('.', '/') + ".class";
//		byte[] b = null;
//		try {
//			b = loadClassData(file);
//			Class<?> c = defineClass(name, b, 0, b.length);
//			resolveClass(c);
//			return c;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
