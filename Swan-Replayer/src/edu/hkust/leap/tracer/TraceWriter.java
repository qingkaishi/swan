package edu.hkust.leap.tracer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import cn.edu.nju.swan.model.SwanEvent;

public class TraceWriter {
	public static Vector<SwanEvent> swanTrace = new Vector<SwanEvent>();
	
	public static void writeTrace(Object o, String where) throws Exception {
		ObjectOutputStream swanRecordOutputStream = new ObjectOutputStream(
				new GZIPOutputStream(new FileOutputStream(where)));
		storeObject(o, swanRecordOutputStream);
	}

	private static void storeObject(Object o, ObjectOutputStream out) {
		try {
			out.writeObject(o);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
