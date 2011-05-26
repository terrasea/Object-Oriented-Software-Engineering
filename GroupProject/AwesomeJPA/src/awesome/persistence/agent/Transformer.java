package awesome.persistence.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.util.List;



public class Transformer {
	private static List<ClassFileTransformer> transformers = null;
	private static Instrumentation instrumentation = null;

	public static void premain(String agentArgs, Instrumentation inst) {
		for (ClassFileTransformer trans : transformers) {
			inst.addTransformer(trans);
		}
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		instrumentation = inst;
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
