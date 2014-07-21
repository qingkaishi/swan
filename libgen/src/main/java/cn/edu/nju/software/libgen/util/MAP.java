/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
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

    public MAP(SwanEvent p1, SwanEvent p2) {
        if (p1 != null && p2 != null) {
            pair.add(p1);
            pair.add(p2);
        } else {
            throw new RuntimeException("MAP error: cannot add null to an MAP.");
        }
    }

    @Override
    public String toString() {
        return first().accessType.name() + "(" + first().idx + ")" + first().sharedMemId + "-->"
                + second().accessType.name() + "(" + second().idx + ")" + second().sharedMemId;
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
        if (closure == null) {
            SwanEvent from = first();
            SwanEvent to = second();
            Vector<Integer> lset1 = new Vector<Integer>(from.lockIds);
            Vector<Integer> lset2 = to.lockIds;

            lset1.retainAll(lset2);

            if (lset1.isEmpty()) {
                closure = this;
                return closure;
            }

            SwanEvent n = from;
            while (n != null) {
                SwanEvent tmp = n.sameThreadNext;
                if (tmp == null) {
                    break;
                }

                if (n.accessType == SwanEvent.AccessType.RELEASE
                        || n.accessType == SwanEvent.AccessType.WAIT_RELEASE) {
                    Vector<Integer> lsetTmp = tmp.lockIds;
                    // check whether lsetTmp contains some locks in lset1
                    if (!containsLockIn(lsetTmp, lset1)) {
                        break;
                    }
                }
                n = tmp;
            }

            SwanEvent m = to;
            while (m != null) {
                SwanEvent tmp = m.sameThreadLast;
                if (tmp == null) {
                    break;
                }

                if (m.accessType == SwanEvent.AccessType.ACQUIRE
                        || m.accessType == SwanEvent.AccessType.WAIT_ACQUIRE) {

                    Vector<Integer> lsetTmp = tmp.lockIds;
                    // check whether lsetTmp contains some locks in lset1
                    if (!containsLockIn(lsetTmp, lset1)) {
                        break;
                    }
                }
                m = tmp;
            }
            closure = new MAP(n, m);
        }
        return closure;
    }

    // check whether there exists a lock in set2, also exists in set1
    private boolean containsLockIn(Vector<Integer> set1, Vector<Integer> lockset) {
        for (int i = 0; i < lockset.size(); i++) {
            int l = lockset.get(i);
            if (set1.contains(l)) {
                return true;
            }
        }
        return false;
    }

    boolean implies(MAP m) {
        // do not use closure to implement implies relation
        // it is only used for Scc test.
        SwanEvent thisFrom = this.first();
        SwanEvent thisTo = this.second();

        SwanEvent mFrom = m.first();
        SwanEvent mTo = m.second();

        if (mFrom.threadId == thisFrom.threadId
                && mTo.threadId == thisTo.threadId) {
            return mFrom.idx <= thisFrom.idx
                    && mTo.idx >= thisTo.idx;
        }
        return false;
    }
}
