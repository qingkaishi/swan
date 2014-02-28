package cn.edu.nju.swan.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import cn.edu.nju.swan.cfg.ControlFlowGraph;
import cn.edu.nju.swan.cfg.MD5;
import cn.edu.nju.swan.cfg.UnitNode;
import cn.edu.nju.swan.model.SwanEvent;
import edu.hkust.leap.tracer.TraceReader;

public class SwanControl {

	public static Vector<SwanEvent> trace;
	public static final Object G = new Object();

	public static void control(long curTid, String stmtSig) {
		if (trace.isEmpty())
			return;

		SwanEvent e = trace.firstElement();

		int counter = 0;
		while (curTid != e.ti) {
			int originalRecordSize = trace.size();

			synchronized (G) {
				try {
					G.wait(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			// after wait if trace is empty, return
			if (trace.isEmpty()) {
				return;
			}

			// a naive implementation, after waiting, if the record size is not
			// changed, a block is detected;
			// then remove the first element. FIXME
			if (originalRecordSize == trace.size()) {
				if (counter < 5) {
					counter++;
				} else {
					trace.remove(0);
				}
			}

			if (trace.isEmpty()) {
				return;
			}

			e = trace.get(0);
		}

		// if they belong to the same thread...
		if (stmtSig.equals(e.si)) {
			// if the two statements are the same.
			if (trace.isEmpty()) {
				return;
			}

			trace.remove(0);
		} else {
			// same thread but not the same statement

			// the head stmt in trace can be reached from curr?
			boolean currCanReachHeadStmt = true;

			// sig = method;stmt
			String[] currStmtSigSplit = stmtSig.split(";");
			String[] headStmtSplit = e.si.split(";");

			// unit node in cfg structure
			UnitNode toExe = null;
			UnitNode sdExe = null;

			if (currStmtSigSplit[0].equals(headStmtSplit[0])) {
				// in the same thread and the same method
				ControlFlowGraph cfg = tidcfgMap.get(curTid).peek();
				toExe = cfg.getUnitNodeBySignature(currStmtSigSplit[1]);
				sdExe = cfg.getUnitNodeBySignature(headStmtSplit[1]);
				currCanReachHeadStmt = toExe.canReach(sdExe);
			} else {
				// // if in different method, here is a naive implementation
				// TODO
			}

			if (!currCanReachHeadStmt) {
				// if sdExe is not reachable, execute toExe and do remove
				// sdExe from record.
				if (trace.isEmpty()) {
					return;
				}

				trace.remove(0);

				// check whether toExe is in the record FIXME
				int i = 0;
				boolean findIt = false;
				for (; i < trace.size(); i++) {
					if (stmtSig.equals(trace.get(i))) {
						findIt = true;
						break;
					}
				}

				if (findIt) {
					while (i >= 0) {
						if (trace.isEmpty()) {
							break;
						}

						trace.remove(0);
						i--;
					}
				}
			}
		}
	}

	private final static Map<Long, Stack<ControlFlowGraph>> tidcfgMap = new HashMap<Long, Stack<ControlFlowGraph>>();
	private final static Map<String, ControlFlowGraph> namecfgMap = new HashMap<String, ControlFlowGraph>();

	public static void startMethod(String cn, String mn) {
		ControlFlowGraph cfg = namecfgMap.get(mn);
		if (cfg == null) {
			cfg = (ControlFlowGraph) TraceReader.readTrace("./cfgs/"
					+ MD5.encode(cn) + "/" + MD5.encode(mn) + ".gz");
			namecfgMap.put(mn, cfg);
		}

		long currentThreadId = Thread.currentThread().getId();
		Stack<ControlFlowGraph> cfgStack = tidcfgMap.get(currentThreadId);
		if (cfgStack == null) {
			cfgStack = new Stack<ControlFlowGraph>();
			tidcfgMap.put(currentThreadId, cfgStack);
		}
		cfgStack.push(cfg);
	}

	public static void endMethodBeforeThrow(String cn, String mn) {
		long currentThreadId = Thread.currentThread().getId();
		tidcfgMap.get(currentThreadId).pop();
	}

	public static void endMethodBeforeReturn(String cn, String mn) {
		long currentThreadId = Thread.currentThread().getId();
		tidcfgMap.get(currentThreadId).pop();
	}

}
