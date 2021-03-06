/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libmonitor;

import cn.edu.nju.software.libevent.SwanEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author qingkaishi
 */
public abstract class MonitorWorker {

    protected List<Lock> locks;
    protected List<Condition> conditions;
    protected List<Thread> threads = new ArrayList<Thread>();
    protected List<List<Integer>> lockObjects = new ArrayList<List<Integer>>();
    protected ConcurrentLinkedQueue<SwanEvent> trace = new ConcurrentLinkedQueue<SwanEvent>();

    Map<Object, Integer> lockObjectMap = new HashMap<Object, Integer>();

    public MonitorWorker(int lockNum) {
        List<Lock> locks = new ArrayList<Lock>(lockNum);
        List<Condition> conditions = new ArrayList<Condition>(lockNum);
        for (int i = 0; i < lockNum; i++) {
            Lock l = new ReentrantLock();
            locks.add(l);
            conditions.add(l.newCondition());
        }

        this.locks = locks;
        this.conditions = conditions;
        this.threads.add(Thread.currentThread()); // main thread
        this.lockObjects.add(new ArrayList<Integer>()); // the lock objects that current thread holds
    }

    public abstract void myBeforeLock(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterLock(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeUnlock(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterUnlock(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeWait(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterWait(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeNotify(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterNotify(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeNotifyAll(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterNotifyAll(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeThreadStart(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterThreadStart(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeThreadJoin(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterThreadJoin(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeRead(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterRead(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myBeforeWrite(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myAfterWrite(Object o, int svno, int lineno, String clsname, int debug);

    public abstract void myInit(int lockNum);

    public abstract void myExit();

    public synchronized int getLockObjectId(Object o) {
        if (!lockObjectMap.containsKey(o)) {
            lockObjectMap.put(o, -1 - lockObjectMap.size());
        }

        return lockObjectMap.get(o);
    }

    public void setTrace(Vector<SwanEvent> trace) {
        this.trace.addAll(trace);
    }
}
