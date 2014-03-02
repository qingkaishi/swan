/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libtransform;

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

    // start from here!
    public static void startTransform(String[] args) {
        List<MyTransformerInterface> trans= new ArrayList<MyTransformerInterface>();
        
        RecursiveVisitor vv = new RecursiveVisitor(null);
        VisitorForInstrumentation pv = new VisitorForInstrumentation(vv);
        pv.setObserverClass("cn.edu.nju.software.libmonitor.Monitor");
        TransformerForInstrumentation.v().setVisitor(pv);
        vv.setNextVisitor(pv);
        
        
        trans.add(TransformerForInstrumentation.v());
        
        TransformClass processor = new TransformClass();
        processor.processAllAtOnce(args, trans);
        Visitor.dumpIidToLine("/tmp/.iidToLine.map");//(Parameters.iidToLineMapFile);
        pv.writeSymTblSize();
    }
}
