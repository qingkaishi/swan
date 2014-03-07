/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.nju.software.libgen;

import java.io.File;

/**
 *
 * @author qingkaishi
 */
public class Generator {

    public static void startGeneration(String[] args) {
        if(args.length < 1){
            throw new RuntimeException("No trace file to re-generate.");
        }
        
        String filename = args[0];
        File f = new File(filename);
        if (f.exists() && !f.isDirectory()) {
            // ...
        } else {
            throw new RuntimeException("Trace file error: " + f.getAbsolutePath() + ".");
        }
    }
}
