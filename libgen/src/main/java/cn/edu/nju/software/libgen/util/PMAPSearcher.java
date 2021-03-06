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
public class PMAPSearcher {

    private static final int k = 5;

    public static List<PMAP> search(Vector<SwanEvent> trace, KernelGraph kg) {
        List<MAP> maps = new ArrayList<MAP>();
        for (int i = 0; i < trace.size(); i++) {
            SwanEvent ei = trace.get(i);
            for (int j = i + 1; j < trace.size(); j++) {
                SwanEvent ej = trace.get(j);
                if (ei.threadId != ej.threadId
                        && ((ei.accessType == SwanEvent.AccessType.READ && ej.accessType == SwanEvent.AccessType.WRITE)
                        || (ei.accessType == SwanEvent.AccessType.WRITE && ej.accessType == SwanEvent.AccessType.WRITE)
                        || (ei.accessType == SwanEvent.AccessType.WRITE && ej.accessType == SwanEvent.AccessType.READ))) {
                    MAP m = new MAP(ei, ej);
                    maps.add(m);
                }
            }
        }

        //System.out.println(maps);
        List<PMAP> pmaps = new ArrayList<PMAP>();
        int startIdx = 0;
        for (int i = 0; i < maps.size(); i++) {
            MAP mi = maps.get(i);
            for (int j = i + 1; j < maps.size(); j++) {
                MAP mj = maps.get(j);
                PMAP pairMap = null;
                if (mi.sameSharedMemWith(mj)) {
                    // single-variable atomicity violation.
                    if (mi.second().equals(mj.first())) {
                        if (mi.first().threadId == mj.second().threadId) {
                            pmaps.add(new PMAP(mi, mj));
                        }
                    } else if (mi.first().equals(mj.second())) {
                        if (mi.second().threadId == mj.first().threadId) {
                            pmaps.add(new PMAP(mj, mi));
                        }
                    }
                } else {
                    // multi-variable atomicity violation.
                    if (mi.first().threadId == mj.second().threadId
                            && mi.second().threadId == mj.first().threadId) {
                        if (mi.isAllWrite() && mj.isAllWrite()) {
                            pmaps.add(new PMAP(mi, mj));
                        }

                        if (!mi.isAllWrite() && !mj.isAllWrite()) {
                            pmaps.add(new PMAP(mi, mj));
                        }
                    }
                }

                // grouping pmaps for efficiency
                if (pmaps.size() - startIdx == k) {
                    //System.out.println(pmaps);
                    kg.checkSCCwith(pmaps.subList(startIdx, pmaps.size()));
                    startIdx = pmaps.size();
                    //System.out.println(pmaps);
                }
            }
        }

        kg.checkSCCwith(pmaps.subList(startIdx, pmaps.size()));
        return pmaps;
    }

    public static List<PMAP> optimize(List<PMAP> pmaps, KernelGraph kg) {
        for (int i = 1; i < pmaps.size(); i++) {
            PMAP pi = pmaps.get(i);
            for (int j = 0; j < i; j++) {
                PMAP pj = pmaps.get(j);
                if (pi.implies(pj)) {
                    pmaps.set(j, pi);
                    pmaps.remove(i);
                    i--;
                    break;
                }

                if (pj.implies(pi)) {
                    pmaps.remove(i);
                    i--;
                    break;
                }
            }
        }

        return pmaps;
    }

}
