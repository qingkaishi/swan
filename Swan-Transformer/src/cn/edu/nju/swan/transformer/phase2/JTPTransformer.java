package cn.edu.nju.swan.transformer.phase2;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Modifier;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;
import cn.edu.nju.swan.Parameters;
import cn.edu.nju.swan.Util;
import cn.edu.nju.swan.transformer.Visitor;

public class JTPTransformer extends BodyTransformer {
	private Visitor visitor;

	public JTPTransformer() {
		RecursiveVisitor2 vv = new RecursiveVisitor2(null);
		LEAPVisitor2 pv = new LEAPVisitor2(vv);
		vv.setNextVisitor(pv);
		visitor = pv;
	}

	protected void internalTransform(Body body, String pn, Map map) {

		Util.resetParameters();
		SootMethod thisMethod = body.getMethod();

		/*
		 * if the method is static and also has no para we need a way to insert
		 * tid_method
		 */
		// if(thisMethod.isStatic()&&thisMethod.getParameterCount()==0)
		// return;

		if (!Util.shouldInstruThisMethod(thisMethod.getName()))
			return;

		SootClass thisClass = thisMethod.getDeclaringClass();
		String scname = thisClass.getName();
		// System.out.println("scname: "+scname);
		if (!Util.shouldInstruThisClass(scname))
			return;

		if (thisMethod.toString().contains("void main(java.lang.String[])")) {
			Parameters.isMethodMain = true;
		} else if (thisMethod.toString().contains("void run()")
				&& Util.isRunnableSubType(thisClass)) {
			Parameters.isMethodRunnable = true;
		}
		if (thisMethod.isSynchronized()) {
			Parameters.isMethodSynchronized = true;
		}

		// /////////////////////////////////////////////////
		Chain units = body.getUnits();

		// NO IDEA WHY THIS
		// To enable insert tid
		if (thisMethod.isStatic() && thisMethod.getParameterCount() == 0) {
			Stmt nop = Jimple.v().newNopStmt();
			// insert the nop just before the return stmt
			units.insertBefore(nop, units.getFirst());
		}

		visitor.visitMethodBegin(thisMethod, units);
		Iterator stmtIt = units.snapshotIterator();
		while (stmtIt.hasNext()) {
			Stmt s = (Stmt) stmtIt.next();
			visitor.visitStmt(thisMethod, units, s);
		}
		visitor.visitMethodEnd(thisMethod, units);

		if (Parameters.isMethodMain || Parameters.isMethodRunnable) {
			if (Parameters.isRuntime) {
				// DO OR DO NOT CATCH EXCEPTION??
				// Visitor.addCallCatchException(body);
			}

			if (Parameters.isMethodMain)
				Visitor.addCallMainMethodEnterInsert(thisMethod, units);
			else {
				Visitor.addCallRunMethodEnterInsert(thisMethod, units);
			}
		}

		if (Parameters.isMethodSynchronized && Parameters.removeSync) {
			thisMethod.setModifiers(thisMethod.getModifiers()
					& ~Modifier.SYNCHRONIZED);
			Visitor.addCallMonitorEntryExit(body);
		}

		// body.validate();
	}
}
