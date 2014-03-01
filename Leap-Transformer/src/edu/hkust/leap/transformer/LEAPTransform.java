package edu.hkust.leap.transformer;

import edu.hkust.leap.transformer.phase1.LEAPVisitor1;
import edu.hkust.leap.transformer.phase1.RecursiveVisitor1;

public class LEAPTransform {
    public static void main(String[] args) {
        RecursiveVisitor1 vv = new RecursiveVisitor1(null);
        LEAPVisitor1 pv = new LEAPVisitor1(vv);
        vv.setNextVisitor(pv);
        Visitor.setObserverClass("edu.hkust.leap.monitor.Monitor");
        TransformClass processor = new TransformClass();
        processor.processAllAtOnce(args, pv);     
    }
}
