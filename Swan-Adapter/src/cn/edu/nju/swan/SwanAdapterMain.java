package cn.edu.nju.swan;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import cn.edu.nju.swan.model.PRP;
import cn.edu.nju.swan.model.RacePair;
import cn.edu.nju.swan.model.SwanEvent;
import cn.edu.nju.swan.model.SwanThread;

public class SwanAdapterMain {

	public static Vector<SwanEvent> swanTrace;
	public static Vector<SwanThread> swanThreads = new Vector<SwanThread>();

	public static Map<Long, SwanThread> map = new HashMap<Long, SwanThread>();

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
//		args = new String[] { "swantrace.gz" };

		File traceFile = new File(args[0]);

		ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(traceFile)));
		swanTrace = (Vector<SwanEvent>) loadObject(in);

		Map<Long, Boolean> startLocksMap = new HashMap<Long, Boolean>();
		for (SwanEvent se : swanTrace) {
			SwanThread st = map.get(se.ti);
			if (st == null) {
				st = new SwanThread(se.ti, se);
				se.mythread = st;
				map.put(se.ti, st);
			} else {
				se.mythread = st;
				st.add(se);
			}

			if (se.ai == SwanEvent.ACQUIRE) {
				startLocksMap.put(se.ti, true);
				se.setlocked(true);
			}

			if (startLocksMap.get(se.ti) != null && startLocksMap.get(se.ti)) {
				se.setlocked(true);
			}

			if (se.ai == SwanEvent.RELEASE) {
				startLocksMap.put(se.ti, false);
				se.setlocked(true);
			}
		}
		swanThreads.addAll(map.values());

//		for (SwanEvent se : swanTrace) {
//			System.err.println(se);
//		}

		MinimalLegalGraph mlg = new MinimalLegalGraph(swanTrace);

		List<RacePair> rps = new ArrayList<RacePair>();
		for (int i = 0; i < swanTrace.size(); i++) {
			SwanEvent ei = swanTrace.get(i);
			for (int j = i + 1; j < swanTrace.size(); j++) {
				SwanEvent ej = swanTrace.get(j);
				if (ei.ti == ej.ti || ei.mi != ej.mi || ei.ai > 1 || ej.ai > 1
						|| (ei.ai == SwanEvent.READ && ej.ai == SwanEvent.READ)) {
					continue;
				} else {
					rps.add(new RacePair(ei, ej));
				}
			}
		}

		int size = 0;
		List<PRP> prps = new ArrayList<PRP>();
		for (int i = 0; i < rps.size(); i++) {
			RacePair rpi = rps.get(i);
			for (int j = i + 1; j < rps.size(); j++) {
				RacePair rpj = rps.get(j);
				if (rpi.canWith(rpj)) {
					size++;

					boolean found = false;
					PRP nprp = new PRP(rpi, rpj);
					for (int k = 0; k < prps.size(); k++) {
						PRP prpk = prps.get(k);
						if (prpk.implies(nprp)) {
							found = true;
							break;
						} else if (nprp.implies(prpk)) {
							found = true;
							prps.set(k, nprp);
							break;
						}
					}

					if (!found) {
						prps.add(nprp);
					}
				}
			}
		}
		System.err.println("Before optimization, " + size + " prps to cover!");
		System.err.println("After optimization, " + prps.size()
				+ " prps to cover!");
		construct(mlg, prps, rps);
	}

	private static void construct(MinimalLegalGraph mlg, List<PRP> prps,
			List<RacePair> rps) {
		do {
			MinimalLegalGraph g = (MinimalLegalGraph) mlg.clone();
			for (int i = 0; i < prps.size();) {
				PRP p = prps.get(i);

				RacePair p1 = p.r1.closure();
				RacePair p2 = p.r2.closure();

				PRP closure = new PRP(p1, p2);

				if (!g.containsSCCWith(closure)) {
					if (!removeRed(closure, prps)) {
						throw new RuntimeException("Error when removing hbr!");
					} else {
						System.err.println("Remain: " + prps.size());
					}
				} else {
					i++;
				}
			}

			// ADD others
			for (RacePair rp : rps) {
				PRP prp = new PRP(rp.closure(), rp.closure());
				g.containsSCCWith(prp);
			}

			// output
			g.output();
		} while (prps.size() != 0);
	}

	private static boolean removeRed(PRP closure, List<PRP> prps) {
		int size = prps.size();
		for (int i = 0; i < prps.size(); i++) {
			PRP shbr = prps.get(i);

			if (closure.implies(shbr)) {
				prps.remove(i--);
			}
		}
		return size != prps.size();
	}

	private static Object loadObject(ObjectInputStream in) {
		Object o = null;
		try {
			o = in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}
}
