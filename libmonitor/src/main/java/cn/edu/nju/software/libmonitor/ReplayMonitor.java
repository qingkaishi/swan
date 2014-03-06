/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

import cn.edu.nju.software.libmonitor.event.SwanEvent;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.locks.Condition;

/**
 *
 * @author qingkaishi
 */
public class ReplayMonitor extends MonitorWorker {

    private boolean start = false;

    public ReplayMonitor(int lockNum) {
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
        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.ACQUIRE, lineno);
        matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.RELEASE, lineno);
        matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.WAIT_RELEASE, lineno);
        matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.WAIT_ACQUIRE, lineno);
        matchUsingObject(o, se);
    }

    @Override
    public void myBeforeNotify(Object o, int svno, int lineno, int debug) {
        if (!start) {
            return;
        }
        while (!threads.contains(Thread.currentThread())) {
        }
        int tid = threads.indexOf(Thread.currentThread());

        SwanEvent se = new SwanEvent(tid,
                this.getLockObjectId(o), null, SwanEvent.AccessType.NOTIFYALL, lineno);
        matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                this.getLockObjectId(o), null, SwanEvent.AccessType.NOTIFYALL, lineno);
        matchUsingObject(o, se);
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
        int startTid = threads.indexOf(o);
        SwanEvent se = new SwanEvent(tid,
                startTid, null, SwanEvent.AccessType.FORK, lineno);
        matchUsingCondition(conditions.get(svno), se);
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
        int joinTid = threads.indexOf(o);
        SwanEvent se = new SwanEvent(tid,
                joinTid, null, SwanEvent.AccessType.JOIN, lineno);

        matchUsingSleep(se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.ACQUIRE, lineno);
        matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.RELEASE, lineno);
        matchUsingObject(o, se);
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

                SwanEvent se = new SwanEvent(tid,
                        lockId, null, SwanEvent.AccessType.ACQUIRE, lineno);
                matchUsingObject(c, se);
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

                SwanEvent se = new SwanEvent(tid,
                        lockId, null, SwanEvent.AccessType.RELEASE, lineno);

                matchUsingObject(c, se);
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
        SwanEvent se = new SwanEvent(tid,
                svno, null, SwanEvent.AccessType.READ, lineno);

        matchUsingCondition(conditions.get(svno), se);
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
        SwanEvent se = new SwanEvent(tid,
                svno, null, SwanEvent.AccessType.WRITE, lineno);

        matchUsingCondition(conditions.get(svno), se);
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
    }

    public void setTrace(Vector<SwanEvent> trace) {
        this.trace.addAll(trace);
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
