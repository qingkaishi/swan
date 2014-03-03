package javato.instrumentor;

import cn.edu.nju.software.libtransform.TransformTask;
import org.objectweb.asm.ClassReader;
import soot.Scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import soot.PackManager;
import soot.PhaseOptions;
import soot.SootClass;
import soot.Transform;
import soot.options.Options;

/**
 * Copyright (c) 2007-2008, Koushik Sen <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
public class TransformClass {

    private String[] argl;
    private String[] excludes;
    private Visitor visitor;

    private void processAll(File f) throws IOException {
        if (f.isFile()) {
            if (f.getName().endsWith(".class")) {
                processClass(f);
            } else if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip")) {
                processArchive(f);
            }
        } else if (f.isDirectory()) {
            File[] list = f.listFiles();
            for (File aList : list) {
                processAll(aList);
            }
        }
    }

    private void processArchive(File jar) throws IOException {
        ZipFile f = new ZipFile(jar.getName());
        Enumeration<? extends ZipEntry> en = f.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".class")) {
                ClassReader cr = new ClassReader(f.getInputStream(e));
                processClass(cr.getClassName());
            }
        }

    }

    private void processClass(String className) {
        System.out.println("className = " + className);
        String fullClassName = className.replace('/', '.');
        for (String exclude : excludes) {
            if (fullClassName.startsWith(exclude)) {
                return;
            }
        }
        argl[argl.length - 1] = fullClassName;
        processAllAtOnce(argl, visitor);
    }

    private void processClass(File f) throws IOException {
        ClassReader cr = new ClassReader(new FileInputStream(f));
        processClass(cr.getClassName());
    }

    public void processAllOneByOne(String args[], Visitor visitor) {
        this.visitor = visitor;
        //TransformerForInstrumentation.init(visitor);
        ArrayList<String> argl = new ArrayList<String>();
        ArrayList<String> excludes = new ArrayList<String>();
        ArrayList<String> includes = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ex")) {
                excludes.add(args[i + 1]);
                System.out.println("ex args[i+1] = " + args[i + 1]);
                i++;
            } else if (args[i].equals("-in")) {
                includes.add(args[i + 1]);
                System.out.println("in args[i+1] = " + args[i + 1]);
                i++;
            } else {
                argl.add(args[i]);
            }
        }
        argl.add("dummy");

        this.argl = new String[argl.size()];
        int i = 0;
        for (String s : argl) {
            this.argl[i] = s;
            i++;
        }
        this.excludes = new String[excludes.size()];
        i = 0;
        for (String s : excludes) {
            this.excludes[i] = s;
            i++;
        }

        for (String dir : includes) {
            System.out.println("dir = " + dir);
            try {
                processAll(new File(dir));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void processAllAtOnce(String[] args, Visitor visitor) {
        Scene.v().setSootClassPath(System.getProperty("sun.boot.class.path")
                + File.pathSeparator + System.getProperty("java.class.path"));
        Scene.v().loadClassAndSupport(visitor.observerClass);
        TransformerForInstrumentation.v().setVisitor(visitor);
        PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter", TransformerForInstrumentation.v()));
        soot.Main.main(args);
        soot.G.reset();
    }

    // HACKED by ise
    public void processAllAtOnce(String[] args, List<TransformTask> transformers) {
        String mainClass = args[0];
        Visitor.mainClass = mainClass;
        setRecordOptions();

        Scene.v().setSootClassPath(
                System.getProperty("sun.boot.class.path") + File.pathSeparator
                + System.getProperty("java.class.path") + File.pathSeparator + args[1]);
        SootClass appclass = Scene.v().loadClassAndSupport(mainClass);
        Scene.v().setMainClass(appclass);

        for (TransformTask t : transformers) {
            PackManager.v().getPack(t.getPhase()).add(new Transform(t.getPhaseName(), t.getSootTransformer()));
            if (t.getVisitor().observerClass != null) {
                Scene.v().loadClassAndSupport(t.getVisitor().observerClass);
            }
        }

        String path = "./transformed_version/";
        String[] args_soot = {"-cp", ".", "-pp", "-validate", mainClass, "-d",
            path, "-f", "jimple", "-x", "jrockit.", "-x", "edu.", "-x",
            "com.", "-x", "checkers.", "-x", "org.xmlpull.", "-x",
            "org.apache.xml.", "-x", "org.apache.xpath.", "-x", "cn.edu.nju."};
        soot.Main.main(args_soot);
        soot.G.reset();
    }

    // HACKED by ise
    private void setRecordOptions() {
        PhaseOptions.v().setPhaseOption("jb", "enabled:true");
        Options.v().set_keep_line_number(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_whole_program(true);
        Options.v().set_app(true);

        // Enable Spark
        HashMap<String, String> opt = new HashMap<String, String>();
        opt.put("propagator", "worklist");
        opt.put("simple-edges-bidirectional", "false");
        opt.put("on-fly-cg", "true");
        opt.put("set-impl", "double");
        opt.put("double-set-old", "hybrid");
        opt.put("double-set-new", "hybrid");
        opt.put("pre_jimplify", "true");
        //SparkTransformer.v().transform("", opt);
        PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");

        /*PackManager
         .v()
         .getPack("wjtp")
         .add(new Transform("wjtp.transformer1",
         new WholeProgramTransformer()));
         PackManager.v().getPack("jtp")
         .add(new Transform("jtp.transformer2", new JTPTransformer()));*/
    }
}
