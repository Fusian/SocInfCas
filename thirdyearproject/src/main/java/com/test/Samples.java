package com.test;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.hash.TLongHashSet;
import gnu.trove.stack.array.TLongArrayStack;

import java.util.ArrayList;
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
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;


public class Samples {//some declarations...
	GraphDatabaseService graphDB1;
	GraphDatabaseService graphDB2;
	BatchInserter graphDB;//open original database, set up insert for new one
	Node [] toandfrom;//holds start and dest node for current edge
	Node tempNode = null;//used to hold a node at various points
	TLongHashSet samp = new TLongHashSet();
	ArrayList<Long> relations = new ArrayList<Long>();//holds relationship ids of sample
	ArrayList<Relationship> tempRels = new ArrayList <Relationship>();//holds edges temporarily
	ArrayList<Relationship> tempRels2 = new ArrayList <Relationship>();//holds edges of other node temporarily
	Iterable<Node> allNode;//iterator for all nodes
	Iterator<Node> nodeIt;//used to cycle through nodes
	Iterable<Relationship> allRel;//it for edges
	Iterator<Relationship> reli;//cycles through edges
	Relationship follow = null;
	Relationship follow2 = null;//these hold relationships to be followed
	Random rand = new Random();//gens random numbers
	//int j = 0;
	int j2 = 0;
	int k = 0;
	int k2 = 0;
	int i = 0;
	//int i2 = 0;//loop counters and temp value stores
	double p;//random number generated
	
	Samples () { }
	
	public void getSamp (String origin, String destin, int size) {
		long bound=0;
		graphDB1 = new EmbeddedGraphDatabase (origin);
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase (destin);
		registerShutdownHook( graphDB2 );//helps in safe shut down
		registerShutdownHook (graphDB1);
		graphDB2.shutdown();
		RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		graphDB = BatchInserters.inserter (destin);
		//above sets up databases and batch inserter
		ArrayList<Long> order = new ArrayList<Long>();
		ArrayList<Long> star = new ArrayList<Long>();
		ArrayList<Long> fini = new ArrayList<Long>();
		
		bound = (Long) graphDB1.getReferenceNode().getProperty("bound");
		j2 = (int)bound + 1;
		boolean found = false;
		
		while (found == false) {
			try {
				j2 = rand.nextInt((int)bound);//pick starting point
				//j2++;
				tempNode = graphDB1.getNodeById(j2);
				found = true;
			}
			catch (Exception e) {
				
			}
		}
		//tempNode = graphDB1.getNodeById(j2);
		 
		Node xprime;//used in random walk
		Node yprime = null;//see where we came from
		Node secondchoice;//used in certain cases of walk
		
		xprime = tempNode;
		yprime = xprime;
		samp.add(xprime.getId());//initlise and add start node to sample
		order.add(xprime.getId());
		//int count = (Integer)xprime.getProperty("count");
		Map <String, Object> properties = MapUtil.map("weight", rand.nextDouble(), "count", xprime.getProperty("count"));
		graphDB.createNode(xprime.getId(),properties);//creates and adds node to sample database
		
		while (samp.size() < size){//loop through sample till we get to size wanted
			//j=0;
			j2=0;
			k=0;
			k2=0;
			//i2=0;//reset temp value stores
			
			allRel = xprime.getRelationships();
			reli = null;
			System.gc();
			reli = allRel.iterator();//gets iterator for node relationships
			
			while (reli.hasNext() == true) {//adds relationships to temp store
				tempRels.add(reli.next());
				System.out.println ("edges: " + tempRels.size());
			}
		
			j2 = rand.nextInt(tempRels.size());//choose random edge
			System.out.println ("picked edge: " + j2);
			follow = tempRels.get(j2);//gets edge chosen
		
			tempNode = follow.getOtherNode(xprime);//get other node
			
			allRel = tempNode.getRelationships();
			reli = null;
			reli = allRel.iterator();//find edges for second node
			
			while (reli.hasNext() == true) {
				tempRels2.add(reli.next());
			}//temp store for second nodes edges
			
			float diff = tempRels.size();
			float diff2 = tempRels2.size();
			k = tempRels2.size();
			diff = diff/diff2;//divide to get value used for comparison
			p = rand.nextDouble();//get random number
			System.out.println ("j/k count: " + diff);
			System.out.println ("Node 1: " + xprime.getId());
			System.out.println ("Node " + tempNode.getId());
			
			if (p <= Math.min(1, diff)) {//compare values
			
				if (yprime == tempNode && tempRels.size() > 1) {
					p = rand.nextDouble();//new value
					k2 = j2;//initilise k2
					while (k2 !=j2) {//get different random edge
						k2 = rand.nextInt (tempRels.size());
					}
					
					tempRels.get(k2);//get next edge
						
					secondchoice = follow.getOtherNode(xprime);//get other prime
						
					allRel = secondchoice.getRelationships();
					reli = allRel.iterator();
					tempRels2.clear();//clear one temp store for next node
					
					while (reli.hasNext() == true) {//store edges
						tempRels2.add(reli.next());
					}
					
					float dif1 = (float)tempRels.size()/(float)tempRels2.size();
					dif1 = dif1 * dif1;
					dif1 = Math.min (1, dif1);
					float dif2 = (float)k/(float)tempRels.size();
					dif2 = dif2 * dif2;
					dif2 = Math.max (1, dif2);
					dif1 = dif1 * dif2;//work out comparison value
					
					if (p <= Math.min(1, dif1)) {//new comparison
						yprime = xprime;//set old node to current node
						xprime = secondchoice;//set new node
						if (!samp.contains(xprime.getId())) {//check if node already in sample and add it if not
							samp.add(xprime.getId());
							properties = MapUtil.map("weight", rand.nextDouble(), "count", xprime.getProperty("count"));
							graphDB.createNode(xprime.getId(),properties);
							order.add(xprime.getId());
						}
						if (!relations.contains(follow2.getId())){//check if edge in sample already and add if not
							toandfrom = null;
							toandfrom = follow2.getNodes();
							properties = MapUtil.map("weight", rand.nextDouble());
							graphDB.createRelationship(toandfrom[0].getId(), toandfrom[1].getId(), mailed, properties );
							relations.add(follow2.getId());
							star.add(toandfrom[0].getId());
							fini.add(toandfrom[1].getId());
						}
					}
					else {//comparison failed, take go to tempnode instead
						yprime = xprime;
						xprime = tempNode;//set new node
						if (!samp.contains(xprime.getId())) {//check if node already in sample and add it if not
							samp.add(xprime.getId());
							properties = MapUtil.map("weight", rand.nextDouble(), "count", xprime.getProperty("count"));
							graphDB.createNode(xprime.getId(),properties);
							order.add(xprime.getId());
						}
						if (!relations.contains(follow.getId())){//node in edge check
							toandfrom = null;
							toandfrom = follow.getNodes();
							properties = MapUtil.map("weight", rand.nextDouble());
							graphDB.createRelationship(toandfrom[0].getId(), toandfrom[1].getId(), mailed, properties);
							relations.add(follow.getId());
							star.add(toandfrom[0].getId());
							fini.add(toandfrom[1].getId());
						}
					}
				}
				else {//add new node and follow to it
					//System.out.println ("failed second if");
					//System.out.println ("xprime 1:" + xprime.getProperty("id"));
					//System.out.println ("yprime 1:" + yprime.getProperty("id"));
					yprime = xprime;
					xprime = tempNode;//updatenodes
					//System.out.println ("xprime 2:" + xprime.getProperty("id"));
					//System.out.println ("yprime 2:" + yprime.getProperty("id"));
					if (!samp.contains(xprime.getId())) {//check if node already in sample and add it if not
						samp.add(xprime.getId());
						properties = MapUtil.map("weight", rand.nextDouble(), "count", xprime.getProperty("count"));
						graphDB.createNode(xprime.getId(),properties);
						order.add(xprime.getId());
					}
					if (!relations.contains(follow.getId()) && (xprime != yprime)){//edge check
						toandfrom = null;
						toandfrom = follow.getNodes();
						properties = MapUtil.map("weight", rand.nextDouble());
						graphDB.createRelationship(toandfrom[0].getId(), toandfrom[1].getId(), mailed, properties);
						relations.add(follow.getId());
						star.add(toandfrom[0].getId());
						fini.add(toandfrom[1].getId());
					}
				}
			}
			else {//add node but don't follow
				if (!samp.contains(tempNode.getId())) {//node check
					samp.add(tempNode.getId());
					properties = MapUtil.map("weight", rand.nextDouble(), "count", tempNode.getProperty("count"));
					graphDB.createNode (tempNode.getId(),properties);
					order.add(tempNode.getId());
				}
				if (!relations.contains(follow.getId())){//edge check
					toandfrom = null;
					toandfrom = follow.getNodes();
					properties = MapUtil.map("weight", rand.nextDouble());
					graphDB.createRelationship(toandfrom[0].getId(), toandfrom[1].getId(), mailed, properties);
					relations.add(follow.getId());
					star.add(toandfrom[0].getId());
					fini.add(toandfrom[1].getId());
				}
			}
			System.out.println ("samp size=================" + samp.size());
			tempRels.clear();
			tempRels2.clear();
		}
		graphDB1.shutdown();
		graphDB.shutdown();
		
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase (destin);
		Transaction tx = graphDB2.beginTx();
		graphDB2.getReferenceNode().setProperty("bound", bound);
		tx.success();
		tx.finish();
		graphDB2.shutdown();
		samp.clear();
		
		Iterator<Long> nod = order.iterator();
		Iterator<Long> edg = star.iterator();
		Iterator<Long> edg2 = fini.iterator();
		
		System.out.println("Nodes");
		while (nod.hasNext()) {
			System.out.println (nod.next());
		}
		
		while (edg.hasNext()) {
			System.out.println ("edges");
			System.out.println (edg.next());
			System.out.println (edg2.next());
		}
	}
	
	public void getSnow (String origin, String destin, int size) {
		long bound=0;
		graphDB1 = new EmbeddedGraphDatabase (origin);
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase (destin);
		registerShutdownHook( graphDB2 );//helps in safe shut down
		registerShutdownHook (graphDB1);
		graphDB2.shutdown();
		RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		graphDB = BatchInserters.inserter (destin);
		//above sets up databases and batch inserter
		Iterator<Long> seedIt;
		TLongIterator seedIt2;
		Node xprime=null;//used in random walk
		new TLongArrayStack();
		ArrayList<Long> destNodes = new ArrayList<Long>();
		ArrayList<Long> picks = new ArrayList<Long>();
		ArrayList<Long> lost = new ArrayList<Long>();
		ArrayList<Long> order = new ArrayList<Long>();
		bound = (Long) graphDB1.getReferenceNode().getProperty("bound");
		j2 = (int)bound + 1;
		boolean found = false;
		
		while (found == false) {
			try {
				j2 = rand.nextInt((int)bound);//pick starting point
				System.out.println (j2);
				//j2++;
				xprime = graphDB1.getNodeById(j2);
				found = true;
			}
			catch (Exception e) {
				
			}
		}
		
		samp.add(xprime.getId());//initlise and add start node to sample

		destNodes.add(xprime.getId());
		order.add(xprime.getId());
		Map <String, Object> properties  = MapUtil.map("weight", rand.nextDouble(), "count", xprime.getProperty("count"));
		graphDB.createNode(xprime.getId(),properties);//creates and adds node to sample database
		long newNode = 0;
		while (samp.size() < size){//loop through sample till we get to size wanted
			seedIt = destNodes.iterator();
			while (seedIt.hasNext()) {
				System.out.println ("seed");
				xprime = graphDB1.getNodeById(seedIt.next());
				System.out.println (xprime.getId());
				reli = xprime.getRelationships().iterator();
				//reli = null;
				System.gc();
				//reli = allRel.iterator();//gets iterator for node relationships
				System.out.println ("herey");
				
				while (reli.hasNext()) {//adds relationships to temp store
					System.out.println("relli");
					follow = reli.next();
					newNode = follow.getOtherNode(xprime).getId();
				
					if (!samp.contains(newNode)){
						if (!picks.contains(newNode)){
							picks.add(newNode);
						}
					}
				}
				
			}
			
			//System.out.println ("idontevenknow");
			//seedIt = destNodes.iterator();
			int ind = 0;
			while (samp.size() + picks.size() > size) {
				System.out.println("sizey");
				ind = rand.nextInt(picks.size());
				//destNodes.remove(picks.get(rand.nextInt(ind)));
				lost.add(picks.get(ind));
				picks.remove(ind);
			}
			samp.addAll(picks);
			seedIt = picks.iterator();
			long seedy;
			while (seedIt.hasNext()) {
				seedy = seedIt.next();
				properties  = MapUtil.map("weight", rand.nextDouble(), "count", graphDB1.getNodeById(seedy).getProperty("count"));
				graphDB.createNode(seedy, properties);
				order.add(seedy);
			}
			destNodes.clear();
			destNodes.addAll(picks);
			picks.clear();
		}
		
		seedIt2 = samp.iterator();
		
		while (seedIt2.hasNext()) {
			xprime = graphDB1.getNodeById(seedIt2.next());
			System.out.println (xprime.getId());
			reli = xprime.getRelationships(Direction.OUTGOING).iterator();
			//reli = null;
			System.gc();
			
			while (reli.hasNext()) {//adds relationships to temp store
				System.out.println("relli");
				follow = reli.next();
				newNode = follow.getOtherNode(xprime).getId();
			
				if (samp.contains(newNode)){
					properties = MapUtil.map("weight", rand.nextDouble());
					graphDB.createRelationship(follow.getStartNode().getId(), follow.getEndNode().getId(), mailed, properties);
				}
			}
		}
		samp.clear();
		destNodes.clear();
		System.gc();
		graphDB.shutdown();
		graphDB1.shutdown();
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase (destin);
		Transaction tx = graphDB2.beginTx();
		graphDB2.getReferenceNode().setProperty("bound", bound);
		tx.success();
		tx.finish();
		graphDB2.shutdown();
		
		Iterator<Node> testse;
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase (destin);
		testse = graphDB2.getAllNodes().iterator();
		Iterator<Relationship> testre;
		Node cur;
		testse.next();
		Iterator<Long> ff = order.iterator();
		System.out.println("order of nodes");
		while (ff.hasNext()){
			System.out.println(ff.next());
		}
		System.out.println ("Relationships");
		while (testse.hasNext()){
			cur = testse.next();
			System.out.println (cur.getId());
			testre = cur.getRelationships(Direction.OUTGOING).iterator();
			
			while (testre.hasNext()) {
				System.out.println (testre.next().getOtherNode(cur));
			}
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
