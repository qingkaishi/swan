package edu.hkust.leap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.hkust.leap.datastructure.MyAccessVector;
import edu.hkust.leap.monitor.Monitor;

public class LeapRecorderMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		args = new String[] { "1", "example.Example" };
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
			}
		});
		
		List<String> arg = new LinkedList(Arrays.asList(args));
		int len = arg.size();
		if (len == 0) {
			System.err
					.println("please specify SPE size, the main class, and parameters... ");
		} else {
			process(arg);
		}
	}

	private static void process(List<String> args) {
		int index = 0;
		Monitor.initialize(Integer.valueOf(args.get(0)));

		if (args.contains("-v")) {
			index = args.indexOf("-v");
			MyAccessVector.setCapa(Integer.valueOf(args.get(++index)));
		}

		run(args.subList(++index, args.size()));
	}

	private static void run(List<String> args) {
		try {
			MonitorThread monThread = new MonitorThread();
			Runtime.getRuntime().addShutdownHook(monThread);

			edu.hkust.leap.monitor.random.Monitor.initialize();

			String appname = args.get(0);
			Class<?> c = Class.forName(appname);
			Class[] argTypes = new Class[] { String[].class };
			Method main = c.getDeclaredMethod("main", argTypes);

			String[] mainArgs = {};

			if (args.size() > 1) {
				mainArgs = new String[args.size() - 1];
				for (int k = 0; k < args.size() - 1; k++)
					mainArgs[k] = args.get(k + 1);
			}
			main.invoke(null, (Object) mainArgs);
			// production code should handle these exceptions more gracefully
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
