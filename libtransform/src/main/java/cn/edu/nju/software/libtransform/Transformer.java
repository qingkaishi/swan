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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author ise
 */
public class Transformer {

    public static void startTransform(String[] args) throws ParseException {
        Options opt = new Options();
        opt.addOption("t", "transform", true, "transform the program to an instrumented version.");
        opt.addOption("S", "stride", false, "determine whether use stride transformation");
        opt.addOption("P", "class-path", true, "the class path of your SUT.");
        CommandLineParser parser = new PosixParser();
        CommandLine cl = parser.parse(opt, args);

        List<TransformTask> trans = new ArrayList<TransformTask>();

        // wjtp
        RecursiveVisitor wv = new RecursiveVisitor(null);
        VisitorForTLA v = new VisitorForTLA(wv);
        wv.setNextVisitor(v);
        TLAForInstrumentation.v().setVisitor(v);
        trans.add(TLAForInstrumentation.v());

        // jtp
        if (cl.hasOption("S")) {
        } else {
            RecursiveVisitor vv = new RecursiveVisitor(null);
            VisitorForInstrumentation pv = new VisitorForInstrumentation(vv);
            pv.setObserverClass("cn.edu.nju.software.libmonitor.Monitor");
            TransformerForInstrumentation.v().setVisitor(pv);
            vv.setNextVisitor(pv);
            trans.add(TransformerForInstrumentation.v());
        }

        TransformClass processor = new TransformClass();
        processor.processAllAtOnce(cl.getOptionValue("t"), cl.getOptionValue("P"), trans);

        // test
        Visitor.st.test();
    }
}
