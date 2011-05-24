package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader extends ClassLoader {

	public MyClassLoader(ClassLoader parent) {
		super(parent);
		System.out.println("MyClassLoader");
	}

	public MyClassLoader() {
		super(MyClassLoader.class.getClassLoader());
		System.out.println("MyClassLoader");
	}

	// synchronized public void addFileToClasspath(String jarName)
	// throws MalformedURLException, ClassNotFoundException {
	// File filePath = new File(jarName);
	// URI uriPath = filePath.toURI();
	// URL urlPath = uriPath.toURL();
	//
	// addURL(urlPath);
	// }

	@Override
	public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		if (!"main.MyClassLoader".equals(name) && name.startsWith("main")) {
			return getClass(name);

		}
		return super.loadClass(name, resolve);
	}

	private Class<?> getClass(String name) throws ClassNotFoundException {
		String file = name.replace('.', '/') + ".class";
		byte[] b = null;
		try {
			System.out.println("getClass " + name);
			b = loadClassData(file);

			Class<?> c = defineClass(name, b, 0, b.length);
			System.out.println(c.getCanonicalName());
			resolveClass(c);

			return c;
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
			return null;
		}
	}

	private byte[] loadClassData(String name) throws IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream(
				name);
		int size = stream.available();
		byte buff[] = new byte[size];
		DataInputStream in = new DataInputStream(stream);
		in.readFully(buff);
		in.close();
		System.out.println(buff.length);
		return buff;
	}

//	@Override
//	public String toString() {
//		return "MyClassLoader";
//	}
}
