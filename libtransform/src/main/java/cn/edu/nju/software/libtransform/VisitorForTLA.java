/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libtransform;

import javato.instrumentor.Visitor;
import javato.instrumentor.contexts.RHSContextImpl;
import javato.instrumentor.contexts.RefContext;
import soot.SootMethod;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.util.Chain;

/**
 *
 * @author qingkaishi
 */
public class VisitorForTLA extends Visitor {

    public VisitorForTLA(Visitor nextVisitor) {
        super(nextVisitor);
    }

    @Override
    public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s,
            InstanceFieldRef instanceFieldRef, RefContext context) {

        String sig = instanceFieldRef.getField().getDeclaringClass().getName()
                + "." + instanceFieldRef.getField().getName() + ".INSTANCE";

        // write instance field
        if (context != RHSContextImpl.getInstance()) {
            if (!this.tla.isObjectThreadLocal(instanceFieldRef, sm)
                    || sig.contains("TableDescriptor.referencedColumnMap")) {
                if (!sig.contains("SQLChar")) {
                    Visitor.st.add(sig);
                }
            }
        }
        nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
    }

    @Override
    public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s,
            StaticFieldRef staticFieldRef, RefContext context) {

        String sig = staticFieldRef.getField().getDeclaringClass().getName()
                + "." + staticFieldRef.getField().getName() + ".STATIC";

        // write static field
        if (context != RHSContextImpl.getInstance()) {
            if (!this.tla.isObjectThreadLocal(staticFieldRef, sm)) {
                Visitor.st.add(sig);
            }
        }
        nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
    }

}
