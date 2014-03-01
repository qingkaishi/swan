package edu.hkust.leap;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LeapTransformerMain {
	private static String trans_classname = "edu.hkust.leap.transformer.LEAPTransform";

	public static void main(String[] args) {
		args = new String[] { "example.Example" };
		if (args.length == 0) {
			System.err.println("please specify the main class ... ");
		} else {
			transform(args);
		}

	}

	private static void transform(String[] args) {
		run(args);
	}

	private static void run(String[] args) {
		try {
			Class<?> c = Class.forName(trans_classname);
			Class[] argTypes = new Class[] { String[].class };
			Method main = c.getDeclaredMethod("main", argTypes);
			String[] mainArgs = Arrays.copyOfRange(args, 0, args.length);
			main.invoke(null, (Object) mainArgs);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
