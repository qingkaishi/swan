package cn.edu.nju.swan;

import cn.edu.nju.swan.transformer.LEAPTransform;

public class SwanTransformerMain {

	public static void main(String[] args) {
//		args = new String[] { "example.Example" };
		if (args.length == 0) {
			System.err.println("please specify the main class ... ");
		} else {
			LEAPTransform.main(args);
		}
	}

}
