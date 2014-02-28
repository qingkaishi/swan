package cn.edu.nju.swan.cfg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import soot.Unit;

public class UnitNode implements Serializable {
	private static final long serialVersionUID = 513206101149226179L;

	private String unitSignature;

	private Vector<UnitNode> preds = new Vector<UnitNode>();
	private Vector<UnitNode> succs = new Vector<UnitNode>();

	transient Unit tempUnit;

	public UnitNode(Unit u) {
		unitSignature = u.toString();
		this.tempUnit = u;
	}

	public Vector<UnitNode> getPreds() {
		return preds;
	}

	public Vector<UnitNode> getSuccs() {
		return succs;
	}

	public String getUnitSignature() {
		return unitSignature;
	}

	public void addPreds(UnitNode unitNode) {
		this.preds.add(unitNode);
	}

	public void addSuccs(UnitNode unitNode) {
		this.succs.add(unitNode);
	}

	public boolean canReach(UnitNode target) {
		List<UnitNode> visited = new ArrayList<UnitNode>();

		Queue<UnitNode> queue = new LinkedList<UnitNode>();
		queue.add(this);

		while (!queue.isEmpty()) {
			UnitNode head = queue.poll();

			if (head.equals(target)) {
				return true;
			}

			for (UnitNode u : head.getSuccs()) {
				if(target.equals(u)){
					return true;
				}
				
				if (u.getUnitSignature().contains("start lock") || u.getUnitSignature().contains("end lock")) {
					visited.add(u);
				}

				if (!visited.contains(u)) {
					queue.add(u);
					visited.add(u);
				}
			}
		}

		return false;
	}

	public boolean canReachMethodInvoke(String methodSig) {
		List<UnitNode> visited = new ArrayList<UnitNode>();

		Queue<UnitNode> queue = new LinkedList<UnitNode>();
		queue.add(this);

		while (!queue.isEmpty()) {
			UnitNode head = queue.poll();

			if (head.getUnitSignature().contains(methodSig)) {
				return true;
			}

			for (UnitNode u : head.getSuccs()) {
				if(u.getUnitSignature().contains(methodSig)){
					return true;
				}
				
				if (u.getUnitSignature().contains("start lock") || u.getUnitSignature().contains("end lock")) {
					visited.add(u);
				}

				if (!visited.contains(u)) {
					queue.add(u);
					visited.add(u);
				}
			}
		}

		return false;
	}

}
