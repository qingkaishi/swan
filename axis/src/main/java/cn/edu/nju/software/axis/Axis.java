/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.axis;

import cn.edu.nju.software.libevent.SwanEvent;
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
import java.util.Vector;

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
        if (args.length < 2) {
            throw new RuntimeException("No trace file or input atomicity violations.");
        }

        List<String> cl1 = new ArrayList<String>();
        List<String> cl2 = new ArrayList<String>();

        List<Integer> ln1 = new ArrayList<Integer>();
        List<Integer> ln2 = new ArrayList<Integer>();

        try {
            String av = args[1];
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

        String filename = args[0];
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
                List<Integer> relatedThreads = new ArrayList<Integer>();
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
}
