package com.test;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.hash.TLongHashSet;

public class SeedPicker {
	private double p = 0.05;
	
	SeedPicker (){}
	

	public String newGraph(String samper) {
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook(graphDB1);
		GraphDatabaseService graphDB2;
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase (samper+"Dis");
		registerShutdownHook(graphDB2);
		graphDB2.shutdown();
		BatchInserter graphDB;
		RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		graphDB = BatchInserters.inserter (samper+"Dis");
		String key;
		Iterator<Node> nodeIt;//used to cycle through nodes
		Iterator<String> keys;
		nodeIt = graphDB1.getAllNodes().iterator();
		Iterator<Relationship> reli; 
		Node n;
		Map <String, Object> properties = MapUtil.map();
		Relationship follow;
		
		while (nodeIt.hasNext()) {
			n = nodeIt.next();
			keys = n.getPropertyKeys().iterator();
			
			while (keys.hasNext()) {
				key = keys.next();
				properties.put(key, n.getProperty(key));
			}
			
			if (n.getId() == 0) {
				graphDB.setNodeProperties(0, properties);
				properties.clear();
			}
			else {
				graphDB.createNode(n.getId(), properties);
				properties.clear();
			}
			
		}
		
		nodeIt = graphDB1.getAllNodes().iterator();
		
		while (nodeIt.hasNext()) {
			n = nodeIt.next();
			reli = n.getRelationships(Direction.OUTGOING).iterator();
			
			while (reli.hasNext()) {
				follow = reli.next();
				properties.put ("weight", follow.getProperty("weight"));
				graphDB.createRelationship(follow.getStartNode().getId(), follow.getEndNode().getId(), mailed, properties);
			}
	
		}
		graphDB1.shutdown();
		graphDB.shutdown();
		return (samper+"Dis");
	}
	public TLongHashSet degDis (String samper, int size, boolean lin) {
		samper = newGraph(samper);
		TLongHashSet seeds = new TLongHashSet();
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook(graphDB1);
		Iterator<Node> nodeIt;//used to cycle through nodes
		Iterator<Relationship> reli;//cycles through edges
		TLongArrayList nodey = new TLongArrayList();
		Node start;
		Node end;
		Transaction tx;
		nodeIt = graphDB1.getAllNodes().iterator();
		double count = 0;
		int i = 0;
		int k = 0;
		long dest = 0;
		//double p = 0.01;
		double discount = 0;
		double initial = 0;
		boolean stop = false;
		double linp = 0;
		nodeIt.next();
		
		System.out.println (lin);
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

			tx = graphDB1.beginTx();
			start.setProperty("oN", 0);
			start.setProperty("iN", 0);
			if (lin == true) {
				start.setProperty("Ocount", count);
			}
			tx.success();
			tx.finish();

		}
		Relationship follow;
		double out;
		
		while (seeds.size() < size) {

			System.out.println ("nodey: " + nodey.size());
			seeds.add (nodey.get(0));
			System.out.println ("Adding:" + nodey.get(0));
			System.out.println ("seedy:" + seeds.size());
			start = graphDB1.getNodeById(nodey.get(0));
			start.getId();
			reli = start.getRelationships().iterator();
			tx = graphDB1.beginTx();
			
			while (reli.hasNext()) {
				follow = reli.next();
				end = follow.getOtherNode(start);
				dest = end.getId();
				
				if (!seeds.contains (dest) && nodey.contains(dest)){
					
					if (follow.getEndNode().getId() == end.getId()) {
						end.setProperty("iN", (Integer) end.getProperty("iN") + 1);
						if (lin == true) {linp = 1/(Double) end.getProperty("Icount");}
					}
					else {
						end.setProperty("oN", (Integer) end.getProperty("oN") + 1);
						if (lin == true) {linp = 1/(Double) start.getProperty("Icount");}
					}
					
					initial = (Double) end.getProperty ("count");
					out = (Double) end.getProperty ("count") - (Integer)end.getProperty("oN");
					
					if (lin == false) {
						discount = out - ((Integer) end.getProperty ("iN")) - (out*p);
					}
					else {
						discount = out - ((Integer) end.getProperty ("iN")) - (out*linp);
					}
					System.out.println ("Discounting " + end.getId() + " from " +initial+ " to " + discount);
					end.setProperty ("count", discount);
					nodey.remove(dest);
					stop = false;
					
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

				}
			}
			tx.success();
			tx.finish();
			
			nodey.removeAt(0);
		}
		graphDB1.shutdown();
		deleteFileOrDirectory(samper);
		return seeds;
		
	}
	public TLongHashSet randSeed (String samper, int size) {
		TLongHashSet seeds = new TLongHashSet();
		
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
			
		Random rand = new Random();
		long pick;
		long bound = (Long) graphDB1.getReferenceNode().getProperty("bound");
		while (seeds.size()<size){
			try{
				pick = rand.nextInt ((int)bound+1);
				System.out.println ("picked:" + pick);
				System.out.println (seeds.size());
				if (graphDB1.getNodeById(pick) != null && pick !=0) {
					seeds.add(pick);
					System.out.println (seeds.size());
				}
			}
			catch (Exception e) {
				
			}
		}
		graphDB1.shutdown();
		return seeds;
	}
	public TLongHashSet randSeedMulti (String samper, int size, int colNum) {
		TLongHashSet seeds = new TLongHashSet();
		
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		Iterator<Node> nodeIt = graphDB1.getAllNodes().iterator();
		
		Random rand = new Random();
		nodeIt.next();
		while (nodeIt.hasNext()){
			nodeIt.next().setProperty("colour", rand.nextInt(colNum));
		}
		graphDB1.shutdown();
		return seeds;
	}
	public TLongHashSet degMaxMin (String samper, int size, boolean max) {
		TLongHashSet seeds = new TLongHashSet();
		//samper = newGraph(samper);
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook(graphDB1);
		Iterator<Node> nodeIt;//used to cycle through nodes
		TLongArrayList nodey = new TLongArrayList();
		Node start;
		nodeIt = graphDB1.getAllNodes().iterator();
		double count = 0;
		int i = 0;
		boolean stop = false;
		
		while (nodeIt.hasNext()) {
			start = nodeIt.next();
			count = (Double) start.getProperty("count");
			
			if (nodey.size() == 0) {
				nodey.add(start.getId());
			}
			else {
				i = 0;
				for (i = 0; i < nodey.size(); i++) {
					if ((count > (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("count") && max == true)||
							count < (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("count") && max == false) {
						nodey.insert(i, start.getId()); 
						stop = true;
						break;
					}
				}
				
				if (stop == false && nodey.size() < size) {
					nodey.add (start.getId());
				}
				
				if (nodey.size() > size) {
					nodey.removeAt(size);
				}
				stop = false;
			}
		}
		
		seeds.addAll(nodey);
		graphDB1.shutdown();
		return seeds;
	}
	
	public static void deleteFileOrDirectory( String s ) {
	   final File file = new File(s);
		if ( file.exists() ) {
	        if ( file.isDirectory() ) {
	            for ( File child : file.listFiles() ) {
	                deleteFileOrDirectory( child.getPath() );
	            }
	        }
	        file.delete();
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