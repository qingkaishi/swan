package cn.edu.nju.swan.model;

import java.io.Serializable;
import java.util.Vector;

public class SwanThread implements Serializable {
	long tid;
	Vector<SwanEvent> events = new Vector<SwanEvent>();

	public SwanThread(long ti, SwanEvent swanEvent) {
		this.tid = ti;
		this.events.add(swanEvent);
	}

	private static final long serialVersionUID = 1L;

	public void add(SwanEvent swanEvent) {
		events.add(swanEvent);
	}

}
