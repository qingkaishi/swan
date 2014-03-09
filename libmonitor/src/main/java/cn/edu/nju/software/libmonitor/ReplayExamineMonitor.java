/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

import cn.edu.nju.software.libevent.SwanEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;

/**
 *
 * @author qingkaishi
 */
public class ReplayExamineMonitor extends MonitorWorker {

    private boolean start = false;

    public ReplayExamineMonitor(int lockNum) {
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
        if (debug < 0) {
            matchUsingSleep(se);
        } else {
            matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.RELEASE, lineno);
        if (debug < 0) {
            matchUsingSleep(se);
        } else {
            matchUsingObject(o, se);
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
        if (debug < 0) {
            matchUsingSleep(se);
        } else {
            matchUsingObject(o, se);
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

        SwanEvent se = new SwanEvent(tid,
                lockId, null, SwanEvent.AccessType.RELEASE, lineno);
        if (debug < 0) {
            matchUsingSleep(se);
        } else {
            matchUsingObject(o, se);
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

                SwanEvent se = new SwanEvent(tid,
                        lockId, null, SwanEvent.AccessType.ACQUIRE, lineno);
                if (debug < 0) {
                    matchUsingSleep(se);
                } else {
                    matchUsingObject(c, se);
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

                SwanEvent se = new SwanEvent(tid,
                        lockId, null, SwanEvent.AccessType.RELEASE, lineno);

                if (debug < 0) {
                    matchUsingSleep(se);
                } else {
                    matchUsingObject(c, se);
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

    //private final Map<Integer, Integer> blocked = new ConcurrentHashMap<Integer, Integer>();
    //private int specialThreadId = -1;

    private void matchUsingCondition(Condition c, SwanEvent se) {
        int type = match(se, trace.peek());
        while (!trace.isEmpty() && (type > 0)
                /*&& se.threadId != specialThreadId*/) {
            /*if (type > 0) {
                blocked.put(se.threadId, type);
                Set<Integer> keysets = blocked.keySet();
                
                Set<Integer> activeThreadIds = new HashSet<Integer>();
                for (int i = 0; i < threads.size(); i++) {
                    if (threads.get(i).isAlive()) {
                        activeThreadIds.add(i);
                    }
                }

                if (keysets.containsAll(activeThreadIds)) {
                    Map.Entry<Integer, Integer> cand = null;
                    Iterator<Map.Entry<Integer, Integer>> it = blocked.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Integer, Integer> entry = it.next();
                        if (cand == null) {
                            cand = entry;
                        } else {
                            if (entry.getValue() > cand.getValue()) {
                                cand = entry;
                            }
                        }
                    }
                    specialThreadId = cand.getKey();
                }
            }*/

            try {
                c.awaitNanos(100000000); //100ms
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            type = match(se, trace.peek());
        }

        /*blocked.remove(se.threadId);
        if (se.threadId == specialThreadId) {
            specialThreadId = -1;
        }*/

        if (type == 0) {
            trace.poll();
        }
    }

    private void matchUsingObject(Object c, SwanEvent se) {
        int type = match(se, trace.peek());
        while (!trace.isEmpty() && (type > 0)
                /*&& se.threadId != specialThreadId*/) {
            /*if (type > 0) {
                blocked.put(se.threadId, type);
                Set<Integer> keysets = blocked.keySet();

                Set<Integer> activeThreadIds = new HashSet<Integer>();
                for (int i = 0; i < threads.size(); i++) {
                    if (threads.get(i).isAlive()) {
                        activeThreadIds.add(i);
                    }
                }

                if (keysets.containsAll(activeThreadIds)) {
                    Map.Entry<Integer, Integer> cand = null;
                    Iterator<Map.Entry<Integer, Integer>> it = blocked.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Integer, Integer> entry = it.next();
                        if (cand == null) {
                            cand = entry;
                        } else {
                            if (entry.getValue() > cand.getValue()) {
                                cand = entry;
                            }
                        }
                    }
                    specialThreadId = cand.getKey();
                }
            }*/

            try {
                c.wait(100); //100ms
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            type = match(se, trace.peek());
        }

        /*blocked.remove(se.threadId);
        if (se.threadId == specialThreadId) {
            specialThreadId = -1;
        }*/

        if (type == 0) {
            trace.poll();
        }
    }

    private void matchUsingSleep(SwanEvent se) {
        int type = match(se, trace.peek());
        while (!trace.isEmpty() && (type > 0)
                /*&& se.threadId != specialThreadId*/) {
            /*if (type > 0) {
                blocked.put(se.threadId, type);
                Set<Integer> keysets = blocked.keySet();

                Set<Integer> activeThreadIds = new HashSet<Integer>();
                for (int i = 0; i < threads.size(); i++) {
                    if (threads.get(i).isAlive()) {
                        activeThreadIds.add(i);
                    }
                }

                if (keysets.containsAll(activeThreadIds)) {
                    Map.Entry<Integer, Integer> cand = null;
                    Iterator<Map.Entry<Integer, Integer>> it = blocked.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Integer, Integer> entry = it.next();
                        if (cand == null) {
                            cand = entry;
                        } else {
                            if (entry.getValue() > cand.getValue()) {
                                cand = entry;
                            }
                        }
                    }
                    specialThreadId = cand.getKey();
                }
            }*/

            try {
                Thread.sleep(100); //100ms
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            type = match(se, trace.peek());
        }

        /*blocked.remove(se.threadId);
        if (se.threadId == specialThreadId) {
            specialThreadId = -1;
        }*/

        if (type == 0) {
            trace.poll();
        }
    }

    // match = 0; no match = -1; may match = + digit;
    private int match(SwanEvent se1, SwanEvent se2) {
        if (se1.equals(se2)) {
            return 0;
        }

        if (trace.isEmpty()) {
            return 0;
        }

        if (matchX(se1, se2)) {
            // rule 2, same thread, and match successfully
            return 0;
        } else {
            int distance = 0;
            boolean mayMatch = false;
            Iterator<SwanEvent> it = trace.iterator();
            while (it.hasNext()) {
                SwanEvent e = it.next();
                if (matchX(se1, e)) {
                    mayMatch = true;
                    break;
                }
                if (se1.threadId == e.threadId) {
                    ++distance;
                }
            }

            if (mayMatch) {
                // rule 3.b some one may match it
                if(se1.threadId == se2.threadId){
                    trace.poll();
                    return distance + 2;
                }
                return 1;
            } else {
                // rule 3.a no one can match it
                return -1;
            }
        }
    }

    private boolean matchX(SwanEvent se1, SwanEvent se2) {
        if (se1 == null && se2 != null) {
            return false;
        }

        if (se1 != null && se2 == null) {
            return false;
        }

        if (se1 == se2 || se1.equals(se2) || trace.isEmpty()) {
            return true;
        }

        return (se1.sharedMemId == se2.sharedMemId || (se1.sharedMemId < 0 && se2.sharedMemId < 0))
                && se1.accessType == se2.accessType
                && se1.threadId == se2.threadId
                && se1.lineNo == se2.lineNo;
    }
}
