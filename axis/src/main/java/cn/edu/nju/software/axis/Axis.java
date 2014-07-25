/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.axis;

import cn.edu.nju.software.libevent.SwanEvent;
import cn.edu.nju.software.petrinet.Node;
import cn.edu.nju.software.petrinet.Place;
import cn.edu.nju.software.petrinet.Transition;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLineNumberTag;
import soot.tagkit.SourceLnPosTag;
import soot.util.Chain;

/**
 * Axis: Automatically Fixing Atomicity Violations Through Solving Control
 * Constraints
 *
 * Liu, Peng and Zhang, Charles
 *
 * ICSE 2012
 *
 */
public class Axis {

    /**
     * usage:
     *
     * axis trace_file class:line,class:line,...;class:line,class:line...
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("[ERROR] Axis is not ready to release!!\n");
        System.exit(0);

        Options opt = new Options();
        opt.addOption("b", "bug", true, "the line numbers of the bug, e.g. class:line,class:line,...;class:line,class:line....");
        opt.addOption("T", "trace", true, "the input trace.");
        opt.addOption("c", "test-case", true, "your test cases, e.g. \"MainClass args\". Please use \"\" to make it as a whole.");
        opt.addOption("P", "class-path", true, "the class path of your SUT.");
        opt.addOption("h", "help", false, "print this information.");
        String formatstr = "java [java-options] -jar axis.jar [--help] --test-cases <args>] [--bug <bug>] [--class-path <args>]";
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cl = parser.parse(opt, args);
            if (cl.hasOption("b") && cl.hasOption("T") && cl.hasOption("c")) {
                String trace = cl.getOptionValue("T");
                String bug = cl.getOptionValue("b");
                String mainclass = cl.getOptionValue("c");
                String cp = null;
                if (cl.hasOption("P")) {
                    cp = cl.getOptionValue("P");
                }

                startAxis(trace, bug, mainclass, cp);
            } else {
                throw new ParseException(formatstr);
            }

            if (cl.hasOption("h")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(formatstr, "", opt, "");
                return;
            }
        } catch (Exception e) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(formatstr, "", opt, "");
            System.exit(1);
        }
    }

    private static void startAxis(String filename, String av, String mainClass, String cp) {
        List<String> cl1 = new ArrayList<String>();
        List<String> cl2 = new ArrayList<String>();

        List<Integer> ln1 = new ArrayList<Integer>();
        List<Integer> ln2 = new ArrayList<Integer>();

        try {
            String[] avp = av.split(";");
            String[] avp1 = avp[0].split(",");
            String[] avp2 = avp[1].split(",");

            for (String str : avp1) {
                String[] item = str.split(":");
                String clsname = item[0];
                int lineno = Integer.parseInt(item[1]);

                cl1.add(clsname);
                ln1.add(lineno);
            }

            for (String str : avp2) {
                String[] item = str.split(":");
                String clsname = item[0];
                int lineno = Integer.parseInt(item[1]);

                cl2.add(clsname);
                ln2.add(lineno);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File f = new File(filename);
        if (f.exists() && !f.isDirectory()) {
            // ...
            try {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Vector<SwanEvent> trace = (Vector<SwanEvent>) ois.readObject();
                ois.close();

                List<SwanEvent> forks = new ArrayList<SwanEvent>();
                Map<Integer, Vector<SwanEvent>> threads = new HashMap<Integer, Vector<SwanEvent>>();
                for (SwanEvent se : trace) {
                    if (!threads.containsKey(se.threadId)) {
                        threads.put(se.threadId, new Vector<SwanEvent>());
                    }

                    if (se.accessType == SwanEvent.AccessType.FORK) {
                        forks.add(se);
                    }

                    threads.get(se.threadId).add(se);
                }

                boolean find1 = false, find2 = false;
                Set<Integer> relatedThreads = new HashSet<Integer>();
                Iterator<Entry<Integer, Vector<SwanEvent>>> it = threads.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<Integer, Vector<SwanEvent>> entry = it.next();
                    int tid = entry.getKey();
                    Vector<SwanEvent> track = entry.getValue();

                    if (!find1 && containsAll(track, cl1, ln1)) {
                        find1 = true;
                        relatedThreads.add(tid);
                    } else if (!find2 && containsAll(track, cl2, ln2)) {
                        find2 = true;
                        relatedThreads.add(tid);
                    }

                    if (find1 && find2) {
                        break;
                    }
                }

                if (!find1 || !find2) {
                    throw new RuntimeException("Wrong input trace: cannot find the atomicity violations in the trace.");
                }

                int sz = -1;
                int nsz = -1;
                do {
                    sz = relatedThreads.size();
                    for (int i = 0; i < forks.size(); i++) {
                        SwanEvent fk = forks.get(i);
                        if (relatedThreads.contains(fk.sharedMemId)) {
                            relatedThreads.add(fk.threadId);
                        }
                        forks.remove(i--);
                    }
                    nsz = relatedThreads.size();
                } while (sz != nsz);

                // get all related classes,  methods (from #ln ~ #ln)
                List<MethodLineScope> mls = new ArrayList<MethodLineScope>();

                Set<String> relatedClasses = new HashSet<String>();
                for (int tid : relatedThreads) {
                    Vector<SwanEvent> track = threads.get(tid);
                    for (SwanEvent e : track) {
                        relatedClasses.add(e.clsname);
                    }
                }

                String defaultClassPath = Scene.v().defaultClassPath();
                Scene.v().setSootClassPath(defaultClassPath + File.pathSeparatorChar + cp);
                Scene.v().loadBasicClasses();

                for (String cls : relatedClasses) {
                    SootClass sootcls = Scene.v().loadClassAndSupport(cls);

                    List<SootMethod> methods = sootcls.getMethods();
                    for (SootMethod sm : methods) {
                        Stmt s = getFirstNonIdentityStmt(sm, sm.retrieveActiveBody().getUnits());

                        int fln = getLineNum(s);
                        int lln = getLineNum(sm);
                        mls.add(new MethodLineScope(sm, cls, fln, lln));
                    }
                }

                // get all used methods, and construct petrinet
                for (int i = 0; i < mls.size(); i++) {
                    MethodLineScope m = mls.get(i);

                    SwanEvent matchedEvent = null;
                    for (int tid : relatedThreads) {
                        Vector<SwanEvent> track = threads.get(tid);
                        for (SwanEvent se : track) {
                            if (se.lineNo >= m.from && se.lineNo <= m.to) {
                                matchedEvent = se;
                                break;
                            }
                        }
                        if (matchedEvent != null) {
                            break;
                        }
                    }
                    if (matchedEvent == null) {
                        mls.remove(i--);
                    } else {
                        // construct petri net

                        m.construct_petri_net();
                    }
                }

                // connect petrinet according to trace order
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        } else {
            throw new RuntimeException("Trace file error: " + f.getAbsolutePath() + ".");
        }
    }

    private static boolean containsAll(Vector<SwanEvent> aThread, List<String> clses, List<Integer> lns) {
        boolean containsAll = true;

        for (int i = 0; i < clses.size(); i++) {
            String cls = clses.get(i);
            int ln = lns.get(i);

            boolean contains = false;
            for (SwanEvent se : aThread) {
                if (se.clsname.equals(cls) && se.lineNo == ln) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                containsAll = false;
                break;
            }
        }

        return containsAll;
    }

    private static int getLineNum(Host h) {
        if (h.hasTag("LineNumberTag")) {
            return ((LineNumberTag) h.getTag("LineNumberTag")).getLineNumber();
        }
        if (h.hasTag("SourceLineNumberTag")) {
            return ((SourceLineNumberTag) h.getTag("SourceLineNumberTag")).getLineNumber();
        }
        if (h.hasTag("SourceLnPosTag")) {
            return ((SourceLnPosTag) h.getTag("SourceLnPosTag")).startLn();
        }
        return 0;
    }

    private static Stmt getFirstNonIdentityStmt(SootMethod sm, Chain units) {
        Stmt s = (Stmt) units.getFirst();
        while (s instanceof IdentityStmt) {
            s = (Stmt) units.getSuccOf(s);
        }
        return s;
    }
}

class MethodLineScope {

    String classname;
    SootMethod method;
    int from;
    int to;

    Map<Stmt, Node> snmap = new HashMap<Stmt, Node>();
    Node petrinet_root;
    Node petrinet_last;

    public MethodLineScope(SootMethod s, String classname, int f, int t) {
        this.method = s;
        this.from = f;
        this.to = t;
        this.classname = classname;
    }

    public void construct_petri_net() {
        ///@TODO
        Chain units = method.retrieveActiveBody().getUnits();
        for (Object u : units) {
            Node n = retrieveNode((Stmt) u);
            if (petrinet_root == null) {
                petrinet_root = n;
                ((Place) petrinet_root).setToken();
            }

            if (((Stmt) u).branches()) {
                if (u instanceof GotoStmt) {
                    // one target
                    Stmt next = (Stmt) units.getSuccOf(u);
                    addNext((Place) n, next);
                } else if (u instanceof IfStmt) {
                    // two targets
                    Stmt next = (Stmt) units.getSuccOf(u);
                    addNext((Place) n, next);

                    next = ((IfStmt) u).getTarget();
                    addNext((Place) n, next);
                } else if (u instanceof TableSwitchStmt || u instanceof LookupSwitchStmt) {
                    //several targets
                    List targets = null;
                    if (u instanceof TableSwitchStmt) {
                        targets = ((TableSwitchStmt) u).getTargets();
                    } else {
                        targets = ((LookupSwitchStmt) u).getTargets();
                    }
                    if (targets == null || targets.isEmpty()) {
                        Stmt next = (Stmt) units.getSuccOf(u);
                        addNext((Place) n, next);
                    } else {
                        for (Object next : targets) {
                            addNext((Place) n, (Stmt) next);
                        }
                    }
                } else {
                    System.err.println("Unknown Branch Stmt: " + u);
                }
            } else if (u instanceof EnterMonitorStmt) {
                
            } else if (u instanceof ExitMonitorStmt) {
                
            } else {
                // one target
                Stmt next = (Stmt) units.getSuccOf(u);
                addNext((Place) n, next);
            }
        }
    }

    Node retrieveNode(Stmt s) {
        if (snmap.containsKey(s)) {
            return snmap.get(s);
        } else {
            Node n = new Place(s);
            snmap.put(s, n);
            return n;
        }
    }

    void addNext(Place n, Stmt next) {
        if (next == null) {
            return;
        }
        Node nextNode = retrieveNode(next);
        Transition t = new Transition();
        n.addNext(t);
        t.addNext(nextNode);
    }
}
