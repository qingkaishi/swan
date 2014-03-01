package edu.hkust.leap.transformer.phase2;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.hkust.leap.Parameters;
import edu.hkust.leap.Util;
import edu.hkust.leap.transformer.loop.MyLoop;
import edu.hkust.leap.transformer.loop.MyLoopFinder;
import edu.hkust.leap.transformer.loop.MyMhpTransformer;

import soot.Body;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.thread.mhp.PegGraph;
/** FIND loops for inside a method body */
public class TransformerForLoops {
	//public static PegGraph pegGraph;
	public static Collection<MyLoop> loops;
	public static HashSet<Stmt> loopstmts = new HashSet<Stmt>();
	
	public static HashMap<SootMethod,Collection<MyLoop>> loopsMap = new HashMap<SootMethod,Collection<MyLoop>>();
	public static void internalTransform(Body body, String phaseName, Map options) {
		
		loops = MyLoopFinder.internalTransform(body,phaseName,options);
		getLoopStmts();
	}
	private static void getLoopStmts()
	{
		loopstmts.clear();
		if(loops==null)
			return;
		
		Iterator lpIt = loops.iterator(); 
        while (lpIt.hasNext()) 
        {
           	 MyLoop loop = (MyLoop)lpIt.next();
        	 List stmts = loop.getLoopStatements();
        	 if(stmts.size()<Parameters.LOOP_STMT_COUNT)
        	 {
	             Iterator stmtIt = stmts.iterator();
	             while(stmtIt.hasNext())
	            	 loopstmts.add((Stmt)stmtIt.next());
        	 }
        }
	}
}
