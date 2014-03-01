package edu.hkust.leap.datastructure;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLongArray;

import edu.hkust.leap.monitor.Monitor;

public class MyAccessVector extends Vector {
	static int CAPACITY = 100;
	long[] elementData;
	int[] counter;
	int pos;
	int ID;
	private ObjectOutputStream out;
	int finalRuntimePos;
	boolean isFirstAccessOnReplay = false;
	
	public MyAccessVector()
	{reset();}
	public MyAccessVector(int ID)
	{
		this.ID = ID;
		reset();
	}
	public static void setCapa(Integer size)
	{
		CAPACITY = size;
	}

	public long getCurrent()
	{
		return elementData[pos];
	}
	public synchronized void add(long id)
	{
		if(pos + 2 > CAPACITY) 
		{
			saveToMultiMap();
			elementData = new long[CAPACITY];
    		pos=0;
		}
		elementData[++pos] = id;	
	}
	//synchronized
	//@Atomic
	public synchronized void add_cmp(long id)
	{
		if(elementData[pos]==id)//we don't use the data at pos zero
			counter[pos]++;
		else
		{
			if (pos + 2 > CAPACITY) 
			{
				saveToMultiMap();
				pos=0;
				    elementData = new long[CAPACITY];
				    counter = new int[CAPACITY];
			}			
			elementData[++pos] = id;
		}		
	}
	private void reset()
	{
		pos=0;
		elementData = new long[CAPACITY];
		counter = new int[CAPACITY];
	}

	private void saveToMultiMap() {
		//Monitor.multimap.put(ID,elementData);
	}
}
