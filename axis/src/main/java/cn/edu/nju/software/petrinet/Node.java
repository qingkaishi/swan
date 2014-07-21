/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.petrinet;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qingkaishi
 */
public abstract class Node {

    private List<Node> nexts = new ArrayList<Node>();

    public List<Node> nexts() {
        return nexts;
    }

    public void addNext(Node n) {
        if (this instanceof Place && n instanceof Transition) {
            nexts.add(n);
        } else if (this instanceof Transition && n instanceof Place) {
            nexts.add(n);
        } else {
            throw new RuntimeException("insertion exception!");
        }
    }
}
