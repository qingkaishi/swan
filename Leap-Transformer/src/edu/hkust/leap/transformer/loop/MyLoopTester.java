package edu.hkust.leap.transformer.loop;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.jimple.Stmt;

public class MyLoopTester extends BodyTransformer {

	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		// TODO Auto-generated method stub
		//System.err.println("here3");
		//System.err.println(b);
		//MyLoopFinder mlf = new MyLoopFinder(Globals.pegGraph);
		SootMethod sm = b.getMethod();
		
        //Iterator trapIt = traps.snapshotIterator();
		Collection<MyLoop> loops = Globals.loopsMap.get(sm);
		
		System.err.println("total loops: "+loops.size());
		
		if(loops!=null)
		{
			Iterator lpIt = loops.iterator(); 
	        while (lpIt.hasNext()) 
	        {
	           	 MyLoop loop = (MyLoop)lpIt.next();
	        	 List stmts = loop.getLoopStatements();
	        	 System.err.println("loop size: "+stmts.size()); 
	        	 //System.out.println(stmts); 
	        	 
	             Iterator stmtIt = b.getUnits().snapshotIterator();
	             while(stmtIt.hasNext())
	             {

	            	 Stmt s = (Stmt) stmtIt.next();
	            	 if(stmts.contains(s))
	            	 {
	            		 //System.out.println(s);
	            	 }
	             }
	        }
		}
		
	}

}
