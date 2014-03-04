/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author qingkaishi
 */
public class Monitor {
    
    private static MonitorWorker realWorker;

    public static void myBeforeLock(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeLock(o, svno, lineno, debug);
    }

    public static void myAfterLock(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterLock(o, svno, lineno, debug);
    }

    public static void myBeforeUnlock(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeUnlock(o, svno, lineno, debug);
    }

    public static void myAfterUnlock(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterUnlock(o, svno, lineno, debug);
    }

    public static void myBeforeWait(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeWait(o, svno, lineno, debug);
    }

    public static void myAfterWait(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterWait(o, svno, lineno, debug);
    }

    public static void myBeforeNotify(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeNotify(o, svno, lineno, debug);
    }

    public static void myAfterNotify(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterNotify(o, svno, lineno, debug);
    }

    public static void myBeforeNotifyAll(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeNotifyAll(o, svno, lineno, debug);
    }

    public static void myAfterNotifyAll(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterNotifyAll(o, svno, lineno, debug);
    }

    public static void myBeforeThreadStart(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeThreadStart(o, svno, lineno, debug);
    }

    public static void myAfterThreadStart(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterThreadStart(o, svno, lineno, debug);
    }

    public static void myBeforeThreadJoin(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeThreadJoin(o, svno, lineno, debug);
    }

    public static void myAfterThreadJoin(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterThreadJoin(o, svno, lineno, debug);
    }

    public static void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeSynchronizedInsInvoke(o, svno, lineno, debug);
    }

    public static void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterSynchronizedInsInvoke(o, svno, lineno, debug);
    }

    public static void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeSynchronizedStaticInvoke(o, svno, lineno, debug);
    }

    public static void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterSynchronizedStaticInvoke(o, svno, lineno, debug);
    }

    public static void myBeforeRead(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeRead(o, svno, lineno, debug);
    }

    public static void myAfterRead(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterRead(o, svno, lineno, debug);
    }

    public static void myBeforeWrite(Object o, int svno, int lineno, int debug) {
        realWorker.myBeforeWrite(o, svno, lineno, debug);
    }

    public static void myAfterWrite(Object o, int svno, int lineno, int debug) {
        realWorker.myAfterWrite(o, svno, lineno, debug);
    }

    public static void myInit(int lockNum) {
        System.out.println("*******************************");
        System.out.println("* Executing with Swan...");
        System.out.println("* " + new Date(System.currentTimeMillis()));
        System.out.println("*******************************");
        System.out.println("");
        
        try {
            // handle cmd line
            FileInputStream fis = new FileInputStream("/tmp/.swan.args");
            ObjectInputStream ois = new ObjectInputStream(fis);
            String[] args = (String[]) ois.readObject();
            ois.close();

            CommandLineParser parser = new PosixParser();
            CommandLine cl = parser.parse(prepareOptions(), args);

            if (cl.hasOption("r") && cl.hasOption("c")) {
                // record
                realWorker = new RecordMonitor(lockNum);
            } else if (cl.hasOption("R") && cl.hasOption("T") && cl.hasOption("c")) {
                // replay
                realWorker = new ReplayMonitor(lockNum);
            } else if (cl.hasOption("e") && cl.hasOption("T") && cl.hasOption("p") && cl.hasOption("c")) {
                // replay-record
                realWorker = new ReplayRecordMonitor(lockNum);
            } else if (cl.hasOption("x") && cl.hasOption("T") && cl.hasOption("p") && cl.hasOption("c")) {
                // replay-examine
                realWorker = new ReplayExamineMonitor(lockNum);
            } else {
                realWorker = new EmptyMonitor(lockNum);
            }
            
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    myExit();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void myExit() {
        realWorker.myExit();
    }

    public static void myCleanup() {
    }

    private static Options prepareOptions() {
        Options opt = new Options();

        opt.addOption("t", "transform", true, "transform the program to an instrumented version.");

        opt.addOption("r", "record", false, "record an exacution.");
        opt.addOption("R", "replay", true, "reproduce an exacution.");
        opt.addOption("e", "replay-record", true, "replay and record an exacution as a trace.");
        opt.addOption("x", "replay-examine", true, "replay a trace to examine fixes.");
        opt.addOption("g", "generate", true, "generate traces that may expose bugs.");

        opt.addOption("p", "patch", true, "the line number you synchronize your codes, e.g. ClassName:20,ClassName:21.");
        opt.addOption("T", "trace", true, "the input trace.");

        opt.addOption("c", "test-case", true, "your test cases, e.g. \"java -cp libmonitor.jar:other-dependencies -jar xxx.jar args\".");
        opt.addOption("P", "soot-class-path", true, "the soot class path.");
        opt.addOption("h", "help", false, "print this information.");

        return opt;
    }
   
}
