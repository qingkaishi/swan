package cn.edu.nju.software;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Swan {

    public static void main(String[] args) {
        Options opt = new Options();

        opt.addOption("t", "transform", true, "transform the program to an instrumented version.");

        opt.addOption("r", "record", false, "record an exacution.");
        opt.addOption("R", "replay", true, "reproduce an exacution.");
        opt.addOption("e", "replay-record", true, "replay and record an exacution as a trace.");
        opt.addOption("x", "replay-examine", true, "replay a trace to examine fixes.");
        opt.addOption("g", "generate", true, "generate traces that may expose bugs.");

        opt.addOption("p", "patch", true, "the line number you synchronize your codes, e.g. ClassName:20,ClassName:21.");
        opt.addOption("T", "trace", true, "the input trace.");

        opt.addOption("c", "test-case", true, "your test cases, e.g. \"MainClass args\". Please use \"\" to make it as a whole.");
        opt.addOption("P", "class-path", true, "the class path of your SUT.");
        opt.addOption("h", "help", false, "print this information.");

        String formatstr = "java [java-options] -jar swan.jar [--help] [--transform <main-class>] [--generate -T] [[--record] [--replay -T] [--replay-record -T -p] [--replay-examine -T -p] --test-cases <args>] [--class-path <args>]";

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cl = parser.parse(opt, args);
            if (cl.hasOption("h")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(formatstr, "", opt, "");
                return;
            }

            if (cl.hasOption("t")) {
                String appname = "cn.edu.nju.software.libtransform.Transformer";
                Class<?> c = Class.forName(appname);
                Class[] argTypes = new Class[]{String[].class};
                Method main = c.getDeclaredMethod("startTransform", argTypes);

                List<String> mainArgs = new ArrayList<String>();
                mainArgs.add(cl.getOptionValue("t"));
                if (cl.hasOption("P")) {
                    mainArgs.add(cl.getOptionValue("P"));
                }
                String[] argsArray = new String[mainArgs.size()];
                main.invoke(null, (Object) mainArgs.toArray(argsArray));
            } else if (cl.hasOption("g") && cl.hasOption("T")) {
                String appname = "cn.edu.nju.software.libgen.Generator";
                Class<?> c = Class.forName(appname);
                Class[] argTypes = new Class[]{String[].class};
                Method main = c.getDeclaredMethod("startGeneration", argTypes);
                String[] mainArgs = cl.getOptionValues("g");
                main.invoke(null, (Object) mainArgs);
            } else {
                FileOutputStream fos = new FileOutputStream("/tmp/.swan.args");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(args);
                oos.close();

                // check args
                if (cl.hasOption("r") && cl.hasOption("c")) {
                } else if (cl.hasOption("R") && cl.hasOption("T") && cl.hasOption("c")) {
                } else if (cl.hasOption("e") && cl.hasOption("T") && cl.hasOption("p") && cl.hasOption("c")) {
                } else if (cl.hasOption("x") && cl.hasOption("T") && cl.hasOption("p") && cl.hasOption("c")) {
                } else {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(formatstr, opt);
                    return;
                }

                // run test case
                String newClassPath = "./";
                if (cl.hasOption("P")) {
                    newClassPath = cl.getOptionValue("P");
                    if (!newClassPath.startsWith("/")) {
                        String pwd = System.getProperty("user.dir");
                        newClassPath = pwd + "/" + newClassPath;
                    }
                }
                if (!newClassPath.endsWith("/")) {
                    newClassPath = newClassPath + "/";
                }

                URL[] urls = new URL[]{new URL("file://" + newClassPath)};
                URLClassLoader ucl = new URLClassLoader(urls);

                String testcase = cl.getOptionValue("c");
                String[] splits = testcase.split(" ");
                String mainClass = splits[0];
                String[] testargs = new String[splits.length - 1];
                for (int i = 0; i < testargs.length; ++i) {
                    testargs[i] = splits[i + 1];
                }

                String appname = mainClass;
                Class<?> c = ucl.loadClass(appname);
                Class[] argTypes = new Class[]{String[].class};
                Method main = c.getDeclaredMethod("main", argTypes);
                main.invoke(null, (Object) testargs);
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(formatstr, opt);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Placeholder
        System.out.print("");
    }
}
