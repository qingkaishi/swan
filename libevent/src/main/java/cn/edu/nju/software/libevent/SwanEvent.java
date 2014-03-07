/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libevent;

import java.io.Serializable;
import java.util.HashSet;
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
        if(curlockIds!=null)
            this.lockIds.addAll(curlockIds);
    }
    
    public transient Set<SwanEvent> happensBefore = null;
}
