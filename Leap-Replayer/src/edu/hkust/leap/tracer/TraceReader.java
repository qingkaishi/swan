package edu.hkust.leap.tracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


import edu.hkust.leap.*;
import edu.hkust.leap.datastructure.MyAccessVector;


public class TraceReader {

	//public static  HashMap<String,Vector<Long>> objectMap = null;
	public static Vector<Long>[] accessVector = null;
	public static HashMap<String,Long> threadNameToIdMap = new HashMap<String,Long>();
	public static Vector<Long> nanoTimeDataVec = null;
	public static Vector<Long> nanoTimeThreadVec = null;
	
	static Date traceFileDate;



	/**
	 * Read traceItem from serialized information in file
	 * 
	 */
	public static void readTrace(int type, String traceFileName) throws Exception{

		ObjectInputStream in = null;

		try {
			File traceFile = new File(traceFileName);
			

			if (traceFileName.endsWith(".gz")) {
				in = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(traceFile)));
			} else {
				in = new ObjectInputStream(new FileInputStream(traceFileName));
			}
		
			switch (type)
			{
				case 1:
					accessVector = (Vector<Long>[]) Util.loadObject(in);
					//printAccessVector();
					break;
				case 2:
					threadNameToIdMap = (HashMap<String,Long>) Util.loadObject(in);
					break;
				case 3:
					nanoTimeDataVec = (Vector<Long>)Util.loadObject(in);
					edu.hkust.leap.monitor.random.Monitor.setDataVec(nanoTimeDataVec);
					break;
				case 4:
					nanoTimeThreadVec = (Vector<Long>) Util.loadObject(in);
					edu.hkust.leap.monitor.random.Monitor.setThreadVec(nanoTimeThreadVec);
					break;					
				default:
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Date getTraceFileDate() {
		return traceFileDate;
	}
}
