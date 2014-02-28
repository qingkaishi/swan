package cn.edu.nju.swan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import cn.edu.nju.swan.model.PRP;
import cn.edu.nju.swan.model.SwanEvent;

public class MinimalLegalGraph implements Cloneable {

	int[][] matrix;

	public MinimalLegalGraph(Vector<SwanEvent> t) {
		matrix = new int[t.size()][t.size()];
		for (int i = 0; i < t.size(); i++) {
			for (int j = 0; j < t.size(); j++) {
				matrix[i][j] = 0;
			}
		}

		for (int i = 0; i < t.size(); i++) {
			SwanEvent ei = t.get(i);
			for (int j = i + 1; j < t.size(); j++) {
				SwanEvent ej = t.get(j);

				if (ei.sameThreadsImmBefore(ej)) {
					matrix[ei.id][ej.id] = 2;
					continue;
				}
			}
		}
	}

	public Object clone() {
		MinimalLegalGraph o = null;
		try {
			o = (MinimalLegalGraph) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	// //////////////////////////
	public boolean containsSCCWith(PRP p) {
		boolean exist1 = false;
		boolean exist2 = false;

		int f1 = p.r1.se1.id, t1 = p.r1.se2.id;
		int f2 = p.r1.se1.id, t2 = p.r1.se2.id;

		if (matrix[f1][t1] != 0) {
			exist1 = true;
		} else {
			matrix[f1][t1] = 1;
		}

		if (matrix[f2][t2] != 0) {
			exist2 = true;
		} else {
			matrix[f2][t2] = 1;
		}

		if (exist1 && exist2) {
			return false;
		}

		if (containsSCC(matrix)) {
			if (!exist1) {
				matrix[f1][t1] = 0;
			}

			if (!exist2) {
				matrix[f2][t2] = 0;
			}
			return true;
		}

		return false;
	}

	public void getSCCs(int[][] matrix) {
		SCCs.clear();
		nodeindex.clear();
		nodelowlink.clear();
		sstack.clear();
		iindex = 0;

		for (int i = 0; i < matrix.length; i++) {
			nodeindex.add(-1);
			nodelowlink.add(-1);
		}

		for (int i = 0; i < matrix.length; i++) {
			if (nodeindex.get(i) == -1) {
				strongConnect(i, matrix);
			}
		}
	}

	public boolean containsSCC(int[][] matrix) {
		SCCs.clear();
		nodeindex.clear();
		nodelowlink.clear();
		sstack.clear();
		iindex = 0;

		for (int i = 0; i < matrix.length; i++) {
			nodeindex.add(-1);
			nodelowlink.add(-1);
		}

		for (int i = 0; i < matrix.length; i++) {
			if (nodeindex.get(i) == -1) {
				strongConnect(i, matrix);
				if (!SCCs.isEmpty()) {
					return true;
				}
			}
		}

		return !SCCs.isEmpty();
	}

	List<List<Integer>> SCCs = new ArrayList<List<Integer>>();
	List<Integer> nodeindex = new ArrayList<Integer>();
	List<Integer> nodelowlink = new ArrayList<Integer>();
	Stack<Integer> sstack = new Stack<Integer>();
	int iindex = 0;

	private void strongConnect(int v, int[][] matrix) {
		if (!SCCs.isEmpty()) {
			return;
		}

		iindex++;
		nodeindex.set(v, iindex);
		nodelowlink.set(v, iindex);
		sstack.push(v);

		for (int w = 0; w < matrix.length; w++) {
			if (matrix[v][w] != 0) {
				if (nodeindex.get(w) == -1) {
					strongConnect(w, matrix);
					nodelowlink.set(v,
							Math.min(nodelowlink.get(v), nodelowlink.get(w)));
				} else if (nodeindex.get(w) < nodeindex.get(v)) {
					if (sstack.contains(w)) {
						nodelowlink.set(v,
								Math.min(nodelowlink.get(v), nodeindex.get(w)));
					}
				}
			}
		}

		if (nodeindex.get(v).equals(nodelowlink.get(v))) {
			List<Integer> SCC = new ArrayList<Integer>();
			int w = sstack.peek();
			while (nodeindex.get(w) >= nodeindex.get(v)) {
				sstack.pop();
				SCC.add(w);
				if (sstack.isEmpty()) {
					break;
				}
				w = sstack.peek();
			}

			if (SCC.size() > 1)
				SCCs.add(SCC);
		}
	}

	public void output() {
		Vector<SwanEvent> topo = topoSorting();

		File dir = new File("../Swan-Replayer");
		File[] fs = dir.listFiles();
		for (File f : fs) {
			if (f.getAbsolutePath().endsWith(".gz")) {
				f.delete();
			}
		}

		try {
			String name = "swantrace" + System.nanoTime() + ".gz";
			TraceWriter.writeTrace(topo, "../Swan-Replayer/" + name);
			System.err.println("Constructed traces has stored into "
					+ "../Swan-Replayer/" + name);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		for (SwanEvent se : topo) {
//			System.err.println(se);
//		}
	}

	/**
	 * @return
	 */
	private Vector<SwanEvent> topoSorting() {
		Vector<SwanEvent> ret = new Vector<SwanEvent>();

		List<Integer> ints = topologicalSort();
		for (int i : ints) {
			ret.add(SwanAdapterMain.swanTrace.get(i));
		}

		return ret;
	}

	/**
	 * WARNING: this method changes the original matrix
	 * 
	 * @return
	 */
	private List<Integer> topologicalSort() {
		List<Integer> ret = new ArrayList<Integer>();

		while (ret.size() != this.matrix.length) {
			for (int i = 0; i < this.matrix.length; i++) {
				if (ret.contains(i)) {
					continue;
				}

				int indegree = 0;
				for (int j = 0; j < this.matrix.length; j++) {
					if (matrix[j][i] != 0) {
						indegree++;
						break;
					}
				}

				if (indegree == 0) {
					ret.add(i);

					for (int j = 0; j < this.matrix.length; j++) {
						matrix[i][j] = 0;
					}
				}
			}
		}

		return ret;
	}
}
