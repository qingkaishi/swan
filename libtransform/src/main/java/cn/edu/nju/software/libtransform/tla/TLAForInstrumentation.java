/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libtransform.tla;

/**
 *
 * @author qingkaishi
 */
import cn.edu.nju.software.libtransform.TransformTask;
import java.util.Iterator;
import java.util.Map;
import javato.instrumentor.Visitor;

import soot.Body;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transformer;
import soot.jimple.Stmt;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import soot.util.Chain;

public class TLAForInstrumentation extends SceneTransformer implements TransformTask {

    private static final TLAForInstrumentation instance = new TLAForInstrumentation();
    private Visitor visitor;

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public String getPhase() {
        return "wjtp";
    }

    public String getPhaseName() {
        return this.getPhase() + "." + "tla";
    }

    public Transformer getSootTransformer() {
        return this;
    }
    
    public static TLAForInstrumentation v(){
        return instance;
    }

    protected void internalTransform(String pn, Map map) {
        visitor.tla = new ThreadLocalObjectsAnalysis(
                new UnsynchronizedMhpAnalysis());

        Iterator<SootClass> classIt = Scene.v().getApplicationClasses()
                .iterator();
        while (classIt.hasNext()) {
            SootClass sc = classIt.next();

            Iterator<SootMethod> methodIt = sc.getMethods().iterator();
            while (methodIt.hasNext()) {
                SootMethod sm = methodIt.next();
                if (sm.isAbstract() || sm.isNative()) {
                    continue;
                }

                try {
                    Body body = sm.retrieveActiveBody();
                    SootMethod thisMethod = body.getMethod();
                    Chain units = body.getUnits();

                    visitor.visitMethodBegin(thisMethod, units);
                    Iterator stmtIt = units.snapshotIterator();
                    while (stmtIt.hasNext()) {
                        Stmt s = (Stmt) stmtIt.next();
                        visitor.visitStmt(thisMethod, units, s);
                    }
                    visitor.visitMethodEnd(thisMethod, units);
                    body.validate();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
