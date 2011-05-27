package awesome.persistence.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.tools.attach.VirtualMachine;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.management.VMManagement;

public abstract class Transformer {
	private static List<ClassFileTransformer> transformers = null;
	private static Instrumentation instrumentation = null;
	private static boolean agentRunning = false;

	public static void premain(String agentArgs, Instrumentation inst) {
		if (!agentRunning) {
			agentRunning = true;
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

	public void addTransformer(ClassFileTransformer clt) {
		if (transformers == null) {
			transformers = new ArrayList<ClassFileTransformer>();
		}

		transformers.add(clt);
	}

	public static void delTransformer(ClassFileTransformer clt) {
		if (transformers != null) {
			while (transformers.remove(clt))
				;
		}
	}

	public static void startAgent() {
		if (!agentRunning) {
			RuntimeMXBean mxbean = (RuntimeMXBean) ManagementFactory
					.getRuntimeMXBean();
			Field jvmField = null;
			try {
				jvmField = mxbean.getClass().getDeclaredField("jvm");
				jvmField.setAccessible(true);

				VMManagement management = (VMManagement) jvmField.get(mxbean);
				Method method = management.getClass().getDeclaredMethod(
						"getProcessId");
				method.setAccessible(true);
				Integer processId = (Integer) method.invoke(management);
				VirtualMachine vm = VirtualMachine.attach(processId.toString());

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		startAgent();
	}
}
