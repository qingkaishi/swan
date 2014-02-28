package cn.edu.nju.swan.transformer.phase1;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.util.Chain;
import java.util.Iterator;
import java.util.Map;

import cn.edu.nju.swan.transformer.Visitor;


public class TransformerForInstrumentation extends BodyTransformer {
    private static TransformerForInstrumentation instance = new TransformerForInstrumentation();
    private Visitor visitor;
    //private
     TransformerForInstrumentation() {
    }


    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public static TransformerForInstrumentation v() {
        return instance;
    }

    //protected
    protected void internalTransform(Body body, String pn, Map map) {
    	
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
    }
    public void transforming(Body body, String pn, Map map){
    	internalTransform(body,  pn,  map);
    }


}
