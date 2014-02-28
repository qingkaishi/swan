package cn.edu.nju.swan.monitor;

public class Monitor {

	public static final int READ = 0;
	public static final int WRITE = 1;
	public static final int ACQUIRE = 2;
	public static final int RELEASE = 3;
	public static final int FORK = 4;
	public static final int JOIN = 5;

	public synchronized static void crashed(Throwable crashedException) {
	}

	public static void threadStartRun(long threadId) {
	}

	public static void threadExitRun(long threadId) {
	}

	public static void mainThreadStartRun(long threadId, String methodName,
			String[] args) {
	}

	public static void accessSPE(int iid, long id) {
	}

	public static boolean youMayCrash(String methodName, Object objects[],
			String types[], long threadId) {
		return true;
	}

	public static void youAreOK() {
	}

	public static void waitBefore(int iid, long id) {
	}

	public static void waitAfter(int iid, long id) {
	}

	public static void waitBefore(Object o, int iid, long id) {
	}

	public static void waitAfter(Object o, int iid, long id) {
	}

	public static void notifyBefore(Object o, int iid, long id) {
	}

	public static void notifyAllBefore(Object o, int iid, long id) {
	}

	public static void notifyBefore(int iid, long id) {
	}

	public static void notifyAllBefore(int iid, long id) {
	}

	public static void readBefore(int iid, long id) {
	}

	public static void writeBefore(int iid, long id) {
	}

	public static void readBeforeInstance(Object o, int iid, long id, String ssig,int ln) {
	}

	public static void writeBeforeInstance(Object o, int iid, long id, String ssig,int ln) {
	}

	public static void readBeforeStatic(int iid, long id, String ssig,int ln) {
	}

	public static void writeBeforeStatic(int iid, long id, String ssig,int ln) {
	}

	public static void enterMonitorAfter(int iid, long id) {
	}

	public static void exitMonitorBefore(int iid, long id, int ln, Object lk, String stmtSig) {
	}

	public static void startMethod(String cn, String mn) {
	}

	public static void endMethodBeforeThrow(String cn, String mn) {
	}

	public static void endMethodBeforeReturn(String cn, String mn) {
	}

	public static void enterMonitorAfter(Object o, int iid, long id) {
	}

	public static void exitMonitorBefore(Object o, int iid, long id) {
	}

	public static void enterMonitorBefore(int iid, long id, int ln, Object lk, String stmtSig) {
	}

	public static void exitMonitorAfter(int iid, long id) {
	}

	public static void enterMonitorBefore(Object o, int iid, long id) {
	}

	public static void exitMonitorAfter(Object o, int iid, long id) {
	}

	public static void startRunThreadBefore(Thread t, long id) {
	}

	public static void joinRunThreadAfter(Thread t, long id) {
	}
}
