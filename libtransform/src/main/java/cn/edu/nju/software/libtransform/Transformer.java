/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libtransform;

import cn.edu.nju.software.libtransform.tla.TLAForInstrumentation;
import java.util.ArrayList;
import java.util.List;
import javato.instrumentor.RecursiveVisitor;
import javato.instrumentor.TransformClass;
import javato.instrumentor.TransformerForInstrumentation;
import javato.instrumentor.Visitor;

/**
 *
 * @author ise
 */
public class Transformer {
    
    // start from here! args[0] = main class; args[1] = soot class path
    public static void startTransform(String[] args) {
        List<TransformTask> trans= new ArrayList<TransformTask>();
        
        // wjtp
        RecursiveVisitor wv = new RecursiveVisitor(null);
        VisitorForTLA v = new VisitorForTLA(wv);
        wv.setNextVisitor(v);
        TLAForInstrumentation.v().setVisitor(v);
        
        // jtp
        RecursiveVisitor vv = new RecursiveVisitor(null);
        VisitorForInstrumentation pv = new VisitorForInstrumentation(vv);
        pv.setObserverClass("cn.edu.nju.software.libmonitor.Monitor");
        TransformerForInstrumentation.v().setVisitor(pv);
        vv.setNextVisitor(pv);
        
        trans.add(TLAForInstrumentation.v());
        trans.add(TransformerForInstrumentation.v());
        
        TransformClass processor = new TransformClass();
        processor.processAllAtOnce(args, trans);
        
        // test
        Visitor.st.test();
    }
}
