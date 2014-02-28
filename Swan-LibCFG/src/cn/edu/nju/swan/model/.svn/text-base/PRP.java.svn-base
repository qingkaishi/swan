package cn.edu.nju.swan.model;

public class PRP {

	public RacePair r1, r2;

	public PRP(RacePair r1, RacePair r2) {
		this.r1 = r1;
		this.r2 = r2;
	}

	public boolean implies(PRP n) {
		return (this.r1.implies(n.r1) && this.r2.implies(n.r2))
				|| (this.r2.implies(n.r1) && this.r1.implies(n.r2));
	}

}
