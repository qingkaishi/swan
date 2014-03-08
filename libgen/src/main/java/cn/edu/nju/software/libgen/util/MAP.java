/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.edu.nju.software.libgen.util;

import cn.edu.nju.software.libevent.SwanEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author qingkaishi
 */
class MAP {
    private List<SwanEvent> pair = new ArrayList<SwanEvent>(2);
    private MAP closure = null;
    
    public MAP(SwanEvent p1, SwanEvent p2){
        if(p1!=null && p2!=null){
            pair.add(p1);
            pair.add(p2);
        }else{
            throw new RuntimeException("MAP error: cannot add null to an MAP.");
        }
    }

    boolean sameSharedMemWith(MAP mj) {
        int thisSvId = pair.get(0).sharedMemId;
        int thatSvId = mj.pair.get(0).sharedMemId;
        return thisSvId == thatSvId;
    }
    
    SwanEvent first() {
        return pair.get(0);
    }
    
    SwanEvent second() {
        return pair.get(1);
    }

    boolean isAllWrite() {
        return this.first().accessType == SwanEvent.AccessType.WRITE 
                && this.second().accessType == SwanEvent.AccessType.WRITE; 
    }

    MAP getEdgeClosure() {
        if(closure == null) {
            SwanEvent from = first();
            SwanEvent to = second();
            Vector<Integer> lset1 = new Vector<Integer>(from.lockIds);
            Vector<Integer> lset2 = to.lockIds;
            
            lset1.retainAll(lset2); //TODO
            // lset1 is the common locks belongs to e1 and e2
            // check events after e1, and those before e2
        }
        return closure;
    }
}
