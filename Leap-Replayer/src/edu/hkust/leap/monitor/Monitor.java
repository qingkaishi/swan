package edu.hkust.leap.monitor;

//import edu.hkust.leap.random.Verifier;
import edu.hkust.leap.Parameters;
import edu.hkust.leap.replayer.ReplayControl;
import edu.hkust.leap.tracer.TraceReader;

public class Monitor {

	public synchronized static void crashed(Throwable crashedException) {
	
	}
	public static void accessSPE(int index,long id)
	{
//		String threadname = Thread.currentThread().getName();
//		long threadId = TraceReader.threadNameToIdMap.get(threadname);
//		
		//index = edu.hkust.leap.random.RandomAccess.getRandomSPEIndex(threadname);		
		//if(index==69)
		ReplayControl.check(index);
		
//		if(index==69)
//			System.out.println(true);
		
//		int type = edu.hkust.leap.random.RandomAccess.getRandomAccessType(threadname);
//		Verifier.check(index,type,threadId);
	}
	public synchronized static void startThreadBefore()
	{ 
	}
	public static void threadStartRun(long threadId)
	{
	}
	public synchronized static void threadExitRun(long threadId)
	{
		
	}
    public synchronized static void startRunThreadBefore(Thread t, long threadId)
    {	
    	
    }
    public synchronized static void joinRunThreadAfter(Thread t,long threadId)
    {
    	
    }
	public static void mainThreadStartRun(long threadId,String methodName, String[] args)
	{
	}

    public static void readBeforeInstance(Object o, int index, long tid) {
        accessSPE(index,tid);			
   }
    public static void writeBeforeInstance(Object o, int index, long tid)
    {
    	accessSPE(index,tid);
    }
    public static void readBeforeStatic(int index,long tid) {

    	accessSPE(index,tid);
    	
    }
    public static void writeBeforeStatic(int index,long tid) {

    	accessSPE(index,tid);
    }
    
    public static void waitAfter(Object o, int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void notifyBefore(Object o, int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void notifyAllBefore(Object o, int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void waitAfter(int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void notifyBefore(int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void notifyAllBefore(int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void enterMonitorAfter(Object o,int index, long tid)
    {
    	//accessSPE(index,tid);
    }
    public static void enterMonitorAfter(int index, long tid)
    {
    	//accessSPE(index,tid);
    }
    public static void enterMonitorBefore(int index, long tid)
    {
    	//accessSPE(index,tid);
    }
    public static void exitMonitorBefore(Object o,int index, long tid)
    {
    	//accessSPE(index,tid);
    }
}
