/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.libtransform.patch;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qingkaishi
 */
public class Patch {

    private List<Integer> patchedlines = new ArrayList<Integer>();

    private static Patch p = new Patch();

    public static Patch v() {
        return p;
    }

    // class:line,class:line,...
    public void parse(String val) {
        String[] units = val.split(",");
        for(String u : units){
            String[] parts = u.split(":");
            if(parts.length == 2) {
                String linestr = parts[1];
                try {
                    int lineno = Integer.parseInt(linestr);
                    patchedlines.add(lineno);
                } catch (Exception e) {
                    System.err.println("The option value is invalid: " + val + ".");
                    System.exit(1);
                }
            }
        }
    }

    public boolean contains(int i) {
        return patchedlines.contains(i);
    }
    
    public boolean isEmpty(){
        return patchedlines.isEmpty();
    }
}
