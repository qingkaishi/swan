/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libtransform;

import cn.edu.nju.software.libtransform.patch.Patch;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javato.instrumentor.Visitor;
import javato.instrumentor.contexts.InvokeContext;
import javato.instrumentor.contexts.RHSContextImpl;
import javato.instrumentor.contexts.RefContext;
import soot.Modifier;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Value;
import soot.jimple.*;
import soot.options.Options;
import soot.util.Chain;

/**
 * Copyright (c) 2007-2008 Pallavi Joshi	<pallavi@cs.berkeley.edu>
 * Koushik Sen <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * modified from VisitorForActiveTesting.java
 *
 * @author ise
 */
public class VisitorForInstrumentation extends Visitor {

    private static int debug_idx = 0;

    public VisitorForInstrumentation(Visitor visitor) {
        super(visitor);
    }

    @Override
    public void visitStmtEnterMonitor(SootMethod sm, Chain units, EnterMonitorStmt enterMonitorStmt) {
        int linenumber = Visitor.getLineNum(enterMonitorStmt);

        LinkedList args = new LinkedList(); // arg list
        args.addLast(enterMonitorStmt.getOp()); // sv obj.
        args.addLast(IntConstant.v(st.getSize())); // sv no.
        args.addLast(IntConstant.v(linenumber));
        args.addLast(IntConstant.v(debug_idx++)); // debug idx

        SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeLock(java.lang.Object,int,int,int)>").makeRef();
        SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterLock(java.lang.Object,int,int,int)>").makeRef();

        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), enterMonitorStmt);
        units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), enterMonitorStmt);

        if (Patch.v().contains(linenumber)) {
            System.out.println("[Swan] Detecting patched MonitorEnter INST at Line " + linenumber + ".");
            units.remove(enterMonitorStmt);
        }
    }

    @Override
    public void visitStmtExitMonitor(SootMethod sm, Chain units, ExitMonitorStmt exitMonitorStmt) {
        int linenumber = Visitor.getLineNum(exitMonitorStmt);

        LinkedList args = new LinkedList(); // arg list
        args.addLast(exitMonitorStmt.getOp()); // sv obj.
        args.addLast(IntConstant.v(st.getSize())); // sv no.
        args.addLast(IntConstant.v(linenumber));
        args.addLast(IntConstant.v(debug_idx++)); // debug idx

        SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeUnlock(java.lang.Object,int,int,int)>").makeRef();
        SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterUnlock(java.lang.Object,int,int,int)>").makeRef();

        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), exitMonitorStmt);
        units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), exitMonitorStmt);

        if (Patch.v().contains(linenumber)) {
            System.out.println("[Swan] Detecting patched MonitorExit INST at Line " + linenumber + ".");
            units.remove(exitMonitorStmt);
        }
    }

    @Override
    public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s, InstanceInvokeExpr invokeExpr, InvokeContext context) {
        Value base = invokeExpr.getBase();
        String sig = invokeExpr.getMethod().getSubSignature();
        if (sig.equals("void wait()") || sig.equals("void wait(long)") || sig.equals("void wait(long,int)")) {
            //addCallWithObject(units, s, "myWaitAfter", base, false);
            LinkedList args = new LinkedList(); // arg list

            args.addLast(base); // sv obj.
            args.addLast(IntConstant.v(st.getSize())); // sv no.
            args.addLast(IntConstant.v(Visitor.getLineNum(s))); //line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx

            SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeWait(java.lang.Object,int,int,int)>").makeRef();
            SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterWait(java.lang.Object,int,int,int)>").makeRef();

            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        } else if (sig.equals("void notify()")) {
            //addCallWithObject(units, s, "myNotifyBefore", base, true);
            LinkedList args = new LinkedList(); // arg list

            args.addLast(base); // sv obj.
            args.addLast(IntConstant.v(st.getSize())); // sv no.
            args.addLast(IntConstant.v(Visitor.getLineNum(s))); //line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx

            SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeNotify(java.lang.Object,int,int,int)>").makeRef();
            SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterNotify(java.lang.Object,int,int,int)>").makeRef();

            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        } else if (sig.equals("void notifyAll()")) {
            //addCallWithObject(units, s, "myNotifyAllBefore", base, true);
            LinkedList args = new LinkedList(); // arg list

            args.addLast(base); // sv obj.
            args.addLast(IntConstant.v(st.getSize())); // sv no.
            args.addLast(IntConstant.v(Visitor.getLineNum(s))); //line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx

            SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeNotifyAll(java.lang.Object,int,int,int)>").makeRef();
            SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterNotifyAll(java.lang.Object,int,int,int)>").makeRef();

            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        } else if (sig.equals("void start()") && isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) {
            //addCallWithObject(units, s, "myStartBefore", base, true);
            LinkedList args = new LinkedList(); // arg list

            args.addLast(base); // sv obj.
            args.addLast(IntConstant.v(st.getSize() + 1)); // sv no.
            args.addLast(IntConstant.v(Visitor.getLineNum(s))); //line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx

            SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeThreadStart(java.lang.Object,int,int,int)>").makeRef();
            SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterThreadStart(java.lang.Object,int,int,int)>").makeRef();

            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        } else if ((sig.equals("void join()")
                || sig.equals("void join(long)")
                || sig.equals("void join(long,int)")) && isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) {
            //addCallWithObject(units, s, "myJoinAfter", base, false);
            LinkedList args = new LinkedList(); // arg list

            args.addLast(base); // sv obj.
            args.addLast(IntConstant.v(st.getSize() + 1)); // sv no.
            args.addLast(IntConstant.v(Visitor.getLineNum(s))); //line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx

            SootMethodRef mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeThreadJoin(java.lang.Object,int,int,int)>").makeRef();
            SootMethodRef mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterThreadJoin(java.lang.Object,int,int,int)>").makeRef();

            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
            units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        } else {
            // mine
            boolean exclude = false;
            List<String> excludes = Options.v().exclude();
            for (String pkg : excludes) {
                if (sm.getDeclaringClass().getPackageName().startsWith(pkg)) {
                    exclude = true;
                    break;
                }
            }
            if (exclude) {
                // as an instruction. args, as well as the base
                // now it is empty, because we only record InstanceField and ... in the version
                // args must be locals
            }
        }
    }

    @Override
    public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s, StaticInvokeExpr invokeExpr, InvokeContext context) {
        /*String sig = invokeExpr.getMethod().getSubSignature();
         if (sig.equals("void exit(int)") && isSystemSubType(invokeExpr.getMethod().getDeclaringClass())) {
         LinkedList args = new LinkedList();
         SootMethodRef myExitMr = Scene.v().getMethod("<" + observerClass + ": void myExit()>").makeRef();

         units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(myExitMr, args)), s);
         return;
         }*/
        boolean exclude = false;
        List<String> excludes = Options.v().exclude();
        for (String pkg : excludes) {
            if (sm.getDeclaringClass().getPackageName().startsWith(pkg)) {
                exclude = true;
                break;
            }
        }
        if (exclude) {
            // as an instruction, args
            // now it is empty, because we only record InstanceField and ... in the version
            // args must be locals
        }
    }

    @Override
    public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s, InstanceFieldRef instanceFieldRef, RefContext context) {
        String sig = instanceFieldRef.getField().getDeclaringClass().getName()
                + "." + instanceFieldRef.getField().getName() + ".INSTANCE";
        int svno = st.get(sig);
        if (svno < 0) {
            nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
            return;
        }

        LinkedList args = new LinkedList(); // arg list
        args.addLast(StringConstant.v(sig)); // sv obj.
        args.addLast(IntConstant.v(svno)); // sv no.
        args.addLast(IntConstant.v(Visitor.getLineNum(s))); // line no.
        args.addLast(IntConstant.v(debug_idx++)); // debug idx

        SootMethodRef mrbefore, mrafter;
        if (context == RHSContextImpl.getInstance()) {
            //read
            mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeRead(java.lang.Object,int,int,int)>").makeRef();
            mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterRead(java.lang.Object,int,int,int)>").makeRef();
        } else {
            //write
            mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeWrite(java.lang.Object,int,int,int)>").makeRef();
            mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterWrite(java.lang.Object,int,int,int)>").makeRef();
        }

        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
        units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
    }

    @Override
    public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s, StaticFieldRef staticFieldRef, RefContext context) {
        String sig = staticFieldRef.getField().getDeclaringClass().getName() + "." + staticFieldRef.getField().getName() + ".STATIC";
        int svno = st.get(sig);
        if (svno < 0) {
            nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
            return;
        }

        LinkedList args = new LinkedList(); // arg list
        args.addLast(StringConstant.v(sig)); // sv obj.
        args.addLast(IntConstant.v(svno)); // sv no.
        args.addLast(IntConstant.v(Visitor.getLineNum(s))); // line no.
        args.addLast(IntConstant.v(debug_idx++)); // debug idx

        SootMethodRef mrbefore, mrafter;
        if (context == RHSContextImpl.getInstance()) {
            //read
            mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeRead(java.lang.Object,int,int,int)>").makeRef();
            mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterRead(java.lang.Object,int,int,int)>").makeRef();
        } else {
            //write
            mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeWrite(java.lang.Object,int,int,int)>").makeRef();
            mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterWrite(java.lang.Object,int,int,int)>").makeRef();
        }

        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), s);
        units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);

        nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
    }

    @Override
    public void visitCaughtExceptionRef(SootMethod sm, Chain units, IdentityStmt s, CaughtExceptionRef caughtExceptionRef) {
        LinkedList args = new LinkedList();
        SootMethodRef myCleanMr = Scene.v().getMethod("<" + observerClass + ": void myCleanup()>").makeRef();
        units.insertAfter(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(myCleanMr, args)), s);

        nextVisitor.visitCaughtExceptionRef(sm, units, s, caughtExceptionRef);
    }

    @Override
    public void visitMethodBegin(SootMethod sm, Chain units) {
        if (Scene.v().getMainClass().getMethod("void main(java.lang.String[])").equals(sm)) {
            LinkedList args = new LinkedList();
            args.addLast(IntConstant.v(st.getSize() + 2));
            SootMethodRef myInitMr = Scene.v().getMethod("<" + observerClass + ": void myInit(int)>").makeRef();
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(myInitMr, args)), getFirstNonIdentityStmt(sm, units));
        } else if (sm.isSynchronized()) {
            LinkedList args = new LinkedList(); // arg list
            args.addLast(IntConstant.v(st.getSize())); // sv no.
            args.addLast(IntConstant.v(Visitor.getLineNum(sm))); // line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx !!!

            SootMethodRef mrbefore = null;
            if (sm.isStatic()) {
                args.addFirst(StringConstant.v(sm.getDeclaringClass().getName())); // sv obj.
                mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeSynchronizedStaticInvoke(java.lang.Object,int,int,int)>").makeRef();
            } else {
                args.addFirst(getThisRefLocal(sm, units));
                mrbefore = Scene.v().getMethod("<" + observerClass + ": void myBeforeSynchronizedInsInvoke(java.lang.Object,int,int,int)>").makeRef();
            }
            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrbefore, args)), getFirstNonIdentityStmt(sm, units));
        }

        nextVisitor.visitMethodBegin(sm, units);
    }

    @Override
    public void visitMethodEnd(SootMethod sm, Chain units) {
        if (sm.isSynchronized()) {
            int linenumber = getLineNum(sm);

            LinkedList args = new LinkedList(); // arg list        
            args.addLast(IntConstant.v(st.getSize())); // sv no.
            args.addLast(IntConstant.v(linenumber)); // line no.
            args.addLast(IntConstant.v(debug_idx++)); // debug idx !!!
            SootMethodRef mrafter = null;
            if (sm.isStatic()) {
                args.addFirst(StringConstant.v(sm.getDeclaringClass().getName())); // sv obj.
                mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterSynchronizedStaticInvoke(java.lang.Object,int,int,int)>").makeRef();
            } else {
                args.addFirst(getThisRefLocal(sm, units)); // sv obj
                mrafter = Scene.v().getMethod("<" + observerClass + ": void myAfterSynchronizedInsInvoke(java.lang.Object,int,int,int)>").makeRef();
            }

            List<Stmt> stmts = getExits(sm, units);
            for (Stmt s : stmts) {
                units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(mrafter, args)), s);
            }

            if (Patch.v().contains(linenumber)) {
                System.out.println("[Swan] Detecting patched SYNC. METHOD at Line " + linenumber + ".");
                sm.setModifiers(sm.getModifiers() & ~Modifier.SYNCHRONIZED);
            }
        }

        nextVisitor.visitMethodEnd(sm, units);
    }

    private Stmt getFirstNonIdentityStmt(SootMethod sm, Chain units) {
        Stmt s = (Stmt) units.getFirst();
        while (s instanceof IdentityStmt) {
            s = (Stmt) units.getSuccOf(s);
        }
        return s;
    }

    private List<Stmt> getExits(SootMethod sm, Chain units) {
        List<Stmt> ret = new ArrayList<Stmt>();

        for (Stmt s = (Stmt) units.getFirst(); s != null; s = (Stmt) units.getSuccOf(s)) {
            if (s instanceof ReturnVoidStmt || s instanceof ReturnStmt) {
                ret.add(s);
            }
        }

        return ret;
    }

    private Value getThisRefLocal(SootMethod sm, Chain units) {
        if (!sm.isStatic()) {
            for (Stmt s = (Stmt) units.getFirst(); s != null; s = (Stmt) units.getSuccOf(s)) {
                if (s instanceof IdentityStmt) {
                    Value v = ((IdentityStmt) s).getRightOp();
                    if (v instanceof ThisRef) {
                        return ((IdentityStmt) s).getLeftOp();
                    }
                }
            }
        }

        throw new RuntimeException("Cannot find the local for ThisRef in " + sm.getName());
    }
}
