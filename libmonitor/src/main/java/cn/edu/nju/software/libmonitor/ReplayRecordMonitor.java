/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

import cn.edu.nju.software.libevent.SwanEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;

/**
 *
 * @author qingkaishi
 */
public class ReplayRecordMonitor extends MonitorWorker {

    private boolean start = false;
    protected ConcurrentLinkedQueue<SwanEvent> newTrace = new ConcurrentLinkedQueue<SwanEvent>();

    public ReplayRecordMonitor(int lockNum) {
        super(lockNum);
    }

    @Override
    public void myBeforeLock(Object o, int svno, int lineno, int debug) {
    }

    @Override
    public void myAfterLock(Object o, int svno, int lineno, int debug) {
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
                lockId, curLockIds, SwanEvent.AccessType.ACQUIRE, lineno);
        if (debug < 0) {
        } else {
            matchUsingObject(o, se);
        }
        newTrace.add(se);

        if (debug < 0) {
            se.patchedEvent = true;
        }
    }

    @Override
    public void myBeforeUnlock(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        Integer lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);

        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.RELEASE, lineno);
        if (debug < 0) {
        } else {
            matchUsingObject(o, se);
        }

        newTrace.add(se);
        curLockIds.remove(lockId);
        if (debug < 0) {
            se.patchedEvent = true;
        }
    }

    @Override
    public void myAfterUnlock(Object o, int svno, int lineno, int debug) {
    }

    @Override
    public void myBeforeWait(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        Integer lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.WAIT_RELEASE, lineno);
        matchUsingObject(o, se);

        newTrace.add(se);
        curLockIds.remove(lockId);
    }

    @Override
    public void myAfterWait(Object o, int svno, int lineno, int debug) {
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
                lockId, curLockIds, SwanEvent.AccessType.WAIT_ACQUIRE, lineno);
        matchUsingObject(o, se);

        newTrace.add(se);
    }

    @Override
    public void myBeforeNotify(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                this.getLockObjectId(o), curLockIds, SwanEvent.AccessType.NOTIFYALL, lineno);
        matchUsingObject(o, se);
        newTrace.add(se);
    }

    @Override
    public void myAfterNotify(Object o, int svno, int lineno, int debug) {
    }

    @Override
    public void myBeforeNotifyAll(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                this.getLockObjectId(o), curLockIds, SwanEvent.AccessType.NOTIFYALL, lineno);
        matchUsingObject(o, se);
        newTrace.add(se);
    }

    @Override
    public void myAfterNotifyAll(Object o, int svno, int lineno, int debug) {
    }

    @Override
    public void myBeforeThreadStart(Object o, int svno, int lineno, int debug) {
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
                startTid, curLockIds, SwanEvent.AccessType.FORK, lineno);
        matchUsingCondition(conditions.get(svno), se);
        newTrace.add(se);
    }

    @Override
    public void myAfterThreadStart(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        locks.get(svno).unlock();
    }

    @Override
    public void myBeforeThreadJoin(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        int joinTid = threads.indexOf(o);
        SwanEvent se = new SwanEvent(tid,
                joinTid, curLockIds, SwanEvent.AccessType.JOIN, lineno);

        matchUsingSleep(se);
        newTrace.add(se);
    }

    @Override
    public void myAfterThreadJoin(Object o, int svno, int lineno, int debug) {
    }

    @Override
    public void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, int debug) {
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
                lockId, curLockIds, SwanEvent.AccessType.ACQUIRE, lineno);
        if (debug < 0) {
        } else {
            matchUsingObject(o, se);
        }
        newTrace.add(se);

        if (debug < 0) {
            se.patchedEvent = true;
        }
    }

    @Override
    public void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        Integer lockId = this.getLockObjectId(o);
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                lockId, curLockIds, SwanEvent.AccessType.RELEASE, lineno);
        if (debug < 0) {
        } else {
            matchUsingObject(o, se);
        }
        newTrace.add(se);
        curLockIds.remove(lockId);

        if (debug < 0) {
            se.patchedEvent = true;
        }
    }

    @Override
    public void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug) {
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
                        lockId, curLockIds, SwanEvent.AccessType.ACQUIRE, lineno);
                if (debug < 0) {
                } else {
                    matchUsingObject(c, se);
                }
                newTrace.add(se);

                if (debug < 0) {
                    se.patchedEvent = true;
                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        } else {
            throw new RuntimeException("Unsupported synchronization object: " + o.toString() + ".");
        }
    }

    @Override
    public void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug) {
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
                        lockId, curLockIds, SwanEvent.AccessType.RELEASE, lineno);

                if (debug < 0) {
                } else {
                    matchUsingObject(c, se);
                }
                newTrace.add(se);
                curLockIds.remove(lockId);

                if (debug < 0) {
                    se.patchedEvent = true;
                }
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        } else {
            throw new RuntimeException("Unsupported synchronization object: " + o.toString() + ".");
        }
    }

    @Override
    public void myBeforeRead(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }

        locks.get(svno).lock();

        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                svno, curLockIds, SwanEvent.AccessType.READ, lineno);

        matchUsingCondition(conditions.get(svno), se);
        newTrace.add(se);
    }

    @Override
    public void myAfterRead(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        locks.get(svno).unlock();
    }

    @Override
    public void myBeforeWrite(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }

        locks.get(svno).lock();

        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());
        List<Integer> curLockIds = lockObjects.get(tid);
        SwanEvent se = new SwanEvent(tid,
                svno, curLockIds, SwanEvent.AccessType.WRITE, lineno);

        matchUsingCondition(conditions.get(svno), se);
        newTrace.add(se);
    }

    @Override
    public void myAfterWrite(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        locks.get(svno).unlock();
    }

    @Override
    public void myInit(int lockNum) {
    }

    @Override
    public void myExit() {
        Vector<SwanEvent> serializableTrace = new Vector<SwanEvent>();
        serializableTrace.addAll(newTrace);
        try {
            FileOutputStream fos = new FileOutputStream("./orig_patch.trace.gz");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serializableTrace);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void matchUsingCondition(Condition c, SwanEvent se) {
        while (!trace.isEmpty() && !match(se, trace.peek())) {
            try {
                c.awaitNanos(100000000); //100ms
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        trace.poll();
    }

    private void matchUsingObject(Object c, SwanEvent se) {
        while (!trace.isEmpty() && !match(se, trace.peek())) {
            try {
                c.wait(100); //100ms
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        trace.poll();
    }

    private void matchUsingSleep(SwanEvent se) {
        while (!trace.isEmpty() && !match(se, trace.peek())) {
            try {
                Thread.sleep(100); //100ms
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        trace.poll();
    }

    private boolean match(SwanEvent se1, SwanEvent se2) {
        if (se1.equals(se2)) {
            return true;
        }

        return (se1.sharedMemId == se2.sharedMemId || (se1.sharedMemId < 0 && se2.sharedMemId < 0))
                && se1.accessType == se2.accessType
                && se1.threadId == se2.threadId;
    }
}
