/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libmonitor;

import cn.edu.nju.software.libevent.SwanEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
public class RecordMonitor extends MonitorWorker {

    private volatile boolean start = false;

    public RecordMonitor(int lockNum) {
        super(lockNum);
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
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        int lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);
        curLockIds.add(lockId);
        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.ACQUIRE, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myBeforeUnlock(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        Integer lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);

        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.RELEASE, clsname, lineno);
        trace.add(se);

        curLockIds.remove(lockId);
    }

    @Override
    public void myAfterUnlock(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeWait(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        Integer lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);

        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.WAIT_RELEASE, clsname, lineno);
        trace.add(se);

        curLockIds.remove(lockId);
    }

    @Override
    public void myAfterWait(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        int lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);
        curLockIds.add(lockId);

        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.WAIT_ACQUIRE, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myBeforeNotify(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);

        SwanEvent se = new SwanEvent(tid, this.getLockObjectId(o), curLockIds, SwanEvent.AccessType.NOTIFY, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myAfterNotify(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeNotifyAll(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);

        SwanEvent se = new SwanEvent(tid,
                this.getLockObjectId(o), curLockIds, SwanEvent.AccessType.NOTIFYALL, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myAfterNotifyAll(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myBeforeThreadStart(Object o, int svno, int lineno, String clsname, int debug) {
        locks.get(svno).lock();
        threads.add((Thread) o);
        lockObjects.add(new ArrayList<Integer>());
        start = true;

        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        int startTid = threads.indexOf(o);
        SwanEvent se = new SwanEvent(tid,
                startTid, curLockIds, SwanEvent.AccessType.FORK, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myAfterThreadStart(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        locks.get(svno).unlock();
    }

    @Override
    public void myBeforeThreadJoin(Object o, int svno, int lineno, String clsname, int debug) {
        // do nothing
    }

    @Override
    public void myAfterThreadJoin(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        int joinTid = threads.indexOf(o);
        SwanEvent se = new SwanEvent(threads.indexOf(Thread.currentThread()),
                joinTid, curLockIds, SwanEvent.AccessType.JOIN, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }

        int tid = threads.indexOf(Thread.currentThread());
        int lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);
        curLockIds.add(lockId);

        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.ACQUIRE, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        Integer lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);

        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.RELEASE, clsname, lineno);
        trace.add(se);

        curLockIds.remove(lockId);
    }

    @Override
    public void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        if (o instanceof String) {
            try {
                Class<?> c = Class.forName((String) o);
                while (!threads.contains(Thread.currentThread())) {
                }

                int tid = threads.indexOf(Thread.currentThread());
                int lockId = this.getLockObjectId(c);
                List<Integer> curLockIds = lockObjects.get(tid);
                curLockIds.add(lockId);

                SwanEvent se = new SwanEvent(tid,
                        lockId, curLockIds, SwanEvent.AccessType.ACQUIRE, clsname, lineno);
                trace.add(se);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        } else {
            throw new RuntimeException("Unsupported synchronization object: " + o.toString() + ".");
        }
    }

    @Override
    public void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        if (o instanceof String) {
            try {
                Class<?> c = Class.forName((String) o);
                while (!threads.contains(Thread.currentThread())) {
                }
                int tid = threads.indexOf(Thread.currentThread());
                Integer lockId = this.getLockObjectId(c);
                List<Integer> curLockIds = lockObjects.get(tid);

                SwanEvent se = new SwanEvent(tid,
                        lockId, curLockIds, SwanEvent.AccessType.RELEASE, clsname, lineno);
                trace.add(se);
                curLockIds.remove(lockId);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        } else {
            throw new RuntimeException("Unsupported synchronization object: " + o.toString() + ".");
        }
    }

    @Override
    public void myBeforeRead(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }

        locks.get(svno).lock();

        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                svno, curLockIds, SwanEvent.AccessType.READ, clsname, lineno);
        trace.add(se);
    }

    @Override
    public void myAfterRead(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }
        locks.get(svno).unlock();
    }

    @Override
    public void myBeforeWrite(Object o, int svno, int lineno, String clsname, int debug) {
        if (!start) {
            return;
        }

        locks.get(svno).lock();

        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                svno, curLockIds, SwanEvent.AccessType.WRITE, clsname, lineno);
        trace.add(se);
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
        Vector<SwanEvent> serializableTrace = new Vector<SwanEvent>();
        serializableTrace.addAll(trace);
        try {
            FileOutputStream fos = new FileOutputStream("./orig.trace.gz");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serializableTrace);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void myInit(int lockNum) {
    }

}
