/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libmonitor;

/**
 *
 * @author qingkaishi
 */
public class EmptyMonitor extends MonitorWorker{

    public EmptyMonitor(int lockNum) {
        super(lockNum);
    }

    @Override
    public void myBeforeLock(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterLock(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeUnlock(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterUnlock(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeWait(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterWait(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeNotify(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterNotify(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeNotifyAll(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterNotifyAll(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeThreadStart(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterThreadStart(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeThreadJoin(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterThreadJoin(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeRead(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterRead(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myBeforeWrite(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myAfterWrite(Object o, int svno, int lineno, String clsname, int debug) {
    }

    @Override
    public void myExit() {
    }

    @Override
    public void myInit(int lockNum) {
    }
    
}
