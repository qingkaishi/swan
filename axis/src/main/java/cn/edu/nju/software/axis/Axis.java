package cn.edu.nju.software.axis;

import cn.edu.nju.software.libevent.SwanEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

/**
 * Hello world!
 *
 */
public class Axis {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("No trace file to re-generate.");
        }

        String filename = args[0];
        File f = new File(filename);
        if (f.exists() && !f.isDirectory()) {
            // ...
            try {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Vector<SwanEvent> trace = (Vector<SwanEvent>) ois.readObject();
                ois.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        } else {
            throw new RuntimeException("Trace file error: " + f.getAbsolutePath() + ".");
        }
    }
}
