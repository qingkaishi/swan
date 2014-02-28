package cn.edu.nju.swan.model;

import java.util.List;

public class RacePair {

	public SwanEvent se1, se2;

	public RacePair(SwanEvent se1, SwanEvent se2) {
		this.se1 = se1;
		this.se2 = se2;
	}

	public boolean canWith(RacePair rpj) {
		if (this.se1.ti != rpj.se2.ti || this.se2.ti != rpj.se1.ti) {
			return false;
		}

		if (this.se1.hasLocked() && this.se2.hasLocked() && rpj.se1.hasLocked()
				&& rpj.se2.hasLocked()) {
			return false;
		}

		if (this.se1.ai == SwanEvent.WRITE && this.se2.ai == SwanEvent.WRITE
				&& rpj.se1.ai == SwanEvent.WRITE
				&& rpj.se2.ai == SwanEvent.WRITE) {
			return true;
		}

		if (this.se1.mi == rpj.se1.mi) {
			// single-var
			if (this.se1.equals(rpj.se2)) {
				// rpj.1, rpj2, this1, this2
				if (rpj.se1.ai == SwanEvent.READ
						&& rpj.se2.ai == SwanEvent.WRITE
						&& (this.se2.ai == SwanEvent.READ || this.se2.ai == SwanEvent.WRITE)) {
					return true;
				}

				if (rpj.se1.ai == SwanEvent.WRITE
						&& rpj.se2.ai == SwanEvent.READ
						&& this.se2.ai == SwanEvent.WRITE) {
					return true;
				}

				if (rpj.se1.ai == SwanEvent.WRITE
						&& rpj.se2.ai == SwanEvent.WRITE
						&& this.se2.ai == SwanEvent.READ) {
					return true;
				}
			} else if (this.se2.equals(rpj.se1)) {
				// this1, this2, rpj.1, rpj2
				if (this.se1.ai == SwanEvent.READ
						&& this.se2.ai == SwanEvent.WRITE
						&& (rpj.se2.ai == SwanEvent.READ || rpj.se2.ai == SwanEvent.WRITE)) {
					return true;
				}

				if (this.se1.ai == SwanEvent.WRITE
						&& this.se2.ai == SwanEvent.READ
						&& rpj.se2.ai == SwanEvent.WRITE) {
					return true;
				}

				if (this.se1.ai == SwanEvent.WRITE
						&& this.se2.ai == SwanEvent.WRITE
						&& rpj.se2.ai == SwanEvent.READ) {
					return true;
				}
			}
		} else {
			// multi-var
			if ((rpj.se2.ai == SwanEvent.READ || rpj.se2.ai == SwanEvent.WRITE)
					&& (rpj.se1.ai == SwanEvent.READ || rpj.se1.ai == SwanEvent.WRITE)
					&& (this.se1.ai == SwanEvent.READ || this.se1.ai == SwanEvent.WRITE)
					&& (this.se2.ai == SwanEvent.READ || this.se2.ai == SwanEvent.WRITE)) {
				if (this.se1.ai == rpj.se2.ai && this.se2.ai == rpj.se1.ai
						&& this.se1.ai != this.se2.ai) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean implies(RacePair r) {
		SwanThread st = this.se1.mythread;
		List<SwanEvent> events = st.events;
		int index = events.indexOf(se1);

		boolean found1 = false;
		for (int i = index; i >= 0; i--) {
			SwanEvent se1p = events.get(i);
			if (se1p.si.equals(r.se1.si)) {
				found1 = true;
				break;
			}
		}

		if (found1) {
			SwanThread st2 = this.se2.mythread;
			List<SwanEvent> events2 = st2.events;

			int index2 = events2.indexOf(se2);
			boolean found2 = false;
			for (int i = index2; i < events2.size(); i++) {
				SwanEvent se2p = events2.get(i);

				if (se2p.si.equals(r.se2.si)) {
					found2 = true;
					break;
				}
			}

			return found2;
		}

		return false;
	}

	public RacePair closure() {
		if (se1.hasLocked() && se2.hasLocked()) {
			SwanEvent ne1 = null, ne2 = null;

			List<SwanEvent> events1 = se1.mythread.events;
			for (int i = events1.indexOf(se1); i < events1.size(); i++) {
				if (events1.get(i).ai == SwanEvent.RELEASE) {
					ne1 = events1.get(i);
				}
			}

			List<SwanEvent> events2 = se2.mythread.events;
			for (int i = events2.indexOf(se2); i >= 0; i--) {
				if (events2.get(i).ai == SwanEvent.ACQUIRE) {
					ne2 = events2.get(i);
				}
			}

			return new RacePair(ne1, ne2);
		} else {
			return this;
		}
	}
}
