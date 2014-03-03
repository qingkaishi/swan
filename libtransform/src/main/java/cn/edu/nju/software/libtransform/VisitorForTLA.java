/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.edu.nju.software.libtransform;

import javato.instrumentor.Visitor;

/**
 *
 * @author qingkaishi
 */
public class VisitorForTLA extends Visitor{

    public VisitorForTLA(Visitor nextVisitor) {
        super(nextVisitor);
    }
    
}
