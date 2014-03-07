/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    public PMAP(MAP p1, MAP p2){
        if(p1!=null && p2!=null){
            pair.add(p1);
            pair.add(p2);
        }else{
            throw new RuntimeException("PMAP error: cannot add null to a PMAP.");
        }
    }
}
