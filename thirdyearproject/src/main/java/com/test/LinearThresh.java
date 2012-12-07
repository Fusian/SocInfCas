package com.test;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.hash.TLongHashSet;

import java.util.Iterator;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class LinearThresh {
	LinearThresh () { }
	
	public void infRun (TLongHashSet seeds, String test) {
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
