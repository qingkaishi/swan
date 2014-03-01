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
	public static String getTmpReplayDirectory() 
	{
		String tempdir = System.getProperty("user.dir");
		tempdir=tempdir.replace("recorder", "replayer");
		
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir+Parameters.SRC_DIR+System.getProperty("file.separator");
		
		File tempFile = new File(tempdir);
		if(!(tempFile.exists()))
			tempFile.mkdir();
			
		tempdir = tempdir+System.getProperty("file.separator");
		return tempdir;
	}
	public static String getReplayTmpDirectory(String appname) 
	{
		String tempdir = System.getProperty("user.dir")+ System.getProperty("file.separator")+"tmp";
		tempdir=tempdir.replace("recorder", "replayer");
		
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		
		//tempdir = tempdir + "data" + System.getProperty("file.separator")+appname+System.getProperty("file.separator");
		
		File tempFile = new File(tempdir);

		if(!tempFile.exists())
		{
			tempFile.mkdir();
		}
		else
		{			
			//deleteFile(tempFile);
		}
			
		return tempdir;
	}
	public static String getTmpTransDirectory() 
	{
		String tempdir = System.getProperty("user.dir");
		tempdir=tempdir.replace("recorder","transformer");
		
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		tempdir = tempdir+Parameters.TMP_DIR+System.getProperty("file.separator");
		return tempdir;
	}
	private static void deleteFile(File f)
	{
		// Get all files in directory
		File[] files = f.listFiles();
		for (File file : files)
		{
		   // Delete each file

		   if (!file.delete())
		   {
		       // Failed to delete file
		       System.out.println("Failed to delete "+file);
		   }
		}
	}
    public static boolean shouldInstruThisClass(String scname)
    {
		
		if(scname.startsWith("java.")
			||scname.startsWith("javax.")	     
			||scname.startsWith("com.sun.")
			||scname.startsWith("java.awt.event.NativeLibLoader.")
			||scname.startsWith("sun.")
			||scname.startsWith("junit.")
			||scname.startsWith("org.junit.")
			||scname.startsWith("org.xmlpull.")
			||scname.startsWith("edu.")
			||scname.startsWith("javato."))
		{
			return false;
		}
		
		if (scname.contains("InputStream") 
			|| scname.contains("OutputStream")
			//|| scname.contains("Exception")
			|| scname.contains("store.raw.data.BasePage") 
			|| scname.contains("junit.")
			|| scname.contains("org.junit.")
			|| scname.contains("checkers.")
			|| scname.contains("org.xmlpull."))
		{
			return false;
		}
   		
		return true;
	}
    public static boolean shouldInstruThisMethod(String smname)
	{    	
    	if(smname.startsWith("java.")
			||smname.startsWith("javax.")	     
			||smname.startsWith("com.sun.")
			||smname.startsWith("java.awt.event.NativeLibLoader.")
			||smname.startsWith("sun.")
			||smname.startsWith("junit.")
			||smname.startsWith("org.junit.")
			||smname.startsWith("org.xmlpull.")
			||smname.startsWith("edu.")
			||smname.startsWith("javato."))
    	{
    		return false;
    	}
    	
		if (smname.contains("InputStream") 
			|| smname.contains("OutputStream")
			//|| scname.contains("Exception")
			|| smname.contains("store.raw.data.BasePage") 
			|| smname.contains("junit.")
			|| smname.contains("org.junit.")
			|| smname.contains("checkers.")
			|| smname.contains("org.xmlpull."))
		{
			return false;
		}
		
		if (smname.contains("readExternal")
			|| smname.contains("writeExternal")
			|| smname.contains("hashCode")
			|| smname.contains("equals")
			|| smname.contains("<clinit>")
			|| smname.contains("<init>"))
    	{
    		return false;
    	}
   		
		return true;
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
