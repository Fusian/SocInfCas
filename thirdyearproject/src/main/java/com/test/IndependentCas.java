package com.test;

import java.util.Iterator;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.hash.TLongHashSet;

public class IndependentCas {
	private double p = 0.05;
	
	IndependentCas (){}
	
	public void infRun (TLongHashSet seeds, String test) {
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
	}
	
	public TLongHashSet DegDis (String samper, int size) {
		
		TLongHashSet seeds = new TLongHashSet();
		TLongDoubleHashMap deg = new TLongDoubleHashMap();
		TLongIntHashMap sN = new TLongIntHashMap();
		TLongDoubleIterator checker;
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		Iterator<Node> nodeIt;//used to cycle through nodes
		Iterator<Relationship> reli;//cycles through edges
		TLongArrayList nodey = new TLongArrayList();
		Node start;
		Node end;
		Relationship follow;
		Transaction tx;
		nodeIt = graphDB1.getAllNodes().iterator();
		double count = 0;
		int i = 0;
		double biggest = 0;
		long next = 0;
		long current = 0;
		long dest = 0;
		//double p = 0.01;
		double discount = 0;
		double initial = 0;
		boolean stop = false;
		
		nodeIt.next();
		while (nodeIt.hasNext()) {
			start = nodeIt.next();
			count = (Double) start.getProperty("count");
			
			if (nodey.size() == 0) {
				nodey.add(start.getId());
			}
			else {
				i = 0;
				for (i = 0; i < nodey.size(); i++) {
					if (count > (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("count")) {
						nodey.insert(i, start.getId()); 
						stop = true;
						break;
					}
				}
				
				if (stop == false && nodey.size() < 10000) {
					nodey.add (start.getId());
				}
				
				if (nodey.size() > 10000) {
					nodey.removeAt(10000);
				}
				stop = false;
			}
			/*if (count > biggest) {
				next = start.getId();
				biggest = count;
			}*/
			
			tx = graphDB1.beginTx();
			graphDB1.getNodeById(start.getId()).setProperty("sN", 0);
			tx.success();
			tx.finish();
			//deg.put(start.getId(), count);
			///sN.put (start.getId(), 0);
		}
		
		while (seeds.size() < size) {
			//seeds.add (next);
			//deg.remove(next);
			//sN.remove(next);
			seeds.add (nodey.get(0));
			start = graphDB1.getNodeById(nodey.get(0));
			current = start.getId();
			reli = start.getRelationships(Direction.INCOMING).iterator();
			tx = graphDB1.beginTx();
			while (reli.hasNext()) {
				end = reli.next().getOtherNode(start);
				dest = end.getId();
				if (!seeds.contains (dest) && nodey.contains(dest)){
					end.setProperty("sN", (Integer) end.getProperty("sN") + 1);
					initial = (Double) end.getProperty ("count");
					discount = initial - (2*(Integer) end.getProperty("sN")) - ((initial - 1) * p);
					//sN.adjustValue (dest, sN.get(dest)+1);
					//initial = deg.get(start.getId());
					//discount = initial - (2*sN.get(dest)) - ((initial - 1)*p);
					end.setProperty ("count", discount);
					nodey.remove(dest);
					for (i = 0; i < nodey.size(); i++) {
						if (discount > (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("count")) {
							nodey.insert(i, dest); 
							stop = true;
							break;
						}
					}
					
					if (stop == false) {
						nodey.add (end.getId());
						end.setProperty("broke", true);
					}
					//deg.put (dest, discount);
				}
			}
			tx.success();
			tx.finish();
			
			nodey.removeAt(0);
			//biggest = 0;
			/*start = graphDB1.getNodeById(nodey.get(offset));
			current = start.getId();
			reli = start.getRelationships().iterator();
			while (reli.hasNext()) {
				end = reli.next().getOtherNode(start);
				dest = end.getId();
				if (!seeds.contains (end.getId())){
					sN.adjustValue (dest, sN.get(dest)+1);
					initial = deg.get(start.getId());
					discount = initial - (2*sN.get(dest)) - ((initial - 1)*p);
					deg.put (dest, discount);
				}
			}*/
			
			/*checker = deg.iterator();
			checker.advance();
			while (checker.hasNext()) {
				System.out.println ("next");
				if (checker.value() > biggest) {
					biggest = checker.value();
					next = checker.key();
				}
				checker.advance();
			}*/
		}
		graphDB1.shutdown();
		return seeds;
		
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