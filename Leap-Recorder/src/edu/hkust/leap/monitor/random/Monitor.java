package edu.hkust.leap.monitor.random;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class Monitor 
{
	private static Vector<Long> nanoTimeDataVec;
	private static Vector<Long> nanoTimeThreadVec;
	
	public synchronized static long getNanoTime()
	{
		long nt = System.nanoTime();
		nanoTimeDataVec.add(nt);
		nanoTimeThreadVec.add(Thread.currentThread().getId());
		
		System.out.println("Nano Time: "+nt);
		return nt;
	}
	
	public static void initialize()
	{		
		nanoTimeDataVec = new Vector<Long>();
		nanoTimeThreadVec = new Vector<Long>();
	}
	public static void setDataVec(Vector<Long> dataVec)
	{
		nanoTimeDataVec = dataVec;
	}
	public static void setThreadVec(Vector<Long> threadVec)
	{
		nanoTimeThreadVec = threadVec;
	}
	public static Vector<Long> getDataVec()
	{
		return nanoTimeDataVec;
	}
	public static Vector<Long> getThreadVec()
	{
		return nanoTimeThreadVec;
	}
	
}
