package com.test;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class InfSpread {
	private double p = 0.01;
	
	InfSpread () { }
	
	public int linRun (TLongHashSet seeds, String test) {
		boolean stop = false;
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		registerShutdownHook( graphDB1 );
		
		TLongIterator seedIt;
		Iterator<Relationship> reli;//cycles through edges
		Iterator<Relationship> infCheck;
		Node tempNode;
		Node otherNode;
		Node checkNodes;
		Relationship follow;
		Relationship follow2;
		double total = 0;
		TLongHashSet infNodes = new TLongHashSet();
		//graphDB1.beginTx();
		
		
		//assign nodes and edges uniform weights.
		
		//index seed nodes.
		
		//check neighbours of every seeded node. 
		
		//check every edge of neighbour add together active neighbour edges
		while (stop == false) {
			stop = true;
			seedIt = seeds.iterator();
			
			while (seedIt.hasNext()) {
				tempNode = graphDB1.getNodeById(seedIt.next());
				System.out.println (tempNode.getId() + " is a seed node and is having it's neighbours tested");
				reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
				while (reli.hasNext()) {
					follow = reli.next();
					otherNode = follow.getOtherNode(tempNode);
					System.out.println ("checking neighbour " + otherNode.getId());
					if (!seeds.contains(otherNode.getId()) && !infNodes.contains(otherNode.getId())){
						infCheck = otherNode.getRelationships(Direction.INCOMING).iterator();
						total = 0;
						while (infCheck.hasNext()) {
							follow2 = infCheck.next();
							checkNodes = follow2.getOtherNode(otherNode);
							if (seeds.contains(checkNodes.getId())) {
								total = total + (Double)follow2.getProperty("weight");
							}
						}
						if (total >= (Double)otherNode.getProperty("weight")) {
							System.out.println ("adding new node " + otherNode.getId());
							stop = false;
							infNodes.add(otherNode.getId());
						}
					}
				}
			}
			
			System.out.println ("adding new nodes");
			seeds.addAll(infNodes);
			System.out.println ("THESE NODES ARE BEING ADDED");
			System.out.println (infNodes.toString());
			System.out.println (seeds.size());
			
			TLongIterator sedt = seeds.iterator();
			
			while (sedt.hasNext()) {
				System.out.println (sedt.next());
			}
			infNodes.clear();
		}
		graphDB1.shutdown();
		return seeds.size();
	}
	public int indCas (TLongHashSet seeds, String test) {
		//double prob = 0.05;
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		boolean stop = false;
		
		TLongIterator seedIt;
		Iterator<Relationship> reli;//cycles through edges
		TLongHashSet infNodes = new TLongHashSet();
		
		Node tempNode;
		Node otherNode;
		Relationship follow;
		Random rand = new Random();
		
		while (stop == false) {
			stop = true;
			seedIt = seeds.iterator();
			
			while (seedIt.hasNext()) {
				tempNode = graphDB1.getNodeById(seedIt.next());
				System.out.println (tempNode.getId() + " is a seed node and is having it's neighbours tested");
				reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
				
				while (reli.hasNext()) {
					follow = reli.next();
					otherNode = follow.getOtherNode(tempNode);
					if (!seeds.contains(otherNode.getId())&& !infNodes.contains(otherNode.getId())){
						if (p > rand.nextDouble()) {
							infNodes.add(otherNode.getId());
							stop = false;
						}
					}
				}
			}
			
			System.out.println ("adding new nodes");
			seeds.addAll(infNodes);
			System.out.println ("THESE NODES ARE BEING ADDED");
			System.out.println (infNodes.toString());
			System.out.println (seeds.size());
			infNodes.clear();
			
		}
		graphDB1.shutdown();
		return seeds.size();
	}
	
	public int multiLin (ArrayList<TLongHashSet> seeds, String test) {
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		registerShutdownHook( graphDB1 );
		
		double al = 0.99;
		double tf = 0.1;
		double err = 0.00000000000000000002;
		double colNum = seeds.size();
		double W = 0;
		double tot = 0;
		double nodeC = 0;
		double Wmin = 10000;
		double be = 0.8;
		double bound = 0;
		double consta = be/colNum;
		double[] prob = new double[(int) colNum];
		double[] globProb = new double[(int) colNum];
		int j = 0;
		
		
		Random rand = new Random();
		Iterator<Node> allNod = graphDB1.getAllNodes().iterator();
		Iterator<Relationship> allRel;
		Node n = null;
		Node m = null;
		Relationship follow;
		allNod.next();

		while (allNod.hasNext()) {
			n = allNod.next();
			nodeC++;
			allRel = n.getRelationships().iterator();
			while (allRel.hasNext()) {
				tot = tot + (Double) allRel.next().getProperty("weight");
			}
			W = W + tot;
			n.setProperty("wi", tot);
			if (tot < Wmin) {Wmin = tot;}
			tot = 0;
		}
		
		Double I = ((W/(Wmin*be)) * (Math.log(nodeC/err))) + 5000;
		
		for (int i = 0; i < I; i++) {
			bound = rand.nextDouble();
			tot = 0;
			allNod = graphDB1.getAllNodes().iterator();
			allNod.next();
			while (tot < bound) {
				n = allNod.next();
				tot = tot + (((Double)n.getProperty("wi"))/W);
			}
	
			allRel = n.getRelationships().iterator();
			while (allRel.hasNext()) {
				follow = allRel.next();
				j = (Integer)follow.getOtherNode(n).getProperty("colour");
				prob[j] = prob[j] + (Double)follow.getProperty("weight");
			}
			
			for (j = 0; j< colNum; j++) {
				prob[j] = consta + ((1-be)*(prob[j]/(Double)n.getProperty("wi")));
			}
			
			bound = rand.nextDouble();
			tot = 0;
			j = 0;
			while (tot < bound) {
				tot = tot + prob[j];
				j++;
			}
			n.setProperty("colour", j);
			
			if (I - i < 5000) {
				globProb[j]++;
			} 
		}
		int k = 0;
		for (int i =0; i<colNum; i++) {
			if (globProb[i] > globProb[k]) {
				k = i;
			}
		}
		allNod = graphDB1.getAllNodes().iterator();
		allNod.next();
		while (allNod.hasNext()) {
			allNod.next().setProperty("colour", k);
		}
		
		double titer = 1;
		for (int i =0; i<prob.length; i++){
			prob[i] = 0;
		}
		
		while (titer > tf) {
			allNod = graphDB1.getAllNodes().iterator();
			allNod.next();
			while (allNod.hasNext()){
				n = allNod.next();
				
				allRel = n.getRelationships().iterator();
				while (allRel.hasNext()) {
					follow = allRel.next();
					j = (Integer)follow.getOtherNode(n).getProperty("colour");
					prob[j] = prob[j] + (Double)follow.getProperty("weight");
				}
				
				for (j = 0; j< colNum; j++) {
					prob[j] = consta + ((1-be)*(prob[j]/(Double)n.getProperty("wi")));
				}
				
				bound = rand.nextDouble();
				tot = 0;
				j = 0;
				while (tot < bound) {
					tot = tot + prob[j];
					j++;
				}
				n.setProperty("colour", j);
			}
			titer = al * titer;
		}
		return seeds.size();
	}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
}
