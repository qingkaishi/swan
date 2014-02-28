package edu.hkust.leap.monitor.random;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import edu.hkust.leap.replayer.ReplayControl;

public class Monitor 
{
	private static Vector<Long> nanoTimeDataVec;
	private static Vector<Long> nanoTimeThreadVec;
	
	public synchronized static long getNanoTime()
	{
		//The problem is: "java.lang.NoClassDefFoundError: edu/hkust/leap/replayer/ReplayControl"
		
		//long nt = ReplayControl.getRandomSeed();
			
		long nt = nanoTimeDataVec.remove(0);
		
		System.out.println("Nano Time: "+nt);
		return nt;
	}
	public static void setDataVec(Vector<Long> dataVec)
	{
		nanoTimeDataVec = dataVec;
	}
	public static void setThreadVec(Vector<Long> threadVec)
	{
		nanoTimeThreadVec = threadVec;
	}
}
