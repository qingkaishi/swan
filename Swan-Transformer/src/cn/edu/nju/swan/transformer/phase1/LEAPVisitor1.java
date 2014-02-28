package cn.edu.nju.swan.transformer.phase1;

import cn.edu.nju.swan.transformer.Visitor;
import cn.edu.nju.swan.transformer.contexts.InvokeContext;
import cn.edu.nju.swan.transformer.contexts.RHSContextImpl;
import cn.edu.nju.swan.transformer.contexts.RefContext;
import soot.*;
import soot.jimple.*;
import soot.util.*;

public class LEAPVisitor1 extends Visitor {

	public LEAPVisitor1(Visitor visitor) {
		super(visitor);
	}

	public void visitStmtAssign(SootMethod sm, Chain units,
			AssignStmt assignStmt) {
		nextVisitor.visitStmtAssign(sm, units, assignStmt);
	}

	public void visitStmtEnterMonitor(SootMethod sm, Chain units,
			EnterMonitorStmt enterMonitorStmt) {

		nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
	}

	public void visitStmtExitMonitor(SootMethod sm, Chain units,
			ExitMonitorStmt exitMonitorStmt) {

		nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
	}

	/**
	 * Although synchronized instance method invocation and static method
	 * invocation target at different locks, we still use the same SPE for them
	 */
	public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s,
			InstanceInvokeExpr invokeExpr, InvokeContext context) {

		nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);

	}

	public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s,
			StaticInvokeExpr invokeExpr, InvokeContext context) {

		nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);
	}

	public void visitArrayRef(SootMethod sm, Chain units, Stmt s,
			ArrayRef arrayRef, RefContext context) {
		nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
	}

	public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s,
			InstanceFieldRef instanceFieldRef, RefContext context) {

		String sig = instanceFieldRef.getField().getDeclaringClass().getName()
				+ "." + instanceFieldRef.getField().getName() + ".INSTANCE";

		// write instance field & handle array ref
		if (context != RHSContextImpl.getInstance())// ||
													// instanceFieldRef.getField().getType()
													// instanceof ArrayType)
		{
			if (!Visitor.tlo.isObjectThreadLocal(instanceFieldRef, sm)
					|| sig.contains("TableDescriptor.referencedColumnMap")) {
				if (!sig.contains("SQLChar"))
					sharedVariableWriteAccessSet.add(sig);

			}
		}
		nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef,
				context);
	}

	public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s,
			StaticFieldRef staticFieldRef, RefContext context) {

		String sig = staticFieldRef.getField().getDeclaringClass().getName()
				+ "." + staticFieldRef.getField().getName() + ".STATIC";

		// write static field & handle array ref
		if (context != RHSContextImpl.getInstance())// ||
													// staticFieldRef.getField().getType()
													// instanceof ArrayType)
		{
			if (!Visitor.tlo.isObjectThreadLocal(staticFieldRef, sm)) {
				sharedVariableWriteAccessSet.add(sig);

			}
		}
		nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
	}

}
