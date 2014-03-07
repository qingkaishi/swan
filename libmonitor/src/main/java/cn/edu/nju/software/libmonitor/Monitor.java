/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

import cn.edu.nju.software.libevent.SwanEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
public class Monitor {

    private static MonitorWorker realWorker;
    private static String MonitorWorkerType = null;
    private static File TraceFile = null;

    public static void setMonitorWorkerType(String type) {
        MonitorWorkerType = type;
    }

    public static void setTraceFile(File file) {
        TraceFile = file;
    }

    public static void myBeforeLock(Object o, int svno, int lineno, int debug) {
        //System.out.println("before Lock");
        realWorker.myBeforeLock(o, svno, lineno, debug);
    }

    public static void myAfterLock(Object o, int svno, int lineno, int debug) {
        //System.out.println("after Lock");
        realWorker.myAfterLock(o, svno, lineno, debug);
    }

    public static void myBeforeUnlock(Object o, int svno, int lineno, int debug) {
        //System.out.println("before unLock");
        realWorker.myBeforeUnlock(o, svno, lineno, debug);
    }

    public static void myAfterUnlock(Object o, int svno, int lineno, int debug) {
        //System.out.println("after unLock");
        realWorker.myAfterUnlock(o, svno, lineno, debug);
    }

    public static void myBeforeWait(Object o, int svno, int lineno, int debug) {
        //System.out.println("before wait");
        realWorker.myBeforeWait(o, svno, lineno, debug);
    }

    public static void myAfterWait(Object o, int svno, int lineno, int debug) {
        //System.out.println("after wait");
        realWorker.myAfterWait(o, svno, lineno, debug);
    }

    public static void myBeforeNotify(Object o, int svno, int lineno, int debug) {
        //System.out.println("before notify");
        realWorker.myBeforeNotify(o, svno, lineno, debug);
    }

    public static void myAfterNotify(Object o, int svno, int lineno, int debug) {
        //System.out.println("after notify");
        realWorker.myAfterNotify(o, svno, lineno, debug);
    }

    public static void myBeforeNotifyAll(Object o, int svno, int lineno, int debug) {
        //System.out.println("before notify all");
        realWorker.myBeforeNotifyAll(o, svno, lineno, debug);
    }

    public static void myAfterNotifyAll(Object o, int svno, int lineno, int debug) {
        //System.out.println("after notify all");
        realWorker.myAfterNotifyAll(o, svno, lineno, debug);
    }

    public static void myBeforeThreadStart(Object o, int svno, int lineno, int debug) {
        //System.out.println("before thread start");
        realWorker.myBeforeThreadStart(o, svno, lineno, debug);
    }

    public static void myAfterThreadStart(Object o, int svno, int lineno, int debug) {
        //System.out.println("after thread start");
        realWorker.myAfterThreadStart(o, svno, lineno, debug);
    }

    public static void myBeforeThreadJoin(Object o, int svno, int lineno, int debug) {
        //System.out.println("before j");
        realWorker.myBeforeThreadJoin(o, svno, lineno, debug);
    }

    public static void myAfterThreadJoin(Object o, int svno, int lineno, int debug) {
        //System.out.println("after j");
        realWorker.myAfterThreadJoin(o, svno, lineno, debug);
    }

    public static void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, int debug) {
        //System.out.println("before sync ins");
        realWorker.myBeforeSynchronizedInsInvoke(o, svno, lineno, debug);
    }

    public static void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, int debug) {
        //System.out.println("after sync ins");
        realWorker.myAfterSynchronizedInsInvoke(o, svno, lineno, debug);
    }

    public static void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug) {
        //System.out.println("before sync static");
        realWorker.myBeforeSynchronizedStaticInvoke(o, svno, lineno, debug);
    }

    public static void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug) {
        //System.out.println("after sync static");
        realWorker.myAfterSynchronizedStaticInvoke(o, svno, lineno, debug);
    }

    public static void myBeforeRead(Object o, int svno, int lineno, int debug) {
        //System.out.println("before read");
        realWorker.myBeforeRead(o, svno, lineno, debug);
    }

    public static void myAfterRead(Object o, int svno, int lineno, int debug) {
        //System.out.println("after read");
        realWorker.myAfterRead(o, svno, lineno, debug);
    }

    public static void myBeforeWrite(Object o, int svno, int lineno, int debug) {
        //System.out.println("before write");
        realWorker.myBeforeWrite(o, svno, lineno, debug);
    }

    public static void myAfterWrite(Object o, int svno, int lineno, int debug) {
        //System.out.println("after read");
        realWorker.myAfterWrite(o, svno, lineno, debug);
    }

    public static void myInit(int lockNum) {
        try {
            // handle cmd line
            if ("r".equals(MonitorWorkerType)) {
                // record
                realWorker = new RecordMonitor(lockNum);
            } else {
                if ("R".equals(MonitorWorkerType)) {
                    // replay
                    realWorker = new ReplayMonitor(lockNum);
                } else if ("e".equals(MonitorWorkerType)) {
                    // replay-record
                    realWorker = new ReplayRecordMonitor(lockNum);
                } else if ("x".equals(MonitorWorkerType)) {
                    // replay-examine
                    realWorker = new ReplayExamineMonitor(lockNum);
                } else {
                    // empty
                    realWorker = new EmptyMonitor(lockNum);
                }

                FileInputStream fis = new FileInputStream(TraceFile);
                ObjectInputStream ois = new ObjectInputStream(fis);

                Vector<SwanEvent> trace = (Vector<SwanEvent>) ois.readObject();
                realWorker.setTrace(trace);
                ois.close();
                TraceFile = null;
                trace = null;
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

        System.out.println("*******************************");
        System.out.println("* Executing with Swan (" + MonitorWorkerType + ")...");
        System.out.println("* " + new Date(System.currentTimeMillis()));
        System.out.println("*******************************");
        System.out.println("");

        realWorker.myInit(lockNum);
    }

    public static void myExit() {
        realWorker.myExit();
    }

    public static void myCleanup() {
    }

}
