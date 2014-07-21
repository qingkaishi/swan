/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */

package cn.edu.nju.software.libtransform;

import javato.instrumentor.Visitor;


/**
 *
 * @author ise
 */
public interface TransformTask {
    public String getPhase();
    public String getPhaseName();
    public soot.Transformer getSootTransformer();
    public Visitor getVisitor();
}
