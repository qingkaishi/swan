package cn.edu.nju.swan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import replaydriver.ReplayDriver;
import cn.edu.nju.swan.model.SwanEvent;
import cn.edu.nju.swan.monitor.SwanControl;
import edu.hkust.leap.tracer.TraceReader;
import edu.hkust.leap.tracer.TraceWriter;

public class SwanReplayerMain {

	public static List<Integer> lines = new ArrayList<Integer>();
	public static int phase = 3;

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Throwable {
		// args = new String[] { "-phase", "ct", "-sync", "33,36" };
//		 args = new String[] { "-phase", "ct", "-sync", "30,49,19,26" };
//		args = new String[] { "-phase", "rp", "-trace",
//				"swantrace22634220866447.gz" };

		List<String> argsList = Arrays.asList(args);
		if (!argsList.contains("-phase")) {
			System.err.println("please specify phases, ct or rp");
			System.exit(-1);
		}

		if (argsList.contains("ct")) {
			if (!argsList.contains("-sync")) {
				System.err
						.println("please specify sync. lines, e.g. -sync 1,2,3,4");
				System.exit(-1);
			}

			phase = 1;

			try {
				int index = argsList.indexOf("-sync");
				String line = argsList.get(index + 1);
				String[] alines = line.split(",");
				for (String l : alines) {
					lines.add(Integer.valueOf(l));
				}
			} catch (Exception e) {
				System.err.println("please specify phases, ct or rp");
				System.err
						.println("please specify sync. lines, e.g. -sync 1,2,3,4");
				System.exit(-1);
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						TraceWriter.writeTrace(TraceWriter.swanTrace,
								"../Swan-Adapter/swantrace.gz");
						System.err
								.println("Trace has been stored into ../Swan-Adapter/swantrace.gz");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
				}
			});

			if (!argsList.contains("-trace")) {
				System.err
						.println("please specify trace file, e.g. -trace **.gz");
				System.exit(-1);
			}

			phase = 3;

			int index = argsList.indexOf("-trace");
			SwanControl.trace = (Vector<SwanEvent>) TraceReader
					.readTrace(argsList.get(index + 1));
		}

		ReplayDriver.main(args);

	}

}
