package edu.hkust.leap.replayer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import edu.hkust.leap.tracer.TraceReader;

public class Scheduler extends Thread {
    public static final long stallCheckerInterval = Long.getLong("recrash.activeChecker.stallCheckerInterval", 1);
    public static HashSet<Long> activeThreadIds = new HashSet<Long>();
    private static int counter = 0;
    private static boolean flag = true;
    public static int nums_ = 0;
    public static int NUM = 10;
    public Scheduler() {
        super("leap-scheduler");
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
    }
    public static synchronized void clearCounter()
    {
    	counter=0;
    }
    private synchronized static void addCounter()
    {
    	counter++;
    }
    
    private static void checkCounter()
    {
    	if(flag)
    	{
	    	addCounter();
	    	if(counter>100)
	    	{
	    		 //clearAccessVector();
	    		 flag = false;
	    		 //System.err.println("--- Replay failed! ---");
	    	}
    	}
    }
    
    private static void clearAccessVector()
    {
		TraceReader.accessVector = null;
    }
    public void run() {
        Object lock = new Object();
        synchronized (lock) {
            try {
                while (true) {
                    lock.wait(stallCheckerInterval);
                    breakAnyStall();
                }
            } catch (InterruptedException e) {
            }
        }

    }


    public static void breakAnyStall() {
        Thread[] tList = new Thread[Thread.activeCount()];
        HashSet<String> threadNames = new HashSet<String>();
        int numThreads = Thread.enumerate(tList);
        int count = 0;
        int activeCount = 0;

        //System.out.println("----------------------------------------------------------");
        for (int i = 0; i < numThreads; i++) {
        	
//            System.out.println("Thread " + tList[i] + " in state " + tList[i].getState()
//                    + " isDaemon " + tList[i].isDaemon() + " priority "+tList[i].getPriority());
            if (!tList[i].getName().equals("leap-scheduler")
                    && !tList[i].getName().equals("Keep-Alive-Timer")
                    && !tList[i].getName().equals("DestroyJavaVM")) {

            	threadNames.add(tList[i].getName());
            	
                if (tList[i] != Thread.currentThread()
                		&& activeThreadIds.contains(tList[i].getId())
                        && (tList[i].getState() == Thread.State.RUNNABLE
                        || tList[i].getState() == Thread.State.TIMED_WAITING
                        || tList[i].getState() == Thread.State.NEW)) {
                    count++;
                }
                activeCount++;
            }
        }
        //System.out.println("Active Count: "+activeCount+" --- count: "+count);
        if (activeCount == 0) {
            System.exit(0);
        }
        if (count == 0) 
        {
            if (!ActiveChecker.unblockAThread()) {
                //System.err.println("System stall identified by "+Thread.currentThread());
                //printThreadState();
                //System.exit(1);
            }
            checkCounter();
        }
    }
    
    public static int getActiveThreadCount() {
        Thread[] tList = new Thread[Thread.activeCount()];
        int numThreads = Thread.enumerate(tList);
        int count = 0;

        for (int i = 0; i < numThreads; i++) {
            if (!tList[i].getName().equals("leap-scheduler")
                    && !tList[i].getName().equals("Keep-Alive-Timer")
                    && !tList[i].getName().equals("DestroyJavaVM")) {
                count++;
            }
        }
        return count;
    }

    public static void printThreadState() {
        Thread[] tList = new Thread[Thread.activeCount()];
        int numThreads = Thread.enumerate(tList);

        System.out.println("Printing Thread State: ");
        for (int i = 0; i < numThreads; i++) {
            System.out.println("Thread " + tList[i] + " in state " + tList[i].getState() + " isDaemon " + tList[i].isDaemon());
            StackTraceElement[] trace = tList[i].getStackTrace();
            for (StackTraceElement elem : trace) {
                System.out.println("    " + elem);
            }
        }

    }

}
