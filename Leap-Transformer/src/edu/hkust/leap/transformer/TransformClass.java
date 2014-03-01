package edu.hkust.leap.transformer;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.jimple.spark.SparkTransformer;
import soot.options.Options;
import edu.hkust.leap.Parameters;
import edu.hkust.leap.Util;
import edu.hkust.leap.transformer.phase1.TransformerForInstrumentation;
import edu.hkust.leap.transformer.phase1.WholeProgramTransformer;
import edu.hkust.leap.transformer.phase2.JTPTransformer;

public class TransformClass {
//	private FileWriter printer;

	public void print(String str) {
		System.err.println(str);
//		try {
//			printer.write(str);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	public void processAllAtOnce(String[] args, Visitor visitor) {

		TransformerForInstrumentation.v().setVisitor(visitor);
		// Parameters.isOutputJimple = true;
		String mainclass = args[0];
//		try {
//			printer = new FileWriter(System.getProperty("user.dir")
//					+ System.getProperty("file.separator") + "tmp"
//					+ System.getProperty("file.separator") + mainclass);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		transformRuntimeVersion(mainclass);
		transformReplayVersion(mainclass);

		print("*** *** *** *** *** *** *** *** *** *** \n");
		print("\n*** Total access number: " + Visitor.totalaccessnum);
		print("\n*** SPE access number: " + Visitor.sharedaccessnum);
		print("\n*** Instrumented SPE access number: "
				+ Visitor.instrusharedaccessnum);
		print("\n*** SPE size: " + Visitor.speIndexMap.size());

		Iterator speSetIt = Visitor.speIndexMap.keySet().iterator();
		print("\n ");
		print("\n*** *** *** *** *** *** *** *** *** *** ");
		print("\n*** SPE name: ");
		while (speSetIt.hasNext()) {
			print("\n*** " + speSetIt.next());
		}
//		try {
//			printer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void transformRuntimeVersion(String mainclass) {
		Parameters.isRuntime = true;
		Parameters.isReplay = false;
		setRecordOptions(mainclass);

		String path = Util.getTmpDirectory();// .replace("\\", "\\\\")
		String[] args1 = { "-cp", ".", "-pp", "-validate", mainclass, "-d",
				path, "-f", "jimple", "-x", "jrockit.", "-x", "edu.", "-x",
				"com.", "-x", "checkers.", "-x", "org.xmlpull.", "-x",
				"org.apache.xml.", "-x", "org.apache.xpath." };
		String[] args2 = { "-cp", ".", "-pp", mainclass, "-d", path, "-x",
				"org.apache.xalan.", "-x", "org.apache.xpath.", "-i",
				"org.apache.derby.", "-x", "java.", "-x", "javax.", "-x",
				"sun.", "-x", "com.", "-x", "jrockit.", "-x", "edu.", "-x",
				"checkers.", "-x", "org.xmlpull.", "-x", "org.apache.xml.",
				"-x", "org.apache.xpath." };

		if (Parameters.isOutputJimple) {
			soot.Main.main(args1);// "-f","jimple",c"-x","javato.","-x","edu."
									// \\sootOutput "-process-dir", processDir
		} else {
			soot.Main.main(args2);// "-f","jimple",c"-x","javato.","-x","edu."
									// \\sootOutput "-process-dir", processDir
		}

		soot.G.reset();
		System.err.println("***** Runtime version generated *****");
	}

	private void transformReplayVersion(String mainclass) {
		Parameters.isRuntime = false;
		Parameters.isReplay = true;
		setReplayOptions(mainclass);
		Visitor.resetParameter();

		String path = Util.getTmpDirectory();// .replace("\\", "\\\\")
		String[] args1 = { "-cp", ".", "-pp", "-validate", mainclass, "-d",
				path, "-f", "jimple", "-x", "jrockit.", "-x", "edu.", "-x",
				"com.", "-x", "checkers.", "-x", "org.xmlpull.", "-x",
				"org.apache.xml.", "-x", "org.apache.xpath." };
		String[] args2 = { "-cp", ".", "-pp", "-validate", mainclass, "-d",
				path, "-x", "org.apache.xalan.", "-x", "org.apache.xpath.",
				"-i", "org.apache.derby.", "-x", "java.", "-x", "javax.", "-x",
				"sun.", "-x", "com.", "-x", "jrockit.", "-x", "edu.", "-x",
				"checkers.", "-x", "org.xmlpull.", "-x", "org.apache.xml." };

		if (Parameters.isOutputJimple) {
			soot.Main.main(args1);// "-f","jimple",c"-x","javato.","-x","edu."
									// \\sootOutput "-process-dir", processDir
		} else {
			soot.Main.main(args2);// "-f","jimple",c"-x","javato.","-x","edu."
									// \\sootOutput "-process-dir", processDir
		}

		soot.G.reset();
		System.err.println("--- Replay version generated ---");
	}

	private void setRecordOptions(String mainclass) {
		PhaseOptions.v().setPhaseOption("jb", "enabled:true");
		Options.v().set_keep_line_number(true);
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().set_whole_program(true);
		Options.v().set_app(true);

		// Enable Spark
		HashMap<String, String> opt = new HashMap<String, String>();
		// opt.put("verbose","true");
		opt.put("propagator", "worklist");
		opt.put("simple-edges-bidirectional", "false");
		opt.put("on-fly-cg", "true");
		opt.put("set-impl", "double");
		opt.put("double-set-old", "hybrid");
		opt.put("double-set-new", "hybrid");
		opt.put("pre_jimplify", "true");
		SparkTransformer.v().transform("", opt);
		PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");

		Scene.v().setSootClassPath(
				System.getProperty("sun.boot.class.path") + File.pathSeparator
						+ System.getProperty("java.class.path"));

		PackManager
				.v()
				.getPack("wjtp")
				.add(new Transform("wjtp.transformer1",
						new WholeProgramTransformer()));
		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.transformer2", new JTPTransformer()));

		SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
		Scene.v().setMainClass(appclass);

		Scene.v().loadClassAndSupport(Visitor.observerClass);
	}

	private void setReplayOptions(String mainclass) {
		Options.v().set_keep_line_number(true);
		Options.v().set_app(true);

		Scene.v().setSootClassPath(
				System.getProperty("sun.boot.class.path") + File.pathSeparator
						+ System.getProperty("java.class.path"));

		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.transformer", new JTPTransformer()));

		SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
		Scene.v().setMainClass(appclass);

		Scene.v().loadClassAndSupport(Visitor.observerClass);
	}
}
