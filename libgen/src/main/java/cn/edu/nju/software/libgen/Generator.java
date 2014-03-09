/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libgen;

import cn.edu.nju.software.libevent.SwanEvent;
import cn.edu.nju.software.libgen.util.KernelGraph;
import cn.edu.nju.software.libgen.util.PMAP;
import cn.edu.nju.software.libgen.util.PMAPSearcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
public class Generator {

    public static void startGeneration(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("No trace file to re-generate.");
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

                KernelGraph kg = KernelGraph.v(trace);
                kg.test();

                List<PMAP> pmaps = PMAPSearcher.search(trace, kg);
                System.out.println("[Swan] There are " + pmaps.size() + " possible atomicity violations to cover!");
                System.out.println(pmaps);
                List<PMAP> reduced_pmaps = PMAPSearcher.optimize(pmaps, kg);
                System.out.println("[Swan] There are ONLY " + reduced_pmaps.size() + " possible atomicity violations AFTER OPTIMIZATIONS!");
                System.out.println(reduced_pmaps);
                
                if(reduced_pmaps.size() == 0) {
                    System.out.println("[Swan] NO possible atomicity violations to cover!");
                    System.out.println("[Swan] There are two possible reasons:");
                    System.out.println("[Swan]     [1] The input trace is not a buggy trace.");
                    System.out.println("[Swan]     [2] You have fixed it sufficiently.");
                }

                kg.generateTestSchedules(reduced_pmaps);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        } else {
            throw new RuntimeException("Trace file error: " + f.getAbsolutePath() + ".");
        }
    }
}
