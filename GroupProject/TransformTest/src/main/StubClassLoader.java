package main;

import org.objectweb.asm.ClassWriter;

class StubClassLoader extends java.lang.ClassLoader {
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
}
