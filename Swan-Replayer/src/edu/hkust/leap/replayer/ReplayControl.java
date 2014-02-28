package edu.hkust.leap.replayer;

import java.util.Vector;

import edu.hkust.leap.tracer.TraceReader;

public class ReplayControl {

	public static void check(int index) {
//		System.err.println(Thread.currentThread().getId() + ": "
//				+ TraceReader.accessVector[index] + "*" + Thread.activeCount());

		if (TraceReader.accessVector != null) {
			String threadName = Thread.currentThread().getName();
			long threadId = TraceReader.threadNameToIdMap.get(threadName);

			Vector<Long> v = TraceReader.accessVector[index];

//			synchronized (ActiveChecker.lock) {
				if (v.isEmpty())
					return;
//
//				if (threadId != v.get(0)) {
//					(new ActiveChecker(index)).check();
//				}
//
//			}
			while (threadId != v.get(0)) {
				ActiveChecker.blockIfRequired();
//				System.out.println("[BLOCK]"+Thread.currentThread().getId()+": "+v +"*"+Thread.activeCount());
			}

			v.remove(0);
//			System.out.println(Thread.currentThread().getId()+": "+v);
		} else {

		}

	}

	public static long getRandomSeed() {
		String threadName = Thread.currentThread().getName();
		long threadId = TraceReader.threadNameToIdMap.get(threadName);
		long id;

		Vector<Long> v = TraceReader.nanoTimeThreadVec;

		synchronized (ActiveChecker.lock) {

			id = v.get(0);
			// if(id==0)
			// return;

			if (threadId != id) {
				(new ActiveChecker(0)).check();
			}

		}
		id = v.get(0);
		while (threadId != id) {
			ActiveChecker.blockIfRequired();
			id = v.get(0);
		}

		v.remove(0);
		return TraceReader.nanoTimeDataVec.remove(0);

	}
}
