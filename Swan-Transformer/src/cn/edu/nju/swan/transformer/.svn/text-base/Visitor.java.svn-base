package cn.edu.nju.swan.transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import soot.Body;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ConcreteRef;
import soot.jimple.Constant;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.Expr;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.tagkit.LineNumberTag;
import soot.util.Chain;
import cn.edu.nju.swan.Parameters;
import cn.edu.nju.swan.tloax.XFieldThreadEscapeAnalysis;
import cn.edu.nju.swan.transformer.contexts.BinopExprContext;
import cn.edu.nju.swan.transformer.contexts.InvokeContext;
import cn.edu.nju.swan.transformer.contexts.LabelContext;
import cn.edu.nju.swan.transformer.contexts.LocalContext;
import cn.edu.nju.swan.transformer.contexts.LocalOrConstantContext;
import cn.edu.nju.swan.transformer.contexts.RefContext;
import cn.edu.nju.swan.transformer.contexts.TypeContext;

public class Visitor {

	public static Value mainArgs;
	public static boolean methodEntryPointFlag;
	public static ThreadLocalObjectsAnalysis tlo;
	public static XFieldThreadEscapeAnalysis ftea;

	public static PegCallGraph pecg;
	public static long totalaccessnum = 0;
	public static long sharedaccessnum = 0;
	public static long instrusharedaccessnum = 0;

	public static HashSet<String> sharedInstanceVariableSet = new HashSet<String>();
	public static HashSet<String> sharedStaticVariableSet = new HashSet<String>();
	public static HashSet<String> synchronizedMethodSet = new HashSet<String>();
	public static HashSet<SootMethod> synchronizedIgnoreMethodSet = new HashSet<SootMethod>();
	public static String sharedVariableSig = "";

	public static HashMap<SootMethod, Local> methodToThreadIdMap = new HashMap<SootMethod, Local>();
	public static HashSet<String> sharedVariableWriteAccessSet = new HashSet<String>();
	public static HashMap<Value, Integer> speIndexMap = new HashMap<Value, Integer>();
	public static HashMap<Value, Integer> syncObjIndexMap = new HashMap<Value, Integer>();

	protected Visitor nextVisitor;
	static private int counter = 0;
	static private int indexCounter = 0;
	static private int syncIndexCounter = 0;

	static public String observerClass;

	public static void resetParameter() {

		methodEntryPointFlag = false;

		totalaccessnum = 0;
		sharedaccessnum = 0;
		instrusharedaccessnum = 0;

		methodToThreadIdMap.clear();
		// sharedVariableWriteAccessSet.clear();

		// counter = 0;
		// indexCounter=0;
	}

	public int getCounter() {
		return counter;
	}

	public static void setObserverClass(String s) {
		observerClass = s;
	}

	public Visitor(Visitor nextVisitor) {
		this.nextVisitor = nextVisitor;
	}

	private void setMethodEntryPointFlag() {

		methodEntryPointFlag = true;
	}

	public boolean getMethodEntryPointFlag() {

		return methodEntryPointFlag;
	}

	public void clearMethodEntryPointFlag() {

		methodEntryPointFlag = false;
	}

	public void visitMethodBegin(SootMethod sm, Chain units) {
		setMethodEntryPointFlag();
	}

	public void visitMethodEnd(SootMethod sm, Chain units) {

	}

	public void visitStmt(SootMethod sm, Chain units, Stmt s) {
		nextVisitor.visitStmt(sm, units, s);
	}

	public void visitStmtNop(SootMethod sm, Chain units, NopStmt nopStmt) {
		nextVisitor.visitStmtNop(sm, units, nopStmt);
	}

	public void visitStmtBreakpoint(SootMethod sm, Chain units,
			BreakpointStmt breakpointStmt) {
		nextVisitor.visitStmtBreakpoint(sm, units, breakpointStmt);
	}/*
	 * ThrowStmt ::= 'throw' LocalOrConstant@ThrowContext
	 */

	public void visitStmtThrow(SootMethod sm, Chain units, ThrowStmt throwStmt) {
		nextVisitor.visitStmtThrow(sm, units, throwStmt);
	}

	public void visitStmtReturnVoid(SootMethod sm, Chain units,
			ReturnVoidStmt returnVoidStmt) {
		if (Parameters.isMethodRunnable) {
			Visitor.addCallRunMethodExitBefore(sm, units, returnVoidStmt);
		}
		if (Parameters.isMethodSynchronized && Parameters.removeSync) {
			String sig;
			Value memory;
			Value base;
			sig = sm.getDeclaringClass().getName() + ".OBJECT";// +"."+invokeExpr.getMethod().getName();

			if (sm.isStatic()) {
				memory = StringConstant.v(sig);
				Visitor.addCallAccessSyncObj(sm, units, returnVoidStmt,
						"exitMonitorBefore", memory, true);
			} else {
				memory = StringConstant.v(sig);
				Stmt firstStmt = (Stmt) units.getFirst();
				if (firstStmt instanceof IdentityStmt) {
					base = ((IdentityStmt) firstStmt).getLeftOp();
					Visitor.addCallAccessSyncObjInstance(sm, units,
							returnVoidStmt, "exitMonitorBefore", base, memory,
							true);
				}
			}

			Visitor.instrusharedaccessnum++;
			Visitor.totalaccessnum++;
		}
		nextVisitor.visitStmtReturnVoid(sm, units, returnVoidStmt);
	}/*
	 * ReturnStmt ::= 'return' LocalOrConstant@ReturnContext
	 */

	public void visitStmtReturn(SootMethod sm, Chain units,
			ReturnStmt returnStmt) {
		if (Parameters.isMethodSynchronized && Parameters.removeSync) {
			String sig;
			Value memory;
			Value base;
			sig = sm.getDeclaringClass().getName() + ".OBJECT";// +"."+invokeExpr.getMethod().getName();

			if (sm.isStatic()) {
				memory = StringConstant.v(sig);
				Visitor.addCallAccessSyncObj(sm, units, returnStmt,
						"exitMonitorBefore", memory, true);
			} else {
				memory = StringConstant.v(sig);
				Stmt firstStmt = (Stmt) units.getFirst();
				if (firstStmt instanceof IdentityStmt) {
					base = ((IdentityStmt) firstStmt).getLeftOp();
					Visitor.addCallAccessSyncObjInstance(sm, units, returnStmt,
							"exitMonitorBefore", base, memory, true);
				}
			}

			Visitor.instrusharedaccessnum++;
			Visitor.totalaccessnum++;
		}
		nextVisitor.visitStmtReturn(sm, units, returnStmt);
	}/*
	 * MonitorStmt ::= EnterMonitorStmt | ExitMonitorStmt
	 */

	public void visitStmtMonitor(SootMethod sm, Chain units,
			MonitorStmt monitorStmt) {
		nextVisitor.visitStmtMonitor(sm, units, monitorStmt);
	}/*
	 * ExitMonitorStmt ::= 'monitorexit' LocalOrConstant@ExitMonitorContext
	 */

	public void visitStmtExitMonitor(SootMethod sm, Chain units,
			ExitMonitorStmt exitMonitorStmt) {
		nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
	}/*
	 * EnterMonitorStmt ::= 'monitorenter' LocalOrConstant@EnterMonitorContext
	 */

	public void visitStmtEnterMonitor(SootMethod sm, Chain units,
			EnterMonitorStmt enterMonitorStmt) {
		nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
	}/*
	 * LookupSwitchStmt ::= LocalOrConstant@LookupSwitchContext
	 * (LookupValue@LookupSwitchContext Label@LookupSwitchContext)*
	 * Label@LookupSwitchDefaultContext
	 */

	public void visitStmtLookupSwitch(SootMethod sm, Chain units,
			LookupSwitchStmt lookupSwitchStmt) {
		nextVisitor.visitStmtLookupSwitch(sm, units, lookupSwitchStmt);
	}

	public void visitLookupValue(SootMethod sm, Chain units, Stmt stmt,
			int lookupValue) {
		nextVisitor.visitLookupValue(sm, units, stmt, lookupValue);
	}/*
	 * TableSwitchStmt ::= LocalOrConstant@TableSwitchContext
	 * (LookupValue@TableSwitchContext Label@TableSwitchContext)*
	 * Label@TableSwitchDefaultContext
	 */

	public void visitStmtTableSwitch(SootMethod sm, Chain units,
			TableSwitchStmt tableSwitchStmt) {
		nextVisitor.visitStmtTableSwitch(sm, units, tableSwitchStmt);
	}/*
	 * InvokeStmt ::= InvokeExpr@InvokeOnlyContext
	 */

	public void visitStmtInvoke(SootMethod sm, Chain units,
			InvokeStmt invokeStmt) {
		nextVisitor.visitStmtInvoke(sm, units, invokeStmt);
	}

	public void visitStmtIf(SootMethod sm, Chain units, IfStmt ifStmt) {
		nextVisitor.visitStmtIf(sm, units, ifStmt);
	}/*
	 * GotoStmt ::= Label@GotoContext
	 */

	public void visitStmtGoto(SootMethod sm, Chain units, GotoStmt gotoStmt) {
		nextVisitor.visitStmtGoto(sm, units, gotoStmt);
	}/*
	 * IdentityStmt ::= Local@IdentityContext ThisRef@IdentityContext |
	 * Local@IdentityContext ParameterRef@IdentityContext | Local@IdentityCntext
	 * CaughtExceptionRef@IdentityContext
	 */

	public void visitStmtIdentity(SootMethod sm, Chain units,
			IdentityStmt identityStmt) {
		nextVisitor.visitStmtIdentity(sm, units, identityStmt);
	}/*
	 * AssignStmt ::= ConcreteRef@LHSContext LocalOrConstant@RHSContext |
	 * Local@LHSContext RHS@LHSContext
	 */

	public void visitStmtAssign(SootMethod sm, Chain units,
			AssignStmt assignStmt) {
		nextVisitor.visitStmtAssign(sm, units, assignStmt);
	}/*
	 * RHS{LHSContext} ::= ConcreteRef@RHSContext | LocalOrConstant@RHSContext |
	 * Expr@RSHContext
	 */

	public void visitRHS(SootMethod sm, Chain units, Stmt s, Value right) {
		nextVisitor.visitRHS(sm, units, s, right);
	}/*
	 * Expr{RHSContext} ::= BinopExpr@RHSContext | CastExpr@RHSContext |
	 * InstanceOfExpr@RHSContext | InvokeExpr@RHSContext | NewExpr@RHSContext |
	 * NewArrayExpr@RHSContext | NewMultiArrayExpr@RHSContext |
	 * LengthExpr@RHSContext | NegExpr@RHSContext
	 */

	public void visitExpr(SootMethod sm, Chain units, Stmt s, Expr expr) {
		nextVisitor.visitExpr(sm, units, s, expr);
	}/*
	 * NegExpr{RHSContext} ::= LocalOrConstant@NegContext
	 */

	public void visitNegExpr(SootMethod sm, Chain units, Stmt s, NegExpr negExpr) {
		nextVisitor.visitNegExpr(sm, units, s, negExpr);
	}/*
	 * LengthExpr{RHSContext} ::= LocalOrConstant@LengthContext
	 */

	public void visitLengthExpr(SootMethod sm, Chain units, Stmt s,
			LengthExpr lengthExpr) {
		nextVisitor.visitLengthExpr(sm, units, s, lengthExpr);
	}/*
	 * NewMultiArrayExpr{RHSContext} ::= Type@NewMultiArrayContext
	 * (LocalOrConstant@NewMultiArrayContext)*
	 */

	public void visitNewMultiArrayExpr(SootMethod sm, Chain units, Stmt s,
			NewMultiArrayExpr newMultiArrayExpr) {
		nextVisitor.visitNewMultiArrayExpr(sm, units, s, newMultiArrayExpr);
	}/*
	 * NewArrayExpr{RHSContext} ::= Type@NewArrayContext
	 * (LocalOrConstant@NewArrayContext)*
	 */

	public void visitNewArrayExpr(SootMethod sm, Chain units, Stmt s,
			NewArrayExpr newArrayExpr) {
		nextVisitor.visitNewArrayExpr(sm, units, s, newArrayExpr);
	}/*
	 * NewExpr{RHSContext} ::= Type@NewArrayContext
	 */

	public void visitNewExpr(SootMethod sm, Chain units, Stmt s, NewExpr newExpr) {
		nextVisitor.visitNewExpr(sm, units, s, newExpr);
	}/*
	 * InvokeExpr{InvokeAndAssignContext,InvokeOnlyContext} ::=
	 * LocalOrConstant@InvokeAndAssignTargetContextImpl
	 * Signature@InvokeAndAssignContext
	 * (LocalOrConstant@InvokeAndAssignArgumentContext)* |
	 * LocalOrConstant@InvokeOnlyTargetContext Signature@InvokeOnlyContext
	 * (LocalOrConstant@InvokeOnlyArgumentContext)*
	 */

	public void visitInvokeExpr(SootMethod sm, Chain units, Stmt s,
			InvokeExpr invokeExpr, InvokeContext context) {
		nextVisitor.visitInvokeExpr(sm, units, s, invokeExpr, context);
	}/*
	 * InstanceOfExpr{RHSContext} ::= LocalOrConstant@InstanceOfContext
	 * Type@InstanceOfContext
	 */

	public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s,
			StaticInvokeExpr invokeExpr, InvokeContext context) {
		nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);
	}

	public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s,
			InstanceInvokeExpr invokeExpr, InvokeContext context) {
		nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);
	}

	public void visitInstanceOfExpr(SootMethod sm, Chain units, Stmt s,
			InstanceOfExpr instanceOfExpr) {
		nextVisitor.visitInstanceOfExpr(sm, units, s, instanceOfExpr);
	}/*
	 * CastExpr{RHSContext} ::= Type@CastContext LocalOrConstant@CastContext
	 */

	public void visitCastExpr(SootMethod sm, Chain units, Stmt s,
			CastExpr castExpr) {
		nextVisitor.visitCastExpr(sm, units, s, castExpr);
	}/*
	 * Type{CastContext,InstanceOfContext,NewArrayContext,NewExpr,
	 * NewMultiArrayContext}
	 */

	public void visitType(SootMethod sm, Chain units, Stmt s, Type castType,
			TypeContext context) {
		nextVisitor.visitType(sm, units, s, castType, context);
	}/*
	 * BinopExpr{RHSContext,IfContext} ::= LocalOrConstant@RHSFirstContext
	 * Binop@RHSContext LocalOrConstant@RHSSecondContext |
	 * LocalOrConstant@IfFirstContext Binop@IfContext
	 * LocalOrConstant@IfSecondContext
	 */

	public void visitBinopExpr(SootMethod sm, Chain units, Stmt s,
			BinopExpr expr, BinopExprContext context) {
		nextVisitor.visitBinopExpr(sm, units, s, expr, context);
	}/*
	 * ConcreteRef{RHSContext,LHSContext} ::= InstanceFieldRef{RHSContext} |
	 * ArrayRef{RHSContext} | StaticFieldRef{RHSContext} |
	 * InstanceFieldRef{LHSContext} | ArrayRef{LHSContext} |
	 * StaticFieldRef{LHSContext}
	 */

	public void visitConcreteRef(SootMethod sm, Chain units, Stmt s,
			ConcreteRef concreteRef, RefContext context) {
		nextVisitor.visitConcreteRef(sm, units, s, concreteRef, context);
	}/*
	 * LocalOrConstant{RHSFirstContext,RHSSecondContext,IfFirstContext,
	 * IfSecondContext,CastContext,InstanceOfContext,
	 * InvokeAndAssignTargetContextImpl
	 * ,InvokeAndAssignArgumentContext,InvokeOnlyTargetContext
	 * ,InvokeOnlyArgumentContext,
	 * LengthContext,NegContext,NewMultiArrayContext,NewArrayContext,
	 * RHSContext,
	 * EnterMonitorContext,ExitMonitorContext,LookupSwitchContext,TableSwitchContext
	 * , ReturnContext,ThrowContext} ::= Local | Constant
	 */

	public void visitLocalOrConstant(SootMethod sm, Chain units, Stmt s,
			Value right, LocalOrConstantContext context) {
		nextVisitor.visitLocalOrConstant(sm, units, s, right, context);
	}/*
	 * Constant{{RHSFirstContext,RHSSecondContext,IfFirstContext,IfSecondContext,
	 * CastContext,InstanceOfContext,
	 * InvokeAndAssignTargetContextImpl,InvokeAndAssignArgumentContext
	 * ,InvokeOnlyTargetContext,InvokeOnlyArgumentContext,
	 * LengthContext,NegContext,NewMultiArrayContext,NewArrayContext,
	 * RHSContext,
	 * EnterMonitorContext,ExitMonitorContext,LookupSwitchContext,TableSwitchContext
	 * , ReturnContext,ThrowContext}
	 */

	public void visitConstant(SootMethod sm, Chain units, Stmt s,
			Constant constant, LocalOrConstantContext context) {
		nextVisitor.visitConstant(sm, units, s, constant, context);
	}/*
	 * Local{RHSFirstContext,RHSSecondContext,IfFirstContext,IfSecondContext,
	 * CastContext,InstanceOfContext,
	 * InvokeAndAssignTargetContextImpl,InvokeAndAssignArgumentContext
	 * ,InvokeOnlyTargetContext,InvokeOnlyArgumentContext,
	 * LengthContext,NegContext,NewMultiArrayContext,NewArrayContext,
	 * RHSContext,
	 * EnterMonitorContext,ExitMonitorContext,LookupSwitchContext,TableSwitchContext
	 * , ReturnContext,ThrowContext,IdentityContext,LHSContext}
	 */

	public void visitLocal(SootMethod sm, Chain units, Stmt s, Local local,
			LocalContext context) {
		nextVisitor.visitLocal(sm, units, s, local, context);
	}/*
	 * StaticFieldRef{RHSContext,LHSContext}
	 */

	public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s,
			StaticFieldRef staticFieldRef, RefContext context) {
		nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
	}/*
	 * ArrayRef{RHSContext,LHSContext}
	 */

	public void visitArrayRef(SootMethod sm, Chain units, Stmt s,
			ArrayRef arrayRef, RefContext context) {
		nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
	}/*
	 * InstanceFieldRef{RHSContext,LHSContext}
	 */

	public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s,
			InstanceFieldRef instanceFieldRef, RefContext context) {
		nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef,
				context);
	}/*
	 * CaughtExceptionRef{IdentityContext}
	 */

	public void visitCaughtExceptionRef(SootMethod sm, Chain units,
			IdentityStmt s, CaughtExceptionRef caughtExceptionRef) {
		nextVisitor.visitCaughtExceptionRef(sm, units, s, caughtExceptionRef);
	}/*
	 * ParameterRef{IdentityContext}
	 */

	public void visitParameterRef(SootMethod sm, Chain units, IdentityStmt s,
			ParameterRef parameterRef) {
		nextVisitor.visitParameterRef(sm, units, s, parameterRef);
	}/*
	 * ThisRef{IdentityContext}
	 */

	public void visitThisRef(SootMethod sm, Chain units, IdentityStmt s,
			ThisRef thisRef) {
		nextVisitor.visitThisRef(sm, units, s, thisRef);
	}/*
	 * Binop{RHSContext,IfContext}
	 */

	public void visitBinop(SootMethod sm, Chain units, Stmt s, String op,
			BinopExprContext context) {
		nextVisitor.visitBinop(sm, units, s, op, context);
	}/*
	 * Signature{InvokeAndAssignContext,InvokeOnlyContext}
	 */

	public void visitSignature(SootMethod sm, Chain units, Stmt s,
			String signature, InvokeContext context) {
		nextVisitor.visitSignature(sm, units, s, signature, context);
	}/*
	 * Label{GotoContext,IfContext,LookupSwitchContext,LookupSwitchDefaultContext
	 * ,TableSwitchContext,TableSwitchDefaultContext}
	 */

	public void visitLabel(SootMethod sm, Chain units, Stmt gotoStmt,
			Unit target, LabelContext context) {
		nextVisitor.visitLabel(sm, units, gotoStmt, target, context);
	}

	protected static boolean isThreadSubType(SootClass c) {
		if (c.getName().equals("java.lang.Thread"))
			return true;
		if (!c.hasSuperclass()) {
			return false;
		}
		return isThreadSubType(c.getSuperclass());
	}

	protected static boolean isRunnableSubType(SootClass c) {
		if (c.implementsInterface("java.lang.Runnable"))
			return true;
		if (c.hasSuperclass())
			return isRunnableSubType(c.getSuperclass());
		return false;
	}

	protected boolean isSubClass(SootClass c, String typeName) {
		if (c.getName().equals(typeName))
			return true;
		if (c.implementsInterface(typeName))
			return true;
		if (!c.hasSuperclass()) {
			return false;
		}
		return isSubClass(c.getSuperclass(), typeName);
	}

	public void addCallReCrashWith(SootMethod sm, Chain units,
			AssignStmt assignStmt) {
		Stmt s1 = (Stmt) units.getSuccOf(assignStmt);
		Value e = s1.getInvokeExpr().getArg(0);
		LinkedList args = new LinkedList();
		args.addLast(e);
		SootMethodRef mr = Scene.v().getMethod(Parameters.CATCH_EXCEPTION_SIG)
				.makeRef();
		units.insertAfter(
				Jimple.v().newInvokeStmt(
						Jimple.v().newStaticInvokeExpr(mr, args)), s1);
	}

	public void setStaticReceiver(SootMethod sm, Chain units) {

	}

	public static void addLocalThreadId(Body body) {

		Chain units = body.getUnits();

		Local tid = Jimple.v().newLocal("tid_" + body.getMethod().getName(),
				LongType.v());
		Local thread_ = Jimple.v().newLocal(
				"thread_" + body.getMethod().getName(),
				RefType.v("java.lang.Thread"));

		body.getLocals().add(tid);
		methodToThreadIdMap.put(body.getMethod(), tid);

		body.getLocals().add(thread_);

		String methodSig1 = "<" + "java.lang.Thread"
				+ ": java.lang.Thread currentThread()>";

		SootMethodRef mr1 = Scene.v().getMethod(methodSig1).makeRef();

		Value staticInvoke = Jimple.v().newStaticInvokeExpr(mr1);

		AssignStmt newAssignStmt1 = Jimple.v().newAssignStmt(thread_,
				staticInvoke);

		String methodSig2 = "<" + "java.lang.Thread" + ": long getId()>";

		SootMethodRef mr2 = Scene.v().getMethod(methodSig2).makeRef();

		Value virtualInvoke = Jimple.v().newVirtualInvokeExpr(thread_, mr2);

		AssignStmt newAssignStmt2 = Jimple.v()
				.newAssignStmt(tid, virtualInvoke);

		Stmt insertStmt = getLastIdentityStmt(units);
		if (insertStmt != null)
			units.insertAfter(newAssignStmt2, insertStmt);
		else
			units.insertBefore(newAssignStmt2, getFirstNonIdentityStmt(units));

		units.insertBefore(newAssignStmt1, newAssignStmt2);
	}

	public static void addCallMainMethodEnterInsert(SootMethod sm, Chain units) {

		LinkedList args = new LinkedList();
		args.addLast(getMethodThreadId(sm));
		args.addLast(StringConstant.v(sm.getDeclaringClass().getName() + "."
				+ sm.getName()));
		args.addLast(((IdentityStmt) units.getFirst()).getLeftOp());
		String methodSig = "<"
				+ observerClass
				+ ": void mainThreadStartRun(long,java.lang.String,java.lang.String[])>";

		SootMethodRef mr = Scene.v().getMethod(methodSig).makeRef();
		Value staticInvoke = Jimple.v().newStaticInvokeExpr(mr, args);
		units.insertAfter(Jimple.v().newInvokeStmt(staticInvoke),
				getMainThreadIdentityStmt(units));
	}

	public static void addCallRunMethodEnterInsert(SootMethod sm, Chain units) {

		LinkedList args = new LinkedList();
		args.addLast(getMethodThreadId(sm));
		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + "threadStartRun"
								+ "(long)>").makeRef();
		Value staticInvoke = Jimple.v().newStaticInvokeExpr(mr, args);
		units.insertAfter(Jimple.v().newInvokeStmt(staticInvoke),
				getRunThreadIdentityStmt(units));

	}

	private static Stmt getFirstNonIdentityStmt(Chain units) {
		Stmt s = (Stmt) units.getFirst();
		while (s instanceof IdentityStmt)
			s = (Stmt) units.getSuccOf(s);
		return s;

	}

	private static Stmt getLastIdentityStmt(Chain units) {
		Stmt s = getFirstNonIdentityStmt(units);
		return (Stmt) units.getPredOf(s);

	}

	private static Stmt getMainThreadIdentityStmt(Chain units) {
		Stmt s = (Stmt) units.getFirst();
		while (true) {
			if (s.toString().contains("tid_main"))
				break;
			s = (Stmt) units.getSuccOf(s);
		}
		return s;
	}

	private static Stmt getRunThreadIdentityStmt(Chain units) {
		Stmt s = (Stmt) units.getFirst();
		while (true) {
			if (s.toString().contains("tid_run"))
				break;
			s = (Stmt) units.getSuccOf(s);
		}
		return s;
	}

	public static Local getMethodThreadId(SootMethod sm) {
		if (Visitor.methodToThreadIdMap.get(sm) == null) {
			try {
				Body body = sm.retrieveActiveBody();
				Visitor.addLocalThreadId(body);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Visitor.methodToThreadIdMap.get(sm);
	}

	public static void addCallCatchException(Body body) {
		Chain units = body.getUnits();

		Local l_r0, l_r1;
		l_r0 = Jimple.v().newLocal("$exp_r0", RefType.v("java.lang.Throwable"));
		l_r1 = Jimple.v().newLocal("exp_r1", RefType.v("java.lang.Throwable"));
		body.getLocals().add(l_r0);
		body.getLocals().add(l_r1);

		Unit beginStmt = null;
		Unit returnStmt = null;
		Unit endStmt = null;
		Stmt s = null;
		Iterator stmtIt = units.snapshotIterator();

		boolean oops = true;
		while (stmtIt.hasNext()) {
			s = (Stmt) stmtIt.next();

			if (s instanceof IdentityStmt)
				continue;
			else {
				if (oops) {
					beginStmt = s;
					oops = false;
				}
				if (s instanceof ReturnVoidStmt)
					returnStmt = s;
			}
		}

		if (returnStmt == null)
			return;

		Stmt handlerStart = Jimple.v().newIdentityStmt(l_r0,
				Jimple.v().newCaughtExceptionRef());
		units.insertBefore(handlerStart, returnStmt);

		units.insertBefore(Jimple.v().newAssignStmt(l_r1, l_r0), returnStmt);
		LinkedList args = new LinkedList();
		args.addLast(l_r1);
		SootMethodRef mr = Scene.v().getMethod(Parameters.CATCH_EXCEPTION_SIG)
				.makeRef();
		units.insertBefore(
				Jimple.v().newInvokeStmt(
						Jimple.v().newStaticInvokeExpr(mr, args)), returnStmt);

		endStmt = Jimple.v().newGotoStmt(returnStmt);
		units.insertBefore(endStmt, handlerStart);
		SootClass exceptionClass = Scene.v()
				.getSootClass("java.lang.Exception");
		body.getTraps().add(
				Jimple.v().newTrap(exceptionClass, beginStmt, endStmt,
						handlerStart));

		body.validate();
	}

	public static void addCallMonitorEntryExit(Body body) {
		SootMethod appMethod = body.getMethod();
		SootClass appClass = appMethod.getDeclaringClass();
		Chain units = body.getUnits();
		String sig;
		Value memory;
		Value base;

		sig = appClass.getName() + ".OBJECT";// +"."+invokeExpr.getMethod().getName();
		memory = StringConstant.v(sig);

		if (Parameters.isRuntime)
			if (appMethod.isStatic()) {
				Visitor.addCallSpecialMonitorEntry(appMethod, units,
						"enterMonitorAfter", memory, false);
			} else {
				Stmt firstStmt = (Stmt) units.getFirst();
				if (firstStmt instanceof IdentityStmt) {
					base = ((IdentityStmt) firstStmt).getLeftOp();
					Visitor.addCallSpecialMonitorEntryInstance(appMethod,
							units, "enterMonitorAfter", base, memory, false);
				}
			}
		Visitor.instrusharedaccessnum++;
		Visitor.totalaccessnum++;
	}

	public static void addCallSpecialMonitorEntryInstance(SootMethod sm,
			Chain units, String methodName, Value base, Value v, boolean before) {

		// methodName = "accessSPE";
		LinkedList args = new LinkedList();
		args.addLast(base);
		args.addLast(IntConstant.v(getSyncObjectIndex(v)));
		args.addLast(getMethodThreadId(sm));

		Stmt s = getThreadIdentityStmt(units, sm.getName());

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(java.lang.Object,int,long)>").makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}
	}

	public static void addCallSpecialMonitorEntry(SootMethod sm, Chain units,
			String methodName, Value v, boolean before) {

		// methodName = "accessSPE";
		LinkedList args = new LinkedList();
		args.addLast(IntConstant.v(getSyncObjectIndex(v)));
		args.addLast(getMethodThreadId(sm));

		Stmt s = getThreadIdentityStmt(units, sm.getName());

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(int,long)>").makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}
	}

	private static int getSyncObjectIndex(Value v) {

		if (syncObjIndexMap.containsKey(v))
			return syncObjIndexMap.get(v);
		else {
			syncObjIndexMap.put(v, syncIndexCounter);
			return syncIndexCounter++;
		}

	}

	private static Stmt getThreadIdentityStmt(Chain units, String methodname) {
		Stmt s = (Stmt) units.getFirst();
		while (true) {
			if (s.toString().contains("tid_" + methodname))
				break;
			s = (Stmt) units.getSuccOf(s);
		}
		return s;
	}

	public static void addCallAccessSPE(SootMethod sm, Chain units, Stmt s,
			String methodName, Value v, boolean before) {

		// methodName = "accessSPE";

		LinkedList args = new LinkedList();
		args.addLast(IntConstant.v(getSPEIndex(v)));
		args.addLast(getMethodThreadId(sm));

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(int,long)>").makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}
	}

	public static void addCallAccessSPEInstance(SootMethod sm, Chain units,
			Stmt s, String methodName, Value o, Value spe, boolean before) {

		int lineNumber = ((LineNumberTag) s.getTag("LineNumberTag"))
				.getLineNumber();
		
		LinkedList args = new LinkedList();
		args.addLast(o);
		args.addLast(IntConstant.v(getSPEIndex(spe)));
		args.addLast(getMethodThreadId(sm));
		args.addLast(StringConstant.v(sm.getSignature() + ";" + s));
		args.addLast(IntConstant.v(lineNumber));

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<"
								+ observerClass
								+ ": void "
								+ methodName
								+ "(java.lang.Object,int,long,java.lang.String,int)>")
				.makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}
	}

	public static void addCallAccessSPEStatic(SootMethod sm, Chain units,
			Stmt s, String methodName, Value v, boolean before) {

		int lineNumber = ((LineNumberTag) s.getTag("LineNumberTag"))
				.getLineNumber();
		// methodName = "accessSPE";

		LinkedList args = new LinkedList();
		args.addLast(IntConstant.v(getSPEIndex(v)));
		args.addLast(getMethodThreadId(sm));
		args.addLast(StringConstant.v(sm.getSignature() + ";" + s));
		args.addLast(IntConstant.v(lineNumber));

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(int,long,java.lang.String,int)>").makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}

		// Handle special case:
		/*
		 * Stmt lastIdendityStmt = getLastIdentityStmt(units); Stmt fakeTid =
		 * (Stmt)units.getPredOf(s); Stmt fakeThread =
		 * (Stmt)units.getPredOf(fakeTid); Stmt fakeNull =
		 * (Stmt)units.getPredOf(fakeThread);
		 * 
		 * if(lastIdendityStmt == null && fakeNull == null ) { if (before) {
		 * units
		 * .insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr
		 * (mr, args)), fakeTid); } else {
		 * units.insertAfter(Jimple.v().newInvokeStmt
		 * (Jimple.v().newStaticInvokeExpr(mr, args)), s); } } else { if
		 * (before) { units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().
		 * newStaticInvokeExpr(mr, args)), s); } else {
		 * units.insertAfter(Jimple.
		 * v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mr, args)), s); } }
		 */
	}

	private static int getSPEIndex(Value v) {

		if (speIndexMap.containsKey(v))
			return speIndexMap.get(v);
		else {
			speIndexMap.put(v, indexCounter);
			return indexCounter++;
		}
	}

	public static void addCallRunMethodExitBefore(SootMethod sm, Chain units,
			Stmt returnVoidStmt) {

		LinkedList args = new LinkedList();
		args.addLast(getMethodThreadId(sm));
		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + "threadExitRun"
								+ "(long)>").makeRef();
		Value staticInvoke = Jimple.v().newStaticInvokeExpr(mr, args);
		units.insertBefore(Jimple.v().newInvokeStmt(staticInvoke),
				returnVoidStmt);
		// Iterator stmtIt = units.snapshotIterator();
		// while (stmtIt.hasNext())
		// {
		// Stmt s = (Stmt) stmtIt.next();
		// if(s instanceof ReturnVoidStmt)
		// units.insertBefore(Jimple.v().newInvokeStmt(staticInvoke), s);
		// }
	}

	public static void addCallAccessSyncObjInstance(SootMethod sm, Chain units,
			Stmt s, String methodName, Value base, Value v, boolean before) {

		// methodName = "accessSPE";

		LinkedList args = new LinkedList();
		args.addLast(base);
		args.addLast(IntConstant.v(getSPEIndex(v)));
		args.addLast(getMethodThreadId(sm));

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(java.lang.Object,int,long)>").makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}
	}

	public static void addCallAccessSyncObj(SootMethod sm, Chain units, Stmt s,
			String methodName, Value v, boolean before) {
		int lineNumber = ((LineNumberTag) s.getTag("LineNumberTag"))
				.getLineNumber();
		Value lock = null;
		String stmtSig = null;
		if (s instanceof MonitorStmt) {
			lock = ((MonitorStmt) s).getOp();
			stmtSig = sm.getSignature() + ";" + s.toString();
		}
		// methodName = "accessSPE";

		LinkedList args = new LinkedList();
		args.addLast(IntConstant.v(-1));
		args.addLast(getMethodThreadId(sm));
		args.addLast(IntConstant.v(lineNumber));
		args.addLast(lock);
		args.addLast(StringConstant.v(stmtSig));

		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<"
								+ observerClass
								+ ": void "
								+ methodName
								+ "(int,long,int,java.lang.Object,java.lang.String)>")
				.makeRef();
		if (before) {
			units.insertBefore(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		} else {
			units.insertAfter(
					Jimple.v().newInvokeStmt(
							Jimple.v().newStaticInvokeExpr(mr, args)), s);
		}
	}

	public static void addCallstartRunThreadBefore(SootMethod sm, Chain units,
			Stmt s, String methodName, Value v) {
		LinkedList args = new LinkedList();
		args.addLast(v);
		args.addLast(getMethodThreadId(sm));
		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(java.lang.Thread,long)>").makeRef();
		units.insertBefore(
				Jimple.v().newInvokeStmt(
						Jimple.v().newStaticInvokeExpr(mr, args)), s);
	}

	public static void addCallJoinRunThreadAfter(SootMethod sm, Chain units,
			Stmt s, String methodName, Value v) {
		LinkedList args = new LinkedList();
		args.addLast(v);
		args.addLast(getMethodThreadId(sm));
		SootMethodRef mr = Scene
				.v()
				.getMethod(
						"<" + observerClass + ": void " + methodName
								+ "(java.lang.Thread,long)>").makeRef();
		units.insertAfter(
				Jimple.v().newInvokeStmt(
						Jimple.v().newStaticInvokeExpr(mr, args)), s);
	}
}
