package cn.edu.nju.swan.transformer.phase2;

import java.util.LinkedList;

import soot.ArrayType;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.util.Chain;
import cn.edu.nju.swan.Parameters;
import cn.edu.nju.swan.transformer.Visitor;
import cn.edu.nju.swan.transformer.contexts.InvokeContext;
import cn.edu.nju.swan.transformer.contexts.RHSContextImpl;
import cn.edu.nju.swan.transformer.contexts.RefContext;

@SuppressWarnings("rawtypes")
public class LEAPVisitor2 extends Visitor {

	public LEAPVisitor2(Visitor visitor) {
		super(visitor);
	}

	public void visitStmtEnterMonitor(SootMethod sm, Chain units,
			EnterMonitorStmt enterMonitorStmt) {
		Visitor.sharedaccessnum++;
		Visitor.totalaccessnum++;
		Visitor.instrusharedaccessnum++;

		Value op = enterMonitorStmt.getOp();
		Type type = op.getType();
		String sig = type.toString() + ".OBJECT";
		Value memory = StringConstant.v(sig);

		if (Parameters.isRuntime)
			Visitor.addCallAccessSyncObj(sm, units, enterMonitorStmt,
					"enterMonitorAfter", memory, false);
		else if (Parameters.isReplay)
			Visitor.addCallAccessSyncObj(sm, units, enterMonitorStmt,
					"enterMonitorBefore", memory, true);

		if (Parameters.isReplay && Parameters.removeSync)
			units.remove(enterMonitorStmt);

		nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
	}

	public void visitStmtExitMonitor(SootMethod sm, Chain units,
			ExitMonitorStmt exitMonitorStmt) {
		Visitor.sharedaccessnum++;
		Visitor.totalaccessnum++;
		Visitor.instrusharedaccessnum++;

		Value op = exitMonitorStmt.getOp();
		Type type = op.getType();
		String sig = type.toString() + ".OBJECT";
		Value memory = StringConstant.v(sig);

		Visitor.addCallAccessSyncObj(sm, units, exitMonitorStmt,
				"exitMonitorBefore", memory, true);

		if (Parameters.isReplay && Parameters.removeSync)
			units.remove(exitMonitorStmt);

		nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
	}

	public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s,
			InstanceInvokeExpr invokeExpr, InvokeContext context) {
		String sigclass = invokeExpr.getMethod().getDeclaringClass().getName()
				+ ".OBJECT";// +"."+invokeExpr.getMethod().getName();
		Value memory = StringConstant.v(sigclass);

		Value base = invokeExpr.getBase();
		String sig = invokeExpr.getMethod().getSubSignature();
		if (sig.equals("void wait()") || sig.equals("void wait(long)")
				|| sig.equals("void wait(long,int)")) {

			Visitor.addCallAccessSyncObj(sm, units, s, "waitAfter", memory,
					false);
			Visitor.instrusharedaccessnum++;
			Visitor.sharedaccessnum++;
			Visitor.totalaccessnum++;

			if (Parameters.isReplay && Parameters.removeSync)
				units.remove(s);

		} else if (sig.equals("void notify()")) {
			Visitor.addCallAccessSyncObj(sm, units, s, "notifyBefore", memory,
					true);
			Visitor.instrusharedaccessnum++;
			Visitor.sharedaccessnum++;
			Visitor.totalaccessnum++;

			if (Parameters.isReplay && Parameters.removeSync)
				units.remove(s);
		} else if (sig.equals("void notifyAll()")) {
			Visitor.addCallAccessSyncObj(sm, units, s, "notifyAllBefore",
					memory, true);
			Visitor.instrusharedaccessnum++;
			Visitor.sharedaccessnum++;
			Visitor.totalaccessnum++;

			if (Parameters.isReplay && Parameters.removeSync)
				units.remove(s);
		} else if (sig.equals("void start()")
				&& isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) {
			Visitor.addCallstartRunThreadBefore(sm, units, s,
					"startRunThreadBefore", invokeExpr.getBase());
		} else if ((sig.equals("void join()") || sig.equals("void join(long)") || sig
				.equals("void join(long,int)"))
				&& isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) {
			Visitor.addCallJoinRunThreadAfter(sm, units, s,
					"joinRunThreadAfter", invokeExpr.getBase());
		} else if (invokeExpr.getMethod().isSynchronized()) {
			if (Parameters.isReplay) {
				Visitor.addCallAccessSyncObj(sm, units, s,
						"enterMonitorBefore", memory, true);
			}
		}

		nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);
	}

	public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s,
			InstanceFieldRef instanceFieldRef, RefContext context) {
		Visitor.totalaccessnum++;

		Value base = instanceFieldRef.getBase();

		String sig = instanceFieldRef.getField().getDeclaringClass().getName()
				+ "." + instanceFieldRef.getField().getName() + ".INSTANCE";
		Value memory = StringConstant.v(sig);

		if (!instanceFieldRef.getField().isFinal())
			if (Visitor.sharedVariableWriteAccessSet.contains(sig)) {
				String methodname = "readBeforeInstance";

				if (context != RHSContextImpl.getInstance()) {
					methodname = "writeBeforeInstance";
				} else if (instanceFieldRef.getField().getType() instanceof ArrayType) {

					Stmt nextStmt = (Stmt) units.getSuccOf(s);
					if (s instanceof AssignStmt
							&& nextStmt instanceof AssignStmt) {
						AssignStmt assgnStmt = (AssignStmt) s;
						AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
						if (assgnNextStmt.getLeftOp().toString()
								.contains(assgnStmt.getLeftOp().toString())) {
							methodname = "writeBeforeInstance";
						}
					}
				}

				Visitor.addCallAccessSPEInstance(sm, units, s, methodname,
						base, memory, true);
				Visitor.sharedaccessnum++;
				Visitor.instrusharedaccessnum++;
			}
		nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef,
				context);
	}

	public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s,
			StaticFieldRef staticFieldRef, RefContext context) {
		Visitor.totalaccessnum++;
		String sig = staticFieldRef.getField().getDeclaringClass().getName()
				+ "." + staticFieldRef.getField().getName() + ".STATIC";
		Value memory = StringConstant.v(sig);

		if (!staticFieldRef.getField().isFinal())
			if (Visitor.sharedVariableWriteAccessSet.contains(sig)) {
				String methodname = "readBeforeStatic";

				if (context != RHSContextImpl.getInstance()) {
					methodname = "writeBeforeStatic";
				} else if (staticFieldRef.getField().getType() instanceof ArrayType) {
					Stmt nextStmt = (Stmt) units.getSuccOf(s);
					if (s instanceof AssignStmt
							&& nextStmt instanceof AssignStmt) {
						AssignStmt assgnStmt = (AssignStmt) s;
						AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
						if (assgnNextStmt.getLeftOp().toString()
								.contains(assgnStmt.getLeftOp().toString())) {
							methodname = "writeBeforeStatic";
						}
					}
				}

				Visitor.addCallAccessSPEStatic(sm, units, s, methodname,
						memory, true);
				Visitor.sharedaccessnum++;
				Visitor.instrusharedaccessnum++;
			}
		nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
	}

	/**
	 * Method guards
	 */
	@Override
	public void visitStmtReturn(SootMethod sm, Chain units,
			ReturnStmt returnStmt) {
		this.addMethodMonitor(sm, units, returnStmt, "endMethodBeforeReturn");
	}

	@Override
	public void visitStmtReturnVoid(SootMethod sm, Chain units,
			ReturnVoidStmt returnStmt) {
		this.addMethodMonitor(sm, units, returnStmt, "endMethodBeforeReturn");
	}

	@Override
	public void visitStmtThrow(SootMethod sm, Chain units, ThrowStmt throwStmt) {
		this.addMethodMonitor(sm, units, throwStmt, "endMethodBeforeThrow");
	}

	@Override
	public void visitStmtIdentity(SootMethod sm, Chain units,
			IdentityStmt identityStmt) {
		Stmt next = (Stmt) units.getSuccOf(identityStmt);
		if (!(next instanceof IdentityStmt)) {
			this.addMethodMonitor(sm, units, next, "startMethod");
		}
	}

	private Stmt addMethodMonitor(SootMethod thisMethod, Chain units,
			Stmt currentStmt, String methodName) {
		// method signature
		LinkedList args = new LinkedList();
		args.addLast(StringConstant.v(thisMethod.getDeclaringClass().getName()));
		args.addLast(StringConstant.v(thisMethod.getSignature()));

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + Visitor.observerClass + ": void " + methodName
								+ "(java.lang.String,java.lang.String)>")
				.makeRef();
		Stmt invokeStmt = Jimple.v().newInvokeStmt(
				Jimple.v().newStaticInvokeExpr(mr, args));
		units.insertBefore(invokeStmt, currentStmt);

		return invokeStmt;
	}
}
