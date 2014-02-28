package cn.edu.nju.swan.cfg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import soot.Unit;
import soot.toolkits.graph.DirectedGraph;

public class ControlFlowGraph implements Serializable {

	private static final long serialVersionUID = -2684703835405580036L;

	private Vector<UnitNode> heads = new Vector<UnitNode>();
	
	private Map<String, UnitNode> nameNodeMaps = new HashMap<String, UnitNode>();
	
	private String methodSignature;
	private String classSignature;
	
	private Map<Long, UnitNode> pointerMap = new HashMap<Long, UnitNode>();
	
	public ControlFlowGraph(DirectedGraph<Unit> cfg, String classsig, String methodSig) {
		this.methodSignature = methodSig;
		this.classSignature = classsig;
		
		Map<Unit, UnitNode> tempMap = new HashMap<Unit, UnitNode>();
		
		Iterator<Unit> unitsIt = cfg.iterator();
		while(unitsIt.hasNext()){
			Unit u = unitsIt.next();
			UnitNode un = new UnitNode(u);
			tempMap.put(u, un);
			
			this.nameNodeMaps.put(un.getUnitSignature(), un);
		}
		
		for(UnitNode un : this.nameNodeMaps.values()){
			List<Unit> predsUnit = cfg.getPredsOf(un.tempUnit);
			for(Unit u : predsUnit){
				un.addPreds(tempMap.get(u));
			}
			
			List<Unit> succsUnit = cfg.getSuccsOf(un.tempUnit);
			for(Unit u : succsUnit){
				un.addSuccs(tempMap.get(u));
			}
		}
		
		List<Unit> headsUnit  = cfg.getHeads();
		for(Unit u : headsUnit){
			this.heads.add(tempMap.get(u));
		}
	}

	public Vector<UnitNode> getPredsOf(UnitNode un) {
		return un.getPreds();
	}

	public Vector<UnitNode> getSuccsOf(UnitNode un) {
		return un.getSuccs();
	}

	public Vector<UnitNode> getHeads() {
		return heads;
	}

	public Iterator<UnitNode> iterator(){
		return nameNodeMaps.values().iterator();
	}
	
	public UnitNode getUnitNodeBySignature(String sig){
		return nameNodeMaps.get(sig);
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	public void setUnitNodePointerBeforeInvoke(long currentThreadId, UnitNode unitNodeBySignature) {
		pointerMap.put(currentThreadId, unitNodeBySignature);
	}
	
	public UnitNode getUnitNodePointer(long currentThreadId){
		return pointerMap.get(currentThreadId);
	}

	public String getClassSignature() {
		return classSignature;
	}

	public void setClassSignature(String classSignature) {
		this.classSignature = classSignature;
	}
}
