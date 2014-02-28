package cn.edu.nju.swan;
import java.io.File;
import soot.RefType;
import soot.SootClass;
import soot.Type;


public class Util{

	static String[] unInstruClasses = {
		"jrockit.",
			"java.",
			"javax.",
			"xjava.",
			"COM.",
			"com.",
			"cryptix.",
			"sun.",
			"sunw.",
			"junit.",
			"org.junit.",
			"org.xmlpull.",
			"edu.hkust.leap.",
			"cn.edu.nju.swan."
	};
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
		tempdir = tempdir+"tmp"+System.getProperty("file.separator");
		
		if(Parameters.isOutputJimple)
			tempdir = tempdir+Parameters.OUTPUT_JIMPLE+System.getProperty("file.separator");
		
		if(Parameters.isRuntime)
			tempdir = tempdir+Parameters.PHASE_RECORD;
		else
			tempdir = tempdir+Parameters.PHASE_REPLAY;
		
		File tempFile = new File(tempdir);
		if(!(tempFile.exists()))
			tempFile.mkdir();
			
		tempdir = tempdir+System.getProperty("file.separator");
		return tempdir;
	}
	
    public static boolean isRunnableSubType(SootClass c) {
        if (c.implementsInterface("java.lang.Runnable"))
            return true;
        if (c.hasSuperclass())
            return isRunnableSubType(c.getSuperclass());
        return false;
    }
    public static boolean shouldInstruThisClass(String scname)
    {
		for(int k=0;k<unInstruClasses.length;k++)
		{
			if(scname.startsWith(unInstruClasses[k]))
			{
				return false;
			}
		}
		
		return true;
	}
    public static boolean shouldInstruThisMethod(String smname)
	{    	   	
		if (smname.contains("<clinit>")
			|| smname.contains("<init>"))
    	{
    		return false;
    	}
   		
		return true;
	}
    public static void resetParameters()
    {
  		 Parameters.isMethodRunnable = false;
  		 Parameters.isMethodMain = false;
  		 Parameters.isMethodSynchronized = false;
    }
	public static boolean instruThisType(Type type) 
	{
		if(type instanceof RefType)
		{
			if(Util.shouldInstruThisClass(type.toString()))
				//return true;
				return false;
		}
		return false;
	}
	public static String getAncestorClassName(SootClass sc1)
	{			
		SootClass sc2 = sc1.getSuperclass();
		while(shouldInstruThisClass(sc2.getName()))
		{
			sc1 = sc2;
			sc2 = sc1.getSuperclass();
		}
		return sc1.getName();
	}


	public static boolean isApplicationClass(SootClass declaringClass) 
	{
		String classname = declaringClass.getName();
		for(int k=0;k<unInstruClasses.length;k++)
		{
			if(classname.startsWith(unInstruClasses[k]))
			{
				return false;
			}
		}
		return true;
	}
}
