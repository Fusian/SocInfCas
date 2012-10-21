package com.test;

public class stantest {
	public static void main(String[] args)
	{
		System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		StanfordParser parser = new StanfordParser("C:/Users/Fusian/Downloads/Email-EuAll.txt");
		parser.Parse();
		System.out.println(parser.getComments());
		//System.out.println(parser.getDataSet().get(1)[0]);
		
		//Graph<Long, Long> g = parser.toGraph();
		//System.out.println("We have " + g.getEdgeCount() + " edges");
		
		
	}
}
