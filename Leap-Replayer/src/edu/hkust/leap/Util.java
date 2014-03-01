package edu.hkust.leap;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Util implements Cloneable{

	public static String getShortTypeName(String argumentTypeName) {
		String ret = argumentTypeName.replace('$', '.');
		if (ret.startsWith("java.lang.")) {
			ret = ret.substring("java.lang.".length());
		}
		return ret;
	}
	public static String makeArgumentName(int argOrder, int k) {
		if (argOrder == 0) {
			return "this";
		}

		return "arg"+k+"_" + argOrder;
	}
	public static String makeArgumentName(int argOrder) {
		if (argOrder == 0) {
			return "this";
		}

		return "arg_" + argOrder;
	}


	public static String transClassNameDotToSlash(String name) {
		return name.replace('.', '/');
	}

	public static String transClassNameSlashToDot(String name) {
		return name.replace('/', '.');
	}
	public static String getTmpRecordDirectory() 
	{
		String tempdir = System.getProperty("user.dir");
		tempdir=tempdir.replace("replayer","monitor");
		
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir+Parameters.TMP_DIR+System.getProperty("file.separator");
		return tempdir;
	}
	public static String getTmpDirectory() 
	{
		String tempdir = System.getProperty("user.dir");
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		
		File tempFile = new File(tempdir);
		if(!(tempFile.exists()))
			tempFile.mkdir();
			
		tempdir = tempdir+System.getProperty("file.separator");
		return tempdir;
	}
	
    public static void storeObject(Object o, ObjectOutputStream out)
    {
    	try
    	{
    		out.writeObject(o);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public static Object loadObject(ObjectInputStream in)
    {
    	Object o =null;
    	try
    	{
    		o = in.readObject();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return o;
    	}
    }
}
