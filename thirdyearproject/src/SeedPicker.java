import java.io.File;
import java.util.Iterator;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.hash.TLongHashSet;

public class SeedPicker {
	private double p = 0.05;
	
	SeedPicker (){}
	//type 0 = independent cas, 1 = linear 1/edge num, 2 = normalised, any else = normalised with if
	public TLongHashSet degDis (String samper, int size, boolean multi, int type) {
		TLongHashSet seeds = new TLongHashSet();//will hold choices
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook(graphDB1);//set up graph
		Iterator<Node> nodeIt;//used to cycle through nodes
		Iterator<Relationship> reli;//cycles through edges
		TLongArrayList nodey = new TLongArrayList();//will store nodes ordered by edge count
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
		nodeIt.next();//cycle past reference node
		
		System.out.println (type);
		while (nodeIt.hasNext()) {//go through all nodes
			start = nodeIt.next();//get next node

			tx = graphDB1.beginTx();//set properties node needs for deg dis algorithm
			
			if (!start.hasProperty("incomeTotal")) {
				reli = start.getRelationships(Direction.INCOMING).iterator();
				while (reli.hasNext()) {
					initial = initial + (Double) reli.next().getProperty("weight");
				}
				start.setProperty("incomeTotal", initial);
			}
			
			start.setProperty("oN", 0);//represents outgoing edges ending in an infected node
			start.setProperty("iN", 0);//represents incoming edges ending in an infected node
			start.setProperty("Discount", start.getProperty("count"));
			tx.success();
			tx.finish();

			
			count = (Double) start.getProperty("Discount");//get outgoing edge count
			
			if (nodey.size() == 0) {//start list if list empty
				nodey.add(start.getId());
			}
			else {//list not empty, need to find correct position
				i = 0;
				
				for (i = 0; i < nodey.size(); i++) {//go through list
					
					if (count > (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("Discount")) {//check if node has found first node to have smaller count
						nodey.insert(i, start.getId()); //insert node at that point, move rest of nodes down
						stop = true;//means we found node
						break;//stop loop once position found
					}
					
				}
				
				if (stop == false && nodey.size() < size*10) {//node is smallest count so far, and cache limit not reached
					nodey.add (start.getId());//add node to bottom of list
				}
				
				if (nodey.size() > size*10) {//cache size has been exceeded
					nodey.removeAt(size*10);//remove last node
				}
				stop = false;//reset found value
			}

			
		}
		Relationship follow;
		double out;
		
		while (seeds.size() < size) {//loop until all nodes needed found

			System.out.println ("nodey: " + nodey.size());
			seeds.add (nodey.get(0));//add top node to seed set
			System.out.println ("Adding:" + nodey.get(0));
			System.out.println ("seedy:" + seeds.size());
			start = graphDB1.getNodeById(nodey.get(0));//get newly added node
			reli = start.getRelationships().iterator();//get nodes edges
			tx = graphDB1.beginTx();
			
			if (multi == true) {
				start.setProperty("colour", 0);
			}
			
			while (reli.hasNext()) {//go through edges and change other nodes counts
				follow = reli.next();//get next edge
				end = follow.getOtherNode(start);//get other node of edge
				dest = end.getId();//get id for other node
				
				if (!seeds.contains (dest) && nodey.contains(dest)){//checks if other node is not already in seed set and is in size list
					
					if (follow.getEndNode().getId() == end.getId()) {//check if other node is the destination node
						end.setProperty("iN", (Integer) end.getProperty("iN") + 1);//increase could of end node
						
						if (type == 1) {
							linp = 1/(Double) end.getProperty("Icount");
						}//set probability used in linear function
						else if (type == 2 || !(type == 3 && ((Double)follow.getProperty("weight") > (Double)end.getProperty("weight")))) {
							linp = (Double)follow.getProperty("weight")/(Double) end.getProperty("weight");
							linp = linp/ ((Double)end.getProperty("incomeTotal")/(Double)end.getProperty("weight"));
						}//set probability used in linear function
						else if (type == 3 && ((Double)follow.getProperty("weight") > (Double)end.getProperty("weight"))) {
							linp = 1;
						}
					}
					else {//check if its the start node
						end.setProperty("oN", (Integer) end.getProperty("oN") + 1);//increase out going count 
						if (type == 1) {
							linp = 1/(Double) start.getProperty("Icount");
						}//set probability used in linear function
						else if (type == 2 || !(type == 3 && ((Double)follow.getProperty("weight") > (Double)start.getProperty("weight")))) {
							linp = (Double)follow.getProperty("weight")/(Double) start.getProperty("weight");
							linp = linp/ ((Double)start.getProperty("incomeTotal")/(Double)start.getProperty("weight"));
						}//set probability used in linear function
						else if (type == 3 && ((Double)follow.getProperty("weight") > (Double)start.getProperty("weight"))) {
							linp = 1;
						}
					}
					
					initial = (Double) end.getProperty ("Discount");//get initial count of node
					out = (Double) end.getProperty ("Discount") - (Integer)end.getProperty("oN");//initial change of edge count
					
					if (type == 0) {//getting seeds of independent cascade
						discount = out - ((Integer) end.getProperty ("iN")) - (out*p);//modify node count
					}
					else {//seeds for linear thresh
						discount = out - ((Integer) end.getProperty ("iN")) - (out*linp);//modify node count 
					}
					System.out.println ("Discounting " + end.getId() + " from " +initial+ " to " + discount);
					end.setProperty ("Discount", discount);//set new count
					nodey.remove(dest);//remove end node from list
					stop = false;
					
					for (i = 0; i < nodey.size(); i++) {//go through list to find new place for node
						if (discount > (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("Discount")) {
							nodey.insert(i, dest); 
							stop = true;
							break;
						}
					}
					
					if (stop == false) {//node goes to bottom of list
						nodey.add (end.getId());
						end.setProperty("broke", true);
					}

				}
			}
			tx.success();
			tx.finish();
			
			nodey.removeAt(0);//remove new seed node
		}
		graphDB1.shutdown();
		System.out.println (seeds.toString());
		return seeds;//return seed set
		
	}
	
	//returns a random set of seeds
	public TLongHashSet randSeed (String samper, int size) {
		TLongHashSet seeds = new TLongHashSet();
		
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
			
		Random rand = new Random();
		long pick;
		long bound = (Long) graphDB1.getReferenceNode().getProperty("bound");//find highest id number
		while (seeds.size()<size){//repeat loop until seed size reached
			try{
				pick = rand.nextInt ((int)bound+1);//pick node id at random
				System.out.println ("picked:" + pick);
				System.out.println (seeds.size());
				if (graphDB1.getNodeById(pick) != null && pick !=0) {//check if node exists and is not reference node
					seeds.add(pick);
					System.out.println (seeds.size());//add node to seed set
				}
			}
			catch (Exception e) {
				
			}
		}
		graphDB1.shutdown();
		return seeds;
	}
	
	//Colours a graph for use in multi-linear model
	public void randSeedMultiFull (String samper, int colNum) {
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);//set up graph
		Iterator<Node> nodeIt = graphDB1.getAllNodes().iterator();
		Transaction tx;
		Random rand = new Random();
		nodeIt.next();
		int j = 0;
		
		tx = graphDB1.beginTx();
		int c = 0;
		while (nodeIt.hasNext()){// go through every node in graph
			j = rand.nextInt(colNum);//randomly choose colour
			nodeIt.next().setProperty("colour", j);//set node to chosen colour
			c++;
			if (c == 100000) {c=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
		}
		tx.success();
		tx.finish();
		
		graphDB1.shutdown();
		InfSpread.multiResults(colNum+1, samper);
	}
	
	public TLongHashSet randSeedMulti (String samper, int colNum, int size) {
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);//set up graph
		Transaction tx;
		Random rand = new Random();
		int pick = 0;
		TLongHashSet seeds = new TLongHashSet();
		TLongHashSet tempseeds = new TLongHashSet();

		long bound = (Long) graphDB1.getReferenceNode().getProperty("bound");//find highest id number		
		tx = graphDB1.beginTx();
		int c = 0;
		int i = 0;
		
		for (c = 0; c< colNum; c++) {
			while (tempseeds.size() < size){// go through every node in graph
				try{
					pick = rand.nextInt ((int)bound+1);//pick node id at random
					System.out.println ("picked:" + pick);
					System.out.println (seeds.size());
					if (graphDB1.getNodeById(pick) != null && !graphDB1.getNodeById(pick).hasProperty("colour") && pick !=0) {//check if node exists and is not reference node
						tempseeds.add(pick);
						graphDB1.getNodeById(pick).setProperty("colour", c);
						i++;
						if (i == 100000) {i=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
						System.out.println (seeds.size());//add node to seed set
					}
				}
				catch (Exception e) {}
			}
			
			System.out.println ("For colour " + c + " we have: " + tempseeds.toString());
			seeds.addAll(tempseeds);
			tempseeds.clear();
		}
		tx.success();
		tx.finish();
		
		graphDB1.shutdown();
		//InfSpread.multiResults(colNum, samper);
		return seeds;
	}
	
	//set up degree discount for multiple influence tracks when we only want one to be deg dis
	public void degDisMultiFull (String samper, int size, boolean lin ,int colNum) {
		
		TLongHashSet seeds = degDis (samper, size, lin, true);//gets deg dis seed set for this graph
		
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		Iterator<Node> nodeIt = graphDB1.getAllNodes().iterator();
		Transaction tx;
		Random rand = new Random();
		nodeIt.next();
		int j = 0;
		Node n;
		colNum--;

		tx = graphDB1.beginTx();
		int c = 0;
		while (nodeIt.hasNext()){//cycle through every node
			n = nodeIt.next();
			if (!seeds.contains(n.getId())) {//see if current node is in seed set
				j = rand.nextInt(colNum) + 1;
				n.setProperty("colour", j);
			}
			c++;
			if (c == 100000) {c=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
		}
		tx.success();
		tx.finish();
		
		graphDB1.shutdown();
		InfSpread.multiResults(colNum, samper);
	}
	
	public TLongHashSet degDisMulti (String samper, int size, boolean lin ,int colNum) {
		
		TLongHashSet seeds = degDis (samper, size, lin, true);//gets deg dis seed set for this graph
		TLongHashSet tempSeed = new TLongHashSet();
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		Iterator<Node> nodeIt = graphDB1.getAllNodes().iterator();
		Transaction tx;
		Random rand = new Random();
		nodeIt.next();
		int j = 0;
		Node n;
		//colNum--;

		tx = graphDB1.beginTx();
		int c = 0;
		long bound = (Long) graphDB1.getReferenceNode().getProperty("bound");//find highest id number
		
		for (int i = 1; i < colNum; i++) {
			while (tempSeed.size() < size) {
				try{
					j = rand.nextInt ((int)bound+1);//pick node id at random
					System.out.println ("picked:" + j);
					System.out.println (seeds.size());
					if (!graphDB1.getNodeById(j).hasProperty("colour") && graphDB1.getNodeById(j) != null && j !=0) {//check if node exists and is not reference node
						tempSeed.add(j);
						System.out.println (seeds.size());//add node to seed set
						graphDB1.getNodeById(j).setProperty("colour", i);
						c++;
						if (c == 100000) {c=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
					}
				}
				catch (Exception e) {
					
				}
			}
			System.out.println ("Color " + i+ " has " +tempSeed);
			seeds.addAll(tempSeed);
			tempSeed.clear();
		}

		tx.success();
		tx.finish();
		
		graphDB1.shutdown();
		InfSpread.multiResults(colNum, samper);
		return seeds;
	}
	//Gives seed set of basic max deg/ min deg
	//if boolean max = true finding max set
	public TLongHashSet degMaxMin (String samper, int size, boolean max) {
		TLongHashSet seeds = new TLongHashSet();//will hold selected nodes
		//samper = newGraph(samper);
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook(graphDB1);//graph set up
		Iterator<Node> nodeIt;//used to cycle through nodes
		TLongArrayList nodey = new TLongArrayList();
		Node start;
		nodeIt = graphDB1.getAllNodes().iterator();
		double count = 0;
		int i = 0;
		boolean stop = false;
		int g = 0;
		nodeIt.next();
		while (nodeIt.hasNext()) {//go through every node
			start = nodeIt.next();
			count = (Double) start.getProperty("count");//get count of each node
			System.out.println ("nodes at " + nodey.size());
			g++;
			System.out.println ("Nigga we done " + g + " nodes");
			if (nodey.size() == 0) {//begin list
				nodey.add(start.getId());
			}
			else {//list non empty
				i = 0;
				for (i = 0; i < nodey.size(); i++) {//find nodes correct place in list. Use two different comparisons, as value of max never changes.
					//always finding max OR min so only one side will be used by an instance of the function
					if ((count > (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("count") && max == true)||
							count < (Double) graphDB1.getNodeById(nodey.get(i)).getProperty("count") && max == false) {
						nodey.insert(i, start.getId()); //insert node in correct place
						stop = true;
						break;
					}
				}
				
				if (stop == false && nodey.size() < size) {//node goes to end of list
					nodey.add (start.getId());
				}
				
				if (nodey.size() > size) {//current set is larger then wanted seed size, remove lowest node
					nodey.removeAt(size);
				}
				stop = false;
			}
		}
		
		seeds.addAll(nodey);//add list to seed set
		graphDB1.shutdown();
		return seeds;//return seed set
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