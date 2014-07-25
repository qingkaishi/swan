/*
 * 
 * Developed by Qingkai Shi
 * Copy Right by the State Key Lab for Novel Software Tech., Nanjing University.  
 */
package cn.edu.nju.software.petrinet;

public class Place extends Node {

    private boolean token = false;
    private Object content = null;

    public Place(Object cont) {
        token = false;
        content = cont;
    }

    public Place(Object cont, boolean t) {
        token = t;
        content = cont;
    }

    public boolean hasToken() {
        return token;
    }

    public Object getContent() {
        return content;
    }
    
    public void setToken(){
        token = true;
    }
}
