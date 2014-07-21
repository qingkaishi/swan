/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libgen.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qingkaishi
 */
public class PMAP {

    private List<MAP> pair = new ArrayList<MAP>(2);
    private PMAP closure = null;

    public PMAP(MAP p1, MAP p2) {
        if (p1 != null && p2 != null) {
            pair.add(p1);
            pair.add(p2);
        } else {
            throw new RuntimeException("PMAP error: cannot add null to a PMAP.");
        }
    }

    @Override
    public String toString() {
        return first().toString() + "; " + second().toString(); //To change body of generated methods, choose Tools | Templates.
    }

    MAP first() {
        return pair.get(0);
    }

    MAP second() {
        return pair.get(1);
    }

    PMAP getEdgeClosure() {
        if (closure == null) {
            MAP one = first().getEdgeClosure();
            MAP two = second().getEdgeClosure();
            closure = new PMAP(one, two);
        }
        return closure;
    }

    boolean implies(PMAP p) {
        MAP one = first();
        MAP two = second();

        MAP pone = p.first();
        MAP ptwo = p.second();

        if (one.implies(pone) && two.implies(ptwo)) {
            return true;
        }

        if (one.implies(ptwo) && two.implies(pone)) {
            return true;
        }
        return false;
    }
}
