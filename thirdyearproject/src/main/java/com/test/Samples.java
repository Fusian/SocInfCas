package com.test;
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
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.LuceneBatchInserterIndexProvider;


public class Samples {//some declarations...
	GraphDatabaseService graphDB1;
	GraphDatabaseService graphDB2;
	BatchInserter graphDB;//open original database, set up insert for new one
	Node [] toandfrom;//holds start and dest node for current edge
	Node tempNode = null;//used to hold a node at various points
	ArrayList<Integer> samp = new ArrayList<Integer> ();//holds sample size
	ArrayList<Long> nodeids = new ArrayList<Long>();//keeps ids of current sample
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
	long node1;
	long node2;//holds two nodes of edge
	
	@SuppressWarnings("deprecation")
	Samples () { }
	
	public void getSamp (String origin, String destin, int size) {
		graphDB1 = new EmbeddedGraphDatabase ("C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/" + origin);
		graphDB2 = new GraphDatabaseFactory().newEmbeddedDatabase ("C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/" + destin);
		registerShutdownHook( graphDB2 );//helps in safe shut down
		registerShutdownHook (graphDB1);
		graphDB2.shutdown();
		RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		graphDB = BatchInserters.inserter ("C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/" + destin);
		//above sets up databases and batch inserter
		
		allNode = graphDB1.getAllNodes();
		nodeIt = allNode.iterator();//get nodes of database
		
		nodeIt.next();//go past reference node
		
		j2 = rand.nextInt(262111);//pick starting point
		
		for (i =0; i <= j2; i++) {
			tempNode = nodeIt.next();
			System.out.println (i);
		}//finds starting point 
		Node xprime;//used in random walk
		Node yprime = null;//see where we came from
		Node secondchoice;//used in certain cases of walk
		
		xprime = tempNode;
		yprime = xprime;
		samp.add((Integer) xprime.getProperty("id"));//initlise and add start node to sample
		
		Map <String, Object> properties = MapUtil.map( "id", xprime.getProperty("id") );
		nodeids.add (graphDB.createNode(properties));//creates and adds node to sample database
		
		while (samp.size() < size){//loop through sample till we get to size wanted
			//j=0;
			j2=0;
			k=0;
			k2=0;
			//i2=0;//reset temp value stores
			
			allRel = xprime.getRelationships();
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
			reli = allRel.iterator();//find edges for second node
			
			while (reli.hasNext() == true) {
				tempRels2.add(reli.next());
			}//temp store for second nodes edges
			
			float diff = (float) tempRels.size();
			float diff2 = (float) tempRels2.size();
			k = tempRels2.size();
			diff = diff/diff2;//divide to get value used for comparison
			p = rand.nextDouble();//get random number
			System.out.println ("j/k count: " + diff);
			System.out.println ("Node 1: " + xprime.getProperty("id"));
			System.out.println ("Node " + tempNode.getProperty("id"));
			
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
						if (!samp.contains((Integer) xprime.getProperty("id"))) {//check if node already in sample and add it if not
							samp.add((Integer) xprime.getProperty("id"));
							properties = MapUtil.map( "id", xprime.getProperty("id") );
							nodeids.add (graphDB.createNode(properties));
						}
						if (!relations.contains((Long)follow2.getId())){//check if edge in sample already and add if not
							toandfrom = follow2.getNodes();
							node1 = nodeids.get(samp.indexOf(toandfrom[0].getProperty("id")));
							node2 = nodeids.get(samp.indexOf(toandfrom[1].getProperty("id")));
							graphDB.createRelationship( node1, node2, mailed, null );
							relations.add(follow2.getId());
						}
					}
					else {//comparison failed, take go to tempnode instead
						yprime = xprime;
						xprime = tempNode;//set new node
						if (!samp.contains((Integer) xprime.getProperty("id"))) {//node in sample check
							samp.add((Integer) xprime.getProperty("id"));
							properties = MapUtil.map( "id", xprime.getProperty("id") );
							nodeids.add (graphDB.createNode(properties));
						}
						if (!relations.contains((Long)follow.getId())){//node in edge check
							toandfrom = follow.getNodes();
							node1 = nodeids.get(samp.indexOf(toandfrom[0].getProperty("id")));
							node2 = nodeids.get(samp.indexOf(toandfrom[1].getProperty("id")));
							graphDB.createRelationship( node1, node2, mailed, null );
							relations.add(follow.getId());
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
					if (!samp.contains((Integer) xprime.getProperty("id"))) {//node check
						samp.add((Integer) xprime.getProperty("id"));
						properties = MapUtil.map( "id", xprime.getProperty("id") );
						nodeids.add (graphDB.createNode(properties));
					}
					if (!relations.contains((Long)follow.getId()) && (xprime != yprime)){//edge check
						toandfrom = follow.getNodes();
						node1 = nodeids.get(samp.indexOf(toandfrom[0].getProperty("id")));
						node2 = nodeids.get(samp.indexOf(toandfrom[1].getProperty("id")));
						graphDB.createRelationship( node1, node2, mailed, null );
						relations.add(follow.getId());
					}
				}
			}
			else {//add node but don't follow
				if (!samp.contains((Integer) tempNode.getProperty("id"))) {//node check
					samp.add((Integer) tempNode.getProperty("id"));
					properties = MapUtil.map( "id", tempNode.getProperty("id") );
					nodeids.add(graphDB.createNode (properties));
				}
				if (!relations.contains((Long)follow.getId())){//edge check
					toandfrom = follow.getNodes();
					node1 = nodeids.get(samp.indexOf(toandfrom[0].getProperty("id")));
					node2 = nodeids.get(samp.indexOf(toandfrom[1].getProperty("id")));
					graphDB.createRelationship( node1, node2, mailed, null );
					relations.add(follow.getId());
				}
			}
			System.out.println ("samp size" + samp.size());
			tempRels.clear();
			tempRels2.clear();
		}
		graphDB1.shutdown();
		graphDB.shutdown();
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
