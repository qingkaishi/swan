package cn.edu.nju.swan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import cn.edu.nju.swan.cfg.ControlFlowGraph;
import cn.edu.nju.swan.cfg.MD5;
import cn.edu.nju.swan.transformer.LEAPTransform;

public class SwanTransformerMain {

	public static List<ControlFlowGraph> cfgs = new ArrayList<ControlFlowGraph>();

	public static void main(String[] args) {
//		args = new String[] { "example.Example" };
		if (args.length == 0) {
			System.err.println("please specify the main class ... ");
		} else {
			LEAPTransform.main(args);
		}

		System.err.println();
		System.err.print("Saving CFGs...");
		for (ControlFlowGraph cfg : cfgs) {
			save(cfg, cfg.getClassSignature(), cfg.getMethodSignature());
		}
		System.err.println(" Done!");
	}

	private static void save(ControlFlowGraph mycfg, String clsName,
			String mthdName) {
		try {
			File dir = new File("../Swan-Replayer/cfgs/" + MD5.encode(clsName)
					+ "/");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			ObjectOutputStream swanRecordOutputStream = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(
							dir.getAbsolutePath() + "/" + MD5.encode(mthdName)
									+ ".gz")));
			storeObject(mycfg, swanRecordOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void storeObject(Object o, ObjectOutputStream out) {
		try {
			out.writeObject(o);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
