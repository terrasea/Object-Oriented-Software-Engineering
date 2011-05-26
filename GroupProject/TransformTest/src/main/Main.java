package main;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Main {
	private static final String CLASS_PATH = System
			.getProperty("java.class.path");

	private static final String INSTR_JAR_NAME = "TransformTest.jar";

	private static final String OS_NAME = System.getProperty("os.name");

	Tempt tempt = new Tempt();

	

	public Main() throws Exception{
		
		printHello("Hello");

		tempt.tempt();

		tempt.answer("Not on you life");
		printCL();
	}

	private void printHello(String msg) {
		System.out.println(msg);
	}

	public void printCL() {
		System.out.println("Main ClassLoader: " + Main.class.getClassLoader());
	}

	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			System.out.println(args[0]);
		}
		if (Class.class.getClassLoader() == null && args.length == 0) {
			System
					.setProperty("java.system.class.loader",
							"main.MyClassLoader");
			VirtualMachineDescriptor vmDescriptor = null;
			for(VirtualMachineDescriptor descr: VirtualMachine.list()) {
				System.out.println("descriptor: " + descr.displayName());
				if(descr.displayName().endsWith(INSTR_JAR_NAME) || descr.displayName().endsWith("Main")) {
					vmDescriptor = descr;
					break;
				}
			}
			VirtualMachine vm = VirtualMachine.attach(vmDescriptor);
			String splitter = OS_NAME.equalsIgnoreCase("Windows") ? ";" : ":";
			String agentPath = null;
			for (String entry : CLASS_PATH.split(splitter)) {
				System.out.println("Entry: " + entry);
				if (entry.endsWith(INSTR_JAR_NAME)) {
					agentPath = entry;
					break;
				}
			}
			if (agentPath != null) {
				System.out.println("agentPath: " + agentPath);
				vm.loadAgent(agentPath);
				
			}
				
			vm.detach();
			System.out.println(Main.class.getClassLoader());

			Main main = new Main();
			main.printHello("Goodbye");
		}
		
		
		// System.out.println(main.test);
	}
}
