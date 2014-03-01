package edu.hkust.leap;

import edu.hkust.leap.monitor.Monitor;

public class MonitorThread extends Thread
{
	
	MonitorThread()
	{
		super("MonitorThread");
	}
	public void run()
	{
		if(Monitor.isCrashed)
		{
			System.err.println("--- program crashed! ---");
			System.err.println("--- preparing for reproducing the crash ... ");
			String traceFile_ = Monitor.saveMonitorData();
			System.err.println("--- generating the test driver program ... ");
			Monitor.generateTestDriver(traceFile_);
		}
		else
		{
			Monitor.generateTestDriver(Monitor.saveMonitorData());
		}
	}
}	
