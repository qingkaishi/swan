package cn.edu.nju.swan.monitor;

//import edu.hkust.leap.random.Verifier;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.swan.SwanReplayerMain;
import cn.edu.nju.swan.model.SwanEvent;
import edu.hkust.leap.replayer.ReplayControl;
import edu.hkust.leap.tracer.TraceWriter;

public class Monitor {

	public synchronized static void crashed(Throwable crashedException) {

	}

	public static void accessSPE(int index, long id, String stmtSig, int ln) {
//		if(SwanReplayerMain.lines.contains(ln)){
//			return;
//		}
		
		// String threadname = Thread.currentThread().getName();
		// long threadId = TraceReader.threadNameToIdMap.get(threadname);
		//
		// index =
		// edu.hkust.leap.random.RandomAccess.getRandomSPEIndex(threadname);
		// if(index==69)
		if (SwanReplayerMain.phase == 1) {
			ReplayControl.check(index);
		} else {
			SwanControl.control(id, stmtSig);
		}

		// if(index==69)
		// System.out.println(true);

		// int type =
		// edu.hkust.leap.random.RandomAccess.getRandomAccessType(threadname);
		// Verifier.check(index,type,threadId);
	}

	public synchronized static void startThreadBefore() {
	}

	public static void threadStartRun(long threadId) {
	}

	public synchronized static void threadExitRun(long threadId) {

	}

	public synchronized static void startRunThreadBefore(Thread t, long threadId) {

	}

	public synchronized static void joinRunThreadAfter(Thread t, long threadId) {

	}

	public static void mainThreadStartRun(long threadId, String methodName,
			String[] args) {
	}

	public static void readBeforeInstance(Object o, int index, long tid,
			String ssig, int ln) {
		accessSPE(index, tid, ssig, ln);
		if (SwanReplayerMain.phase == 1) {
			SwanEvent se = new SwanEvent(tid, index, SwanEvent.READ, ssig);
			TraceWriter.swanTrace.add(se);
		}
	}

	public static void writeBeforeInstance(Object o, int index, long tid,
			String ssig, int ln) {
		accessSPE(index, tid, ssig, ln);
		if (SwanReplayerMain.phase == 1) {
			SwanEvent se = new SwanEvent(tid, index, SwanEvent.WRITE, ssig);
			TraceWriter.swanTrace.add(se);
		}
	}

	public static void readBeforeStatic(int index, long tid, String ssig, int ln) {
		accessSPE(index, tid, ssig,ln);
		if (SwanReplayerMain.phase == 1) {
			SwanEvent se = new SwanEvent(tid, index, SwanEvent.READ, ssig);
			TraceWriter.swanTrace.add(se);
		}
	}

	public static void writeBeforeStatic(int index, long tid, String ssig, int ln) {
		accessSPE(index, tid, ssig,ln);
		if (SwanReplayerMain.phase == 1) {
			SwanEvent se = new SwanEvent(tid, index, SwanEvent.WRITE, ssig);
			TraceWriter.swanTrace.add(se);
		}
	}

	public static void waitAfter(Object o, int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void notifyBefore(Object o, int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void notifyAllBefore(Object o, int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void waitAfter(int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void notifyBefore(int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void notifyAllBefore(int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void enterMonitorAfter(Object o, int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void enterMonitorAfter(int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void enterMonitorBefore(int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void exitMonitorBefore(Object o, int index, long tid) {
		// accessSPE(index,tid);
	}

	public static void startMethod(String cn, String mn) {
		if (SwanReplayerMain.phase != 1) {
			SwanControl.startMethod(cn, mn);
		}
	}

	public static void endMethodBeforeThrow(String cn, String mn) {
		if (SwanReplayerMain.phase != 1) {
			SwanControl.endMethodBeforeThrow(cn, mn);
		}
	}

	public static void endMethodBeforeReturn(String cn, String mn) {
		if (SwanReplayerMain.phase != 1) {
			SwanControl.endMethodBeforeReturn(cn, mn);
		}
	}

	static List<Object> locks = new ArrayList<Object>();

	public static void exitMonitorBefore(int iid, long id, int ln, Object lk,
			String stmtSig) {
//		System.err.print("exit:"+ln);
		if (SwanReplayerMain.phase == 1 && SwanReplayerMain.lines.contains(ln)) {
			int lockIndex = -1;
			if (locks.contains(lk)) {
				lockIndex = locks.indexOf(lk);
			} else {
				locks.add(lk);
				lockIndex = locks.indexOf(lk);
			}

			SwanEvent se = new SwanEvent(Thread.currentThread().getId(),
					lockIndex, SwanEvent.RELEASE, stmtSig);
			TraceWriter.swanTrace.add(se);
		} else {
			SwanControl.control(id, stmtSig);
		}
	}

	public static void enterMonitorBefore(int iid, long id, int ln, Object lk,
			String stmtSig) {
//		System.err.print("enter: "+ln);
		if (SwanReplayerMain.phase == 1 && SwanReplayerMain.lines.contains(ln)) {
			int lockIndex = -1;
			if (locks.contains(lk)) {
				lockIndex = locks.indexOf(lk);
			} else {
				locks.add(lk);
				lockIndex = locks.indexOf(lk);
			}

			SwanEvent se = new SwanEvent(Thread.currentThread().getId(),
					lockIndex, SwanEvent.ACQUIRE, stmtSig);
			TraceWriter.swanTrace.add(se);
		} else {
			SwanControl.control(id, stmtSig);
		}
	}

}
