package edu.hkust.leap.transformer.loop;

public class LoopClassTest {
	private int count=0;
	public static void main(String[] args)
	{
		LoopClassTest lc = new LoopClassTest();
		lc.method1();
		lc.method2(100);
	}
	public void method1()
	{
		int k=0;
		for (int i=1;i<100;i++)
		{
			k++;
		}
	}
	public void method2(int num)
	{
		while(num>0)
		{
			for(int i=0;i<num;i++)
			{
				count++;
			}
			num--;
		}
		for (int i=1;i<100;i++)
		{
			num++;
		}
	}
}
