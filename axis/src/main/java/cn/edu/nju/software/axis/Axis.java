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
import java.util.List;
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

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        } else {
            throw new RuntimeException("Trace file error: " + f.getAbsolutePath() + ".");
        }
    }
}
