package edu.hkust.leap.transformer.phase1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import edu.hkust.leap.Parameters;
import edu.hkust.leap.Util;
import edu.hkust.leap.tloax.XFieldThreadEscapeAnalysis;
import edu.hkust.leap.transformer.Visitor;

import soot.Body;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.util.Chain;

public class WholeProgramTransformer extends SceneTransformer
{
	protected void internalTransform(String pn, Map map)
	{
		Visitor.tlo = new ThreadLocalObjectsAnalysis(new UnsynchronizedMhpAnalysis());
		//Visitor.ftea = new XFieldThreadEscapeAnalysis();
		//Visitor.pecg = new PegCallGraph(Scene.v().getCallGraph());
		
		Iterator<SootClass> classIt = Scene.v().getApplicationClasses().iterator();
		while (classIt.hasNext()) 
		{
			SootClass sc =  classIt.next();
			String scname = sc.getName();
			//System.out.println("scname: "+scname);

			if(!Util.shouldInstruThisClass(scname)) 
				continue;
		         		        	  		       		        	  
			Iterator<SootMethod> methodIt = sc.getMethods().iterator();
     	  
			while (methodIt.hasNext()) 
			{
				SootMethod sm = methodIt.next();	
				
/** The following code is for optimization using synchronized ownership
				if(Parameters.isRuntime)
				{
					List list = Visitor.pecg.getPredsOf(sm);
					if (list.size()>0)
					{
						Iterator it = list.iterator();
						boolean flag = false;
						while (it.hasNext())
						{
							SootMethod met = (SootMethod)it.next();
							if(!met.isSynchronized())
							{
								flag = false;
								break;
							}
							else
								flag = true;
						}
						if(flag)
						{
							Visitor.synchronizedIgnoreMethodSet.add(sm);
							System.err.println(" *** synchronizedIgnoreMethod: "+sm);
							continue;
						}
					}
				}
				else
				{
					if(Visitor.synchronizedIgnoreMethodSet.contains(sm))
						continue;
				}
*/
				String smname = sm.getName();
				if(!Util.shouldInstruThisMethod(smname))
					continue;
				
				if(sm.isAbstract() || sm.isNative())
					continue;

				try
				{
					Body body = sm.retrieveActiveBody();
					TransformerForInstrumentation.v().transforming(body, pn, map);
				}catch(Exception e)
				{
					continue;
				}
			}
		}
	}
}
