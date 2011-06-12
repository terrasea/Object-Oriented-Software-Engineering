package awesome.persistence.agent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sun.management.VMManagement;
import awesome.persistence.entity.Instance;
import awesome.persistence.manager.EntityException;
import awesome.persistence.manager.Manager;
import awesome.persistence.manager.NotAEntity;
import awesome.persistence.manager.PropertiesException;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * 
 * Transformer class
 *
 */
public abstract class Transformer {
	private static List<ClassFileTransformer> transformers = null;
	private static Instrumentation instrumentation = null;
	private static boolean agentRunning = false;

	public static void premain(String agentArgs, Instrumentation inst) {
		if (!agentRunning) {
			agentRunning = true;
			System.out.println("Starting agent");
			for (ClassFileTransformer trans : transformers) {
				inst.addTransformer(trans);
			}
		}
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		if (!agentRunning) {
			instrumentation = inst;
			premain(agentArgs, inst);
		}
	}

	public static void redefineClasses(ClassDefinition... definitions)
			throws Exception {
		if (instrumentation == null) {
			throw new RuntimeException(
					"Agent has not been started. Do not have handle to instrumentation");
		}

		instrumentation.redefineClasses(definitions);
	}

	public static void addTransformer(ClassFileTransformer clt) {
		if (transformers == null) {
			transformers = new ArrayList<ClassFileTransformer>();
		}
		if(instrumentation != null) {
			instrumentation.addTransformer(clt);
		}
		transformers.add(clt);
	}

	public static void removeTransformer(ClassFileTransformer clt) {
		if (transformers != null) {
			while (transformers.remove(clt))
				;
		}
		if(instrumentation != null) {
			while(instrumentation.removeTransformer(clt))
				;
		}
	}

	public static String getCurrentPID() {
		RuntimeMXBean mxbean = (RuntimeMXBean) ManagementFactory
				.getRuntimeMXBean();
		Field jvmField = null;
		try {
			jvmField = mxbean.getClass().getDeclaredField("jvm");
			jvmField.setAccessible(true);

			VMManagement management = (VMManagement) jvmField.get(mxbean);
			//System.out.println("management VmId: " + management.getVmId());
			Method method = management.getClass().getDeclaredMethod(
					"getProcessId");
			method.setAccessible(true);
			Integer processId = (Integer) method.invoke(management);
			System.out.println("process id: " + processId);
			return processId.toString();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static final String CLASS_PATH = System.getProperty("java.class.path");

	private static String agentjar = "AwesomeJPA.jar";

	private static final String OS_NAME = System.getProperty("os.name");

	
	public static void setAgentJar(String jarname) {
		agentjar = jarname;
	}
	
	
	public static void startAgent() throws AgentException {
		if (!agentRunning) {
			// without a agent the tranformer won't start
			Transformer.addTransformer(new NullAgent());
			
			try {
				String processId = getCurrentPID();
				VirtualMachine vm = VirtualMachine.attach(processId);
				String splitter = OS_NAME.equalsIgnoreCase("Windows") ? ";" : ":";
				String agentPath = null;
				for (String entry : CLASS_PATH.split(splitter)) {
					if (entry.toLowerCase().endsWith(INSTR_JAR_NAME.toLowerCase())) {
						agentPath = entry;
						break;
					}
				}
				System.out.println("Agent Path: " + agentPath);
				if (agentPath != null) {
					vm.loadAgent(agentPath);
					
				}else {
					throw new AgentException(String.format("Agent jar file starting with %s not in Classpath", agentjar));
				}
					
				vm.detach();
			} catch (SecurityException e) {
				throw new AgentException(e.toString());
			} catch (IllegalArgumentException e) {
				throw new AgentException(e.toString());
			} catch (AttachNotSupportedException e) {
				throw new AgentException(e.toString());
			} catch (IOException e) {
				throw new AgentException(e.toString());
			} catch (AgentLoadException e) {
				throw new AgentException(e.toString());
			} catch (AgentInitializationException e) {
				throw new AgentException(e.toString());
			}
		}

	}

	public static boolean agentRunning() {
		return agentRunning;
	}
	
	
	public static void main(String[] argv) {
		try {
			Manager.setUpManager("lib/awesome.properties");
			System.out.println("Agent running: " + Transformer.agentRunning());
		} catch (PropertiesException e) {
			e.printStackTrace();
		}
		
		Instance inst = new Instance();
		
		inst.setAwesomeId(1);
		inst.setField(20);
		
		try {
			Manager.persist(inst, false);
		} catch (NotAEntity e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (EntityException e) {
			e.printStackTrace();
		}
		
	}
}
