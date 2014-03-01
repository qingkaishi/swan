package edu.hkust.leap.transformer.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Hierarchy;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.PAG;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.thread.mhp.MethodExtentBuilder;
import soot.jimple.toolkits.thread.mhp.MethodInliner;
import soot.jimple.toolkits.thread.mhp.PegGraph;
import soot.jimple.toolkits.thread.mhp.findobject.AllocNodesFinder;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;

public class MyMhpTransformer {
	public static PegGraph internalTransform(String phaseName, Map options)
	{
		//System.err.println("here1");
		
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		PAG pag =null;
		if (pta instanceof PAG){
			pag = (PAG)pta;
		}
		else{
			System.err.println("Please add spark option when you run this program!");
			System.exit(1);
		}
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		CallGraph callGraph = Scene.v().getCallGraph();
		SootMethod sootMethod= Scene.v().getMainClass().getMethodByName("main");
		Body body = sootMethod.retrieveActiveBody();
		long beginBuildPegTime = System.currentTimeMillis();
		PegCallGraph pcg = new PegCallGraph(callGraph);	
		MethodExtentBuilder meb = new MethodExtentBuilder(body, pcg, callGraph);     
		Set<Object> methodsNeedingInlining = meb.getMethodsNeedingInlining();
		Map synchObj = new HashMap();
		Map allocNodeToObj = new HashMap();
		AllocNodesFinder anf = new AllocNodesFinder(pcg, callGraph, pag);
		ArrayList inlineSites = new ArrayList();
		PegGraph pegGraph = buildPeg( callGraph, hierarchy, pag, methodsNeedingInlining, 
			anf.getAllocNodes(), inlineSites, synchObj, anf.getMultiRunAllocNodes(), allocNodeToObj, body, sootMethod);	
		
		
		//Globals.pegGraph = pegGraph;
		//System.err.println(pegGraph);
			
		MethodInliner.inline(inlineSites);
/*		long buildPegDuration = (System.currentTimeMillis() - beginBuildPegTime );
		System.err.println("Peg Duration: "+ buildPegDuration);
		System.err.println("Time for building PEG: " + buildPegDuration/100 + "."
				+buildPegDuration % 100 +" seconds");
		long beginMhpTime = System.currentTimeMillis();
		long mhpAnalysisDuration = (System.currentTimeMillis() - beginMhpTime);
		long beginSccTime = System.currentTimeMillis();	
		long sccDuration =  (System.currentTimeMillis() - beginSccTime);
		long beginSeqTime = System.currentTimeMillis();
		long seqDuration = (System.currentTimeMillis() - beginSeqTime);
		long afterBeginMhpTime = System.currentTimeMillis();
		mhpAnalysisDuration = (System.currentTimeMillis() - afterBeginMhpTime);		
		long duration = (System.currentTimeMillis() - beginBuildPegTime);
		System.err.println("Total time: " + duration );
		System.err.println(" SCC duration "+ sccDuration);
		System.err.println(" Seq duration "+ seqDuration);
		System.err.println("after compacting mhp duration: "+mhpAnalysisDuration);
*/		
		return pegGraph;
	}
	
	protected static PegGraph buildPeg( CallGraph callGraph, Hierarchy hierarchy, PAG pag, Set<Object> methodsNeedingInlining, Set<AllocNode> allocNodes, List inlineSites, Map synchObj, Set<AllocNode> multiRunAllocNodes, Map allocNodeToObj, Body body, 
			SootMethod sm){
		
		PegGraph pG = new PegGraph( callGraph, hierarchy, pag, methodsNeedingInlining, allocNodes, inlineSites, synchObj, multiRunAllocNodes, allocNodeToObj, body,  sm, true,  false);
		return pG;
	}
	public static void transfrom(String phaseName, Map options)
	{
		internalTransform( phaseName,  options);
	}
}
