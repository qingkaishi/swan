/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libmonitor;

/**
 *
 * @author qingkaishi
 */
public abstract class MonitorWorker {

    public abstract void myBeforeLock(Object o, int svno, int lineno, int debug);

    public abstract void myAfterLock(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeUnlock(Object o, int svno, int lineno, int debug);

    public abstract void myAfterUnlock(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeWait(Object o, int svno, int lineno, int debug);

    public abstract void myAfterWait(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeNotify(Object o, int svno, int lineno, int debug);

    public abstract void myAfterNotify(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeNotifyAll(Object o, int svno, int lineno, int debug);

    public abstract void myAfterNotifyAll(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeThreadStart(Object o, int svno, int lineno, int debug);

    public abstract void myAfterThreadStart(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeThreadJoin(Object o, int svno, int lineno, int debug);

    public abstract void myAfterThreadJoin(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, int debug);

    public abstract void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug);

    public abstract void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeRead(Object o, int svno, int lineno, int debug);

    public abstract void myAfterRead(Object o, int svno, int lineno, int debug);

    public abstract void myBeforeWrite(Object o, int svno, int lineno, int debug);

    public abstract void myAfterWrite(Object o, int svno, int lineno, int debug);
}
