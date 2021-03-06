/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libmonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * only for evaluation
 * 
 * @author qingkaishi
 */
public class StrideMonitor extends MonitorWorker {
    private volatile boolean start = false;

    private Vector<Integer> threadStartLog = new Vector<Integer>();

    private Vector<Vector<Integer>> lockLog = new Vector<Vector<Integer>>();
    private Vector<Vector<Integer>> writeLog = new Vector<Vector<Integer>>();
    private Vector<Integer> versions = new Vector<Integer>();

    // thread local
    private Map<Integer, Map<Object, Vector<Integer>>> readValueLog = new HashMap<Integer, Map<Object, Vector<Integer>>>();
    private Map<Integer, Map<Object, Vector<Integer>>> readVersionLog = new HashMap<Integer, Map<Object, Vector<Integer>>>();

    public StrideMonitor(int lockNum) {
        super(lockNum);

        for (int i = 0; i < lockNum; i++) {
            lockLog.add(new Vector<Integer>());
            writeLog.add(new Vector<Integer>());
            versions.add(0);
        }
    }

    @Override
    public void myBeforeLock(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterLock(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        while (!threads.contains(currentThread)) {
            Thread.yield();
        }
        Vector<Integer> lockLogForO = lockLog.get(svno);
        lastOnePredictorAdd(lockLogForO, threads.indexOf(currentThread));
    }

    @Override
    public void myBeforeUnlock(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterUnlock(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeWait(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterWait(Object o, int svno, int lineno, String clsname, int debug) {
        this.myAfterLock(o, svno, lineno, clsname, debug);
    }

    @Override
    public void myBeforeNotify(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterNotify(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeNotifyAll(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterNotifyAll(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeThreadStart(Object o, int svno, int lineno, String clsname, int debug) {
        locks.get(svno).lock();
        threads.add((Thread) o);
        start = true;

        Thread currentThread = Thread.currentThread();
        while (!threads.contains(currentThread)) {
            Thread.yield();
        }
        lastOnePredictorAdd(threadStartLog, threads.indexOf(currentThread));
    }

    @Override
    public void myAfterThreadStart(Object o, int svno, int lineno, String clsname, int debug) {
        locks.get(svno).unlock();
    }

    @Override
    public void myBeforeThreadJoin(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterThreadJoin(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        this.myAfterLock(o, svno, lineno, clsname, debug);
    }

    @Override
    public void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        if (o instanceof String) {
            try {
                this.myAfterLock(Class.forName((String)o), svno, lineno, clsname, debug);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new RuntimeException("Unsupported synchronization object: " + o.toString() + ".");
        }
    }

    @Override
    public void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeRead(Object o, int svno, int lineno, String clsname, int debug) {

    }

    @Override
    public void myAfterRead(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }

        Thread currentThread = Thread.currentThread();
        while (!threads.contains(currentThread)) {
            Thread.yield();
        }

        {
            int version = versions.get(svno);
            // version log, 
            Map<Object, Vector<Integer>> verLogForThisThread = readVersionLog.get(currentThread);
            if (verLogForThisThread == null) {
                verLogForThisThread = new HashMap<Object, Vector<Integer>>();
                readVersionLog.put(currentThread.hashCode(), verLogForThisThread);
            }
            Vector<Integer> verLogForO = verLogForThisThread.get(o);
            if (verLogForO == null) {
                verLogForO = new Vector<Integer>();
                verLogForThisThread.put(o, verLogForO);
            }
            lastOnePredictorAdd(verLogForO, version);
        }

        {
            // value log 
            Map<Object, Vector<Integer>> valLogForThisThread = readValueLog.get(currentThread);
            if (valLogForThisThread == null) {
                valLogForThisThread = new HashMap<Object, Vector<Integer>>();
                readValueLog.put(currentThread.hashCode(), valLogForThisThread);
            }
            Vector<Integer> valLogForO = valLogForThisThread.get(o);
            if (valLogForO == null) {
                valLogForO = new Vector<Integer>();
                valLogForThisThread.put(o, valLogForO);
            }
            lastOnePredictorAdd(valLogForO, o.hashCode());
        }
    }

    @Override
    public void myBeforeWrite(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }

        locks.get(svno).lock();

        while (!threads.contains(Thread.currentThread())) {
            Thread.yield();
        }

        // version ++
        versions.set(svno, versions.get(svno) + 1);
        // write log
        lastOnePredictorAdd(writeLog.get(svno), threads.indexOf(Thread.currentThread()));
    }

    @Override
    public void myAfterWrite(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        locks.get(svno).unlock();
    }

    @Override
    public void myExit() {
        Vector<Integer> serializableTrace = new Vector<Integer>();
        serializableTrace.addAll(threadStartLog);
        for(Vector<Integer> v : lockLog) {
            serializableTrace.addAll(v);
        }
        for(Vector<Integer> v : writeLog) {
            serializableTrace.addAll(v);
        }
        
        Iterator<Integer> it1 = readValueLog.keySet().iterator();
        while(it1.hasNext()) {
            int key = it1.next();
            
            Map<Object, Vector<Integer>> valmap = readValueLog.get(key);
            Iterator<Object> it = valmap.keySet().iterator();
            while(it.hasNext()) {
                Object objKey = it.next();
                serializableTrace.addAll(valmap.get(objKey));
            }
            
            Map<Object, Vector<Integer>> vermap = readVersionLog.get(key);
            it = vermap.keySet().iterator();
            while(it.hasNext()) {
                Object objKey = it.next();
                serializableTrace.addAll(vermap.get(objKey));
            }
        }
        
        try {
            FileOutputStream fos = new FileOutputStream("./orig.trace.gz");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serializableTrace);
            oos.close();
            
            File f = new File("./orig.trace.gz");
            System.out.println(">>>>>>>>>>>> Log Size: " + f.length() + "Bytes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void myInit(int lockNum) {
    }

    private static void lastOnePredictorAdd(Vector<Integer> vec, int toAdd) {
        if (vec.isEmpty()) {
            vec.add(toAdd);
            vec.add(1);
        } else {
            int last = vec.lastElement();
            int prelast = vec.get(vec.size() - 2);

            if (prelast == toAdd) {
                vec.set(vec.size() - 1, last + 1);
            } else {
                vec.add(toAdd);
                vec.add(1);
            }
        }
    }

}
