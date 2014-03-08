/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    public AccessType accessType;
    public Vector<Integer> lockIds = new Vector<Integer>();
    public boolean patchedEvent = false;

    public SwanEvent(int threadId, int sharedMemId, List<Integer> curlockIds, AccessType accessType, int lineNo) {
        this.threadId = threadId;
        this.sharedMemId = sharedMemId;
        this.accessType = accessType;
        this.lineNo = lineNo;
        if (curlockIds != null) {
            this.lockIds.addAll(curlockIds);
        }
    }

    public transient Set<SwanEvent> happensBefore = null;
    public transient Set<SwanEvent> temporalNext = null;
    public transient SwanEvent sameThreadNext = null;
    public transient SwanEvent sameThreadLast = null;

    public void addTemporalNext(SwanEvent se) {
        if (temporalNext == null) {
            temporalNext = new HashSet<SwanEvent>();
        }
        
        temporalNext.add(se);
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
