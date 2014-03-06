/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

import java.util.Date;

/**
 *
 * @author qingkaishi
 */
public class Monitor {

    private static MonitorWorker realWorker;
    private static String MonitorWorkerType = null;

    public static void setMonitorWorkerType(String type) {
        MonitorWorkerType = type;
    }

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
        try {
            // handle cmd line
            if ("r".equals(MonitorWorkerType)) {
                // record
                realWorker = new RecordMonitor(lockNum);
            } else if ("R".equals(MonitorWorkerType)) {
                // replay
                realWorker = new ReplayMonitor(lockNum);
            } else if ("e".equals(MonitorWorkerType)) {
                // replay-record
                realWorker = new ReplayRecordMonitor(lockNum);
            } else if ("x".equals(MonitorWorkerType)) {
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

        System.out.println("*******************************");
        System.out.println("* Executing with Swan (" + MonitorWorkerType + ")...");
        System.out.println("* " + new Date(System.currentTimeMillis()));
        System.out.println("*******************************");
        System.out.println("");
    }

    public static void myExit() {
        realWorker.myExit();
    }

    public static void myCleanup() {
    }

}
