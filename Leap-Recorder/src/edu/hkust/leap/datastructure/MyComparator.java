package edu.hkust.leap.datastructure;

import java.util.Comparator;

public class MyComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		return ((String) o1).compareTo( (String)o2);
	}

}
