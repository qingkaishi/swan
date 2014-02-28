package cn.edu.nju.swan.transformer;

import cn.edu.nju.swan.transformer.phase1.LEAPVisitor1;
import cn.edu.nju.swan.transformer.phase1.RecursiveVisitor1;

public class LEAPTransform {
    public static void main(String[] args) {
        RecursiveVisitor1 vv = new RecursiveVisitor1(null);
        LEAPVisitor1 pv = new LEAPVisitor1(vv);
        vv.setNextVisitor(pv);
        Visitor.setObserverClass("cn.edu.nju.swan.monitor.Monitor");
        TransformClass processor = new TransformClass();
        processor.processAllAtOnce(args, pv);     
    }
}
