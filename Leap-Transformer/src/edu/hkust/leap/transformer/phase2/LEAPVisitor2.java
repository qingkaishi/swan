package edu.hkust.leap.transformer.phase2;

import edu.hkust.leap.Parameters;
import edu.hkust.leap.Util;
import edu.hkust.leap.transformer.Visitor;
import edu.hkust.leap.transformer.contexts.*;
import soot.ArrayType;
import soot.Body;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;

public class LEAPVisitor2 extends Visitor {

    public LEAPVisitor2(Visitor visitor) {
        super(visitor);
    }
    public void visitStmtAssign(SootMethod sm, Chain units, AssignStmt assignStmt) {
        nextVisitor.visitStmtAssign(sm, units, assignStmt);
    }
    public void visitStmtEnterMonitor(SootMethod sm, Chain units, EnterMonitorStmt enterMonitorStmt) 
    {
    	Visitor.sharedaccessnum++;
    	Visitor.totalaccessnum++; 	
    	Visitor.instrusharedaccessnum++;
    	
    	Value op = enterMonitorStmt.getOp();
    	Type type = op.getType();
    	String sig = type.toString()+".OBJECT";
    	Value memory = StringConstant.v(sig);
    	
    	if(Parameters.isRuntime)
    		Visitor.addCallAccessSyncObj(sm, units, enterMonitorStmt, "enterMonitorAfter",memory, false);
    	else if(Parameters.isReplay)
    		Visitor.addCallAccessSyncObj(sm, units, enterMonitorStmt, "enterMonitorBefore",memory, true);
    	
    	
		if(Parameters.isReplay&&Parameters.removeSync)
			units.remove(enterMonitorStmt);
		
        nextVisitor.visitStmtEnterMonitor(sm, units, enterMonitorStmt);
    }

    public void visitStmtExitMonitor(SootMethod sm, Chain units, ExitMonitorStmt exitMonitorStmt) {
    	Visitor.sharedaccessnum++;
    	Visitor.totalaccessnum++; 	
    	Visitor.instrusharedaccessnum++;
    	
    	Value op = exitMonitorStmt.getOp();
    	Type type = op.getType();
    	String sig = type.toString()+".OBJECT";;
    	Value memory = StringConstant.v(sig);
    	
		//Visitor.addCallAccessSyncObj(sm, units, exitMonitorStmt, "exitMonitorBefore",memory, true);
		
		if(Parameters.isReplay&&Parameters.removeSync)
			units.remove(exitMonitorStmt);
		
        nextVisitor.visitStmtExitMonitor(sm, units, exitMonitorStmt);
    }

    public void visitInstanceInvokeExpr(SootMethod sm, Chain units, Stmt s, InstanceInvokeExpr invokeExpr, InvokeContext context) 
    {
            String sigclass = invokeExpr.getMethod().getDeclaringClass().getName()+".OBJECT";//+"."+invokeExpr.getMethod().getName();
            Value memory = StringConstant.v(sigclass);
            
            Value base = invokeExpr.getBase();
            String sig = invokeExpr.getMethod().getSubSignature();
            if (sig.equals("void wait()")||sig.equals("void wait(long)") || sig.equals("void wait(long,int)")) 
            {       	

        		Visitor.addCallAccessSyncObj(sm, units, s, "waitAfter",memory, false);
            	Visitor.instrusharedaccessnum++;
            	Visitor.sharedaccessnum++;
            	Visitor.totalaccessnum++;
            	
            	if(Parameters.isReplay&&Parameters.removeSync)
        			units.remove(s);

            //} else if (sig.equals("void wait(long)") || sig.equals("void wait(long,int)")) {

            } else if (sig.equals("void notify()")) 
            {
        		Visitor.addCallAccessSyncObj(sm, units, s, "notifyBefore",memory, true);
            	Visitor.instrusharedaccessnum++;
            	Visitor.sharedaccessnum++;
            	Visitor.totalaccessnum++;
            	
            	if(Parameters.isReplay&&Parameters.removeSync)
        			units.remove(s);
            } else if (sig.equals("void notifyAll()")) 
            {
        		Visitor.addCallAccessSyncObj(sm, units, s, "notifyAllBefore",memory, true);
            	Visitor.instrusharedaccessnum++;
            	Visitor.sharedaccessnum++;
            	Visitor.totalaccessnum++;
            	
            	if(Parameters.isReplay&&Parameters.removeSync)
        			units.remove(s);
            }
            else if (sig.equals("void start()") && isThreadSubType(invokeExpr.getMethod().getDeclaringClass())) 
            {
        		Visitor.addCallstartRunThreadBefore(sm, units, s, "startRunThreadBefore", invokeExpr.getBase());
            }
            else if ((sig.equals("void join()") || sig.equals("void join(long)") || sig.equals("void join(long,int)"))
                    && isThreadSubType(invokeExpr.getMethod().getDeclaringClass()))
            {
        		Visitor.addCallJoinRunThreadAfter(sm, units, s, "joinRunThreadAfter", invokeExpr.getBase());
            }
            else if(invokeExpr.getMethod().isSynchronized())
            {
            	if(Parameters.isReplay)
            	{
            		Visitor.addCallAccessSyncObj(sm, units, s, "enterMonitorBefore",memory, true);
            	}
            }
        
        nextVisitor.visitInstanceInvokeExpr(sm, units, s, invokeExpr, context);
    }

    public void visitStaticInvokeExpr(SootMethod sm, Chain units, Stmt s, StaticInvokeExpr invokeExpr, InvokeContext context) {
        
    	nextVisitor.visitStaticInvokeExpr(sm, units, s, invokeExpr, context);
    }

    public void visitArrayRef(SootMethod sm, Chain units, Stmt s, ArrayRef arrayRef, RefContext context) {

        nextVisitor.visitArrayRef(sm, units, s, arrayRef, context);
    }

    public void visitInstanceFieldRef(SootMethod sm, Chain units, Stmt s, InstanceFieldRef instanceFieldRef, RefContext context) 
    {
    	Visitor.totalaccessnum++;

    	Value base = instanceFieldRef.getBase();
    	
		String sig = instanceFieldRef.getField().getDeclaringClass().getName()+"."+instanceFieldRef.getField().getName()+".INSTANCE";
		Value memory = StringConstant.v(sig);
		
		if(!instanceFieldRef.getField().isFinal())
		if(Visitor.sharedVariableWriteAccessSet.contains(sig))
		{	
			String methodname = "readBeforeInstance"; 
			
			if (context != RHSContextImpl.getInstance()) 
	        {
				methodname = "writeBeforeInstance";
	        }
			else if(instanceFieldRef.getField().getType() instanceof ArrayType)
			{
				
				Stmt nextStmt =  (Stmt)units.getSuccOf(s);
				if(s instanceof AssignStmt && nextStmt instanceof AssignStmt)
				{
					AssignStmt assgnStmt = (AssignStmt) s;
					AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
					if(assgnNextStmt.getLeftOp().toString().contains(assgnStmt.getLeftOp().toString()))
			        {
						methodname = "writeBeforeInstance";
			        }
				}  
			}
				
		    Visitor.addCallAccessSPEInstance(sm,units, s, methodname, base, memory, true);
			Visitor.sharedaccessnum++;
		    Visitor.instrusharedaccessnum++;        	
		}
        nextVisitor.visitInstanceFieldRef(sm, units, s, instanceFieldRef, context);
    }

    public void visitStaticFieldRef(SootMethod sm, Chain units, Stmt s, StaticFieldRef staticFieldRef, RefContext context) 
    {      
    	Visitor.totalaccessnum++;
        String sig = staticFieldRef.getField().getDeclaringClass().getName()+"."+staticFieldRef.getField().getName()+".STATIC";
		Value memory = StringConstant.v(sig);

		if(!staticFieldRef.getField().isFinal())
        if(Visitor.sharedVariableWriteAccessSet.contains(sig))
		{	
			String methodname = "readBeforeStatic"; 
			
			if (context != RHSContextImpl.getInstance()) 
	        {
				methodname = "writeBeforeStatic";
	        }
			else if(staticFieldRef.getField().getType() instanceof ArrayType)
			{
				Stmt nextStmt =  (Stmt)units.getSuccOf(s);
				if(s instanceof AssignStmt && nextStmt instanceof AssignStmt)
				{
					AssignStmt assgnStmt = (AssignStmt) s;
					AssignStmt assgnNextStmt = (AssignStmt) nextStmt;
					if(assgnNextStmt.getLeftOp().toString().contains(assgnStmt.getLeftOp().toString()))
			        {
						methodname = "writeBeforeStatic";
			        }
				}  
			}
				
		    Visitor.addCallAccessSPEStatic(sm,units, s, methodname, memory, true);
		    Visitor.sharedaccessnum++;
		    Visitor.instrusharedaccessnum++;        	
		}
        nextVisitor.visitStaticFieldRef(sm, units, s, staticFieldRef, context);
    }
}
