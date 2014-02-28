package cn.edu.nju.swan.model;

import java.io.Serializable;

public class SwanEvent implements Serializable {
	public static final int READ = 0;
	public static final int WRITE = 1;
	public static final int ACQUIRE = 2;
	public static final int RELEASE = 3;
	public static final int FORK = 4;
	public static final int JOIN = 5;

	private static final long serialVersionUID = -1583490909360043613L;
	private static int globalId = 0;

	public int id;
	public long ti;
	public int mi;
	public int ai;
	public String si;
	public SwanThread mythread;

	public SwanEvent(long t, int m, int a, String s) {
		id = globalId++;
		ti = t;
		mi = m;
		ai = a;
		si = s;
	}

	@Override
	public String toString() {
		return "{" + id + "}" + this.locked + "[thread-" + ti + "]" + "[sv="
				+ mi + "]" + "[type=" + ai + "]" + si;
	}

	public boolean sameThreadsImmBefore(SwanEvent ej) {
		if (this.mythread.equals(ej.mythread)) {
			return this.mythread.events.indexOf(this) == ej.mythread.events
					.indexOf(ej) - 1;
		}
		return false;
	}

	private boolean locked = false;

	public void setlocked(boolean b) {
		locked = b;
	}

	public boolean hasLocked() {
		return locked;
	}

}
