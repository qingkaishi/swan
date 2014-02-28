package cn.edu.nju.swan.transformer.phase1;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import cn.edu.nju.swan.Util;
import cn.edu.nju.swan.transformer.Visitor;

public class WholeProgramTransformer extends SceneTransformer {
	protected void internalTransform(String pn, Map map) {

		Visitor.tlo = new ThreadLocalObjectsAnalysis(
				new UnsynchronizedMhpAnalysis());
		// Visitor.ftea = new XFieldThreadEscapeAnalysis();
		// Visitor.pecg = new PegCallGraph(Scene.v().getCallGraph());

		Iterator<SootClass> classIt = Scene.v().getApplicationClasses()
				.iterator();
		while (classIt.hasNext()) {
			SootClass sc = classIt.next();
			String scname = sc.getName();
			// System.out.println("scname: "+scname);

			if (!Util.shouldInstruThisClass(scname))
				continue;

			Iterator<SootMethod> methodIt = sc.getMethods().iterator();

			while (methodIt.hasNext()) {
				SootMethod sm = methodIt.next();

				/**
				 * The following code is for optimization using synchronized
				 * ownership if(Parameters.isRuntime) { List list =
				 * Visitor.pecg.getPredsOf(sm); if (list.size()>0) { Iterator it
				 * = list.iterator(); boolean flag = false; while (it.hasNext())
				 * { SootMethod met = (SootMethod)it.next();
				 * if(!met.isSynchronized()) { flag = false; break; } else flag
				 * = true; } if(flag) {
				 * Visitor.synchronizedIgnoreMethodSet.add(sm);
				 * System.err.println(" *** synchronizedIgnoreMethod: "+sm);
				 * continue; } } } else {
				 * if(Visitor.synchronizedIgnoreMethodSet.contains(sm))
				 * continue; }
				 */
				String smname = sm.getName();
				if (!Util.shouldInstruThisMethod(smname))
					continue;

				if (sm.isAbstract() || sm.isNative())
					continue;

				try {
					Body body = sm.retrieveActiveBody();
					TransformerForInstrumentation.v().transforming(body, pn,
							map);
				} catch (Exception e) {
					continue;
				}
			}
		}
	}
}
