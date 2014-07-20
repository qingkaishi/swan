/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.petrinet;

/**
 *
 * @author qingkaishi
 */
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
}
