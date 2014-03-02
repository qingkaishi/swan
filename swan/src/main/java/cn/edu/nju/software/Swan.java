package cn.edu.nju.software;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
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

        opt.addOption("r", "record", true, "record an exacution.");
        opt.addOption("R", "replay", true, "reproduce an exacution.");
        opt.addOption("e", "replay-record", true, "replay and record an exacution as a trace.");
        opt.addOption("x", "replay-examine", true, "replay a trace to examine fixes.");
        opt.addOption("g", "generate", true, "generate traces that may expose bugs.");

        opt.addOption("p", "patch", true, "the line number you synchronize your codes, e.g. ClassName:20,ClassName:21.");
        opt.addOption("T", "trace", true, "the input trace.");

        opt.addOption("c", "test-case", true, "your test cases, e.g. \"java -cp libmonitor.jar:other-dependencies -jar xxx.jar args\".");
        opt.addOption("h", "help", false, "print this information.");

        String formatstr = "java [java-options] -jar swan.jar [--help] [--transform <main-class>] [--generate -T] [[--record] [--replay -T] [--replay-record -T -p] [--replay-examine -T -p] <--test-cases args>]";

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
                String[] mainArgs = cl.getOptionValues("t");
                main.invoke(null, (Object) mainArgs);
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

                if (cl.hasOption("r") && cl.hasOption("c")) {
                    Runtime.getRuntime().exec(cl.getOptionValue("c"));
                } else if (cl.hasOption("R") && cl.hasOption("T") && cl.hasOption("c")) {
                    Runtime.getRuntime().exec(cl.getOptionValue("c"));
                } else if (cl.hasOption("e") && cl.hasOption("T") && cl.hasOption("p") && cl.hasOption("c")) {
                    Runtime.getRuntime().exec(cl.getOptionValue("c"));
                } else if (cl.hasOption("x") && cl.hasOption("T") && cl.hasOption("p") && cl.hasOption("c")) {
                    Runtime.getRuntime().exec(cl.getOptionValue("c"));
                } else {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(formatstr, opt);
                    return;
                }
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
