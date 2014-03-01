package edu.hkust.leap;

public class Parameters 
{
	public static int LOOP_STMT_COUNT = 0;
	public static final String CRASH_ANNOTATION="leap_Crashed_with";
	public static final String CATCH_EXCEPTION_SIG = "<edu.hkust.leap.monitor.Monitor: void crashed(java.lang.Throwable)>";
	

	public static String OUTPUT_JIMPLE ="jimple";
	public static String PHASE_RECORD ="runtime";
	public static String PHASE_REPLAY ="replay";
	public static boolean shouldInstru = false;
	public static boolean removeSync = true;
	
	public static boolean isMethodPublic=false;
	public static boolean isMethodStatic = false;
	public static boolean isMethodRunnable = false;
	public static boolean isMethodSynchronized = false;
	public static boolean isMethodMain = false;
	
	public static boolean isRuntime=true;
	public static boolean isReplay=false;
	public static boolean isOutputJimple=false;
	
	public static boolean isInnerClass = false;
	public static boolean isAnonymous = false;
	public static boolean isStmtInLoop = false;
		
	public static int lockCount=0;
}
