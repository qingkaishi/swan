/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libevent;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
public class SwanEvent implements Serializable {

    public enum AccessType {

        READ, WRITE, ACQUIRE, RELEASE, WAIT_RELEASE, WAIT_ACQUIRE, NOTIFY, NOTIFYALL, FORK, JOIN;
    }

    public int threadId, sharedMemId, lineNo;
    public String clsname;
    public AccessType accessType;
    public Vector<Integer> lockIds = new Vector<Integer>();
    public boolean patchedEvent = false;

    public SwanEvent(int threadId, int sharedMemId, List<Integer> curlockIds, AccessType accessType, String clsname, int lineNo) {
        this.threadId = threadId;
        this.sharedMemId = sharedMemId;
        this.accessType = accessType;
        this.lineNo = lineNo;
        this.clsname = clsname;
        if (curlockIds != null) {
            this.lockIds.addAll(curlockIds);
        }
    }

    public transient Set<SwanEvent> happensBefore = null;
    public transient Set<SwanEvent> temporalNext = null;
    public transient SwanEvent sameThreadNext = null;
    public transient SwanEvent sameThreadLast = null;
    public transient int idx;

    public void addTemporalNext(SwanEvent se) {
        if (temporalNext == null) {
            temporalNext = new HashSet<SwanEvent>();
        }
        
        temporalNext.add(se);
    }
    
    public void removeTemporalNext(SwanEvent se) {
        if (temporalNext == null) {
            return;
        }
        
        temporalNext.remove(se);
    }
    
    public boolean containsTemporalNext(SwanEvent se) {
        if (temporalNext == null) {
            return false;
        }
        
        return temporalNext.contains(se);
    }
    
    public boolean containsHappensBeforeNext(SwanEvent se) {
        if (happensBefore == null) {
            return false;
        }
        
        return happensBefore.contains(se);
    }
    
    public void clearTemporalNext(){
        if (temporalNext != null) {
            temporalNext.clear();
        }
    }
    
    public Iterator<SwanEvent> getEdgeIteraror(){
        Set<SwanEvent> temp = new HashSet<SwanEvent>();
        if(happensBefore != null){
            temp.addAll(happensBefore);
        }
        
        if(temporalNext != null) {
            temp.addAll(temporalNext);
        }
        
        return temp.iterator();
    }
}
