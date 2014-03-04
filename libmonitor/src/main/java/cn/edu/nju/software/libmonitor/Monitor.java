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
public class Monitor {
    public static void myBeforeLock(Object o, int svno, int lineno, int debug){}
    public static void myAfterLock(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeUnlock(Object o, int svno, int lineno, int debug){}
    public static void myAfterUnlock(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeWait(Object o, int svno, int lineno, int debug){}
    public static void myAfterWait(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeNotify(Object o, int svno, int lineno, int debug){}
    public static void myAfterNotify(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeNotifyAll(Object o, int svno, int lineno, int debug){}
    public static void myAfterNotifyAll(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeThreadStart(Object o, int svno, int lineno, int debug){}
    public static void myAfterThreadStart(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeThreadJoin(Object o, int svno, int lineno, int debug){}
    public static void myAfterThreadJoin(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeSynchronizedInsInvoke(Object o, int svno, int lineno, int debug){}
    public static void myAfterSynchronizedInsInvoke(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug){}
    public static void myAfterSynchronizedStaticInvoke(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeRead(Object o, int svno, int lineno, int debug){}
    public static void myAfterRead(Object o, int svno, int lineno, int debug){}
    
    public static void myBeforeWrite(Object o, int svno, int lineno, int debug){}
    public static void myAfterWrite(Object o, int svno, int lineno, int debug){}
    
    public static void myInit(){}
    public static void myExit(){}
    
    public static void myCleanup(){}
}
