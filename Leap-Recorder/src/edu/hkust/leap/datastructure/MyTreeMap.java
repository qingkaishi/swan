package edu.hkust.leap.datastructure;

import java.util.TreeMap;

public class MyTreeMap extends TreeMap{
public MyTreeMap(MyComparator myComparator) {
		super(myComparator);
	}
}
