package edu.hkust.leap.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;
import java.util.HashMap;
import java.util.Vector;

import edu.hkust.leap.*;
import edu.hkust.leap.datastructure.*;
import edu.hkust.leap.generator.*;

public class Monitor {
	public static int VECARRAYSIZE = 100;
	public static boolean isCrashed = false;
	public static Throwable crashedException=null;
	public static HashMap<String,Long> threadNameToIdMap;		
	public static Vector<Long>[] accessVector;	
	public static void initialize(int size)
	{
		setVecArraySize(size);
		accessVector = new Vector[VECARRAYSIZE];
		for(int i=0;i<VECARRAYSIZE;i++)
		{
			//accessVector[i] = new MyAccessVector();
			accessVector[i] = new Vector<Long>();
		}
		threadNameToIdMap = new HashMap<String,Long>();
	}

	public static String methodname;
    public static String[] mainargs;

	private static String mainthreadname;
    
	public static void setVecArraySize(Integer size) {
		
		VECARRAYSIZE = size;
	}
    public static String saveMonitorData()
    {
    	String traceFile_=null;
		File traceFile_monitordata = null;
		File traceFile_threadNameToIdMap= null;
		File traceFile_nanoTimeDataVec = null;
		File traceFile_nanoTimeThreadVec = null;
		
		ObjectOutputStream fw_monitordata;
		ObjectOutputStream fw_threadNameToIdMap;
		ObjectOutputStream fw_nanoTimeDataVec;
		ObjectOutputStream fw_nanoTimeThreadVec;
		
		//SAVE Runtime Information
		try 
		{
			traceFile_monitordata = File.createTempFile("Leap", "_accessVector.trace.gz", new File(
					Util.getReplayTmpDirectory(methodname)));
			
			String traceFileName = traceFile_monitordata.getAbsolutePath();
			int index  =traceFileName.indexOf("_accessVector");
			traceFile_ = traceFileName.substring(0, index);
			
			traceFile_threadNameToIdMap = new File(traceFile_+"_threadNameToIdMap.trace.gz");
			traceFile_nanoTimeDataVec = new File(traceFile_+"_nanoTimeDataVec.trace.gz");
			traceFile_nanoTimeThreadVec = new File(traceFile_+"_nanoTimeThreadVec.trace.gz");
			
			
			assert (traceFile_monitordata != null && traceFile_threadNameToIdMap != null);

			fw_monitordata = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_monitordata)));
			fw_threadNameToIdMap = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_threadNameToIdMap)));
			fw_nanoTimeDataVec = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_nanoTimeDataVec)));
			fw_nanoTimeThreadVec = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(traceFile_nanoTimeThreadVec)));
			
			Util.storeObject(accessVector, fw_monitordata);
			Util.storeObject(threadNameToIdMap, fw_threadNameToIdMap);
			Util.storeObject(edu.hkust.leap.monitor.random.Monitor.getDataVec(), fw_nanoTimeDataVec);
			Util.storeObject(edu.hkust.leap.monitor.random.Monitor.getThreadVec(), fw_nanoTimeThreadVec);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return traceFile_;
    }
    public static void generateTestDriver(String traceFile_)
    {
		//GENERATE Test Driver
		try {
			CrashTestCaseGenerator.main(new String[] { traceFile_,
					Util.getTmpReplayDirectory() });
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
	public synchronized static void crashed(Throwable crashedException) 
	{
		
		isCrashed = true;
		
//		System.err.println("--- program crashed! ---");
//		System.err.println("--- preparing for reproducing the crash ... ");
//		String traceFile_ = saveMonitorData();
//		System.err.println("--- generating the test driver program ... ");
//		generateTestDriver(traceFile_);
//		
		System.exit(-1);
	}
	
	public synchronized static long getNanoTime()
	{
		long nt = System.nanoTime();
		System.out.println("Nano Time: "+nt);
		return nt;
	}
	
	public synchronized static void startThreadBefore()
	{ 
	}
	public static void threadStartRun(long threadId)
	{
		threadNameToIdMap.put(Thread.currentThread().getName(),threadId);
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
		mainthreadname = Thread.currentThread().getName();
		
		threadNameToIdMap.put(Parameters.MAIN_THREAD_NAME,threadId);//Thread.currentThread().getName()
		mainargs = args;
		methodname = methodName;
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
    //synchronized
	public static void accessSPE(int index,long threadId) {
		//GO TO THE RANDOM BUG INJECTION LIBRARY FIRST
//		String threadname = Thread.currentThread().getName();
//		if(mainthreadname.equals(threadname))
//			threadname = Parameters.MAIN_THREAD_NAME;
//		
//		index = edu.hkust.leap.random.RandomAccess.getRandomSPEIndex(threadname);
		index=0;
		accessVector[index].add(threadId);
		
//		int type = edu.hkust.leap.random.RandomAccess.getRandomAccessType(threadname);
//		Verifier.check(index,type,threadId);
	}
    public static void waitBefore(Object o, int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void waitBefore(int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void waitAfter(Object o, int index, long tid)
    {	
    	//accessSPE(index,tid);
    }
    public static void waitAfter(int index, long tid)
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
    public static void enterMonitorBefore(Object o,int index, long tid)
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
    public static void exitMonitorBefore(int index, long tid)
    {
    	//accessSPE(index,tid);
    }
}
