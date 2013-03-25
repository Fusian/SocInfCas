

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.linked.TDoubleLinkedList;
import gnu.trove.list.linked.TLongLinkedList;
import gnu.trove.set.hash.TLongHashSet;
import gnu.trove.stack.array.TIntArrayStack;

import java.util.Iterator;
import java.util.Random;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.MapUtil;

public class InfSpread {
	private double p = 0.01;//used in independent cascade
	
	InfSpread () { }
	//Function performs linear threshold cascade on a graph when given a seed set
	public int linRun (TLongHashSet seeds, String test, GraphDatabaseService graphDB1) {
		boolean stop = false;//decides if influence run stops
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );//holds graph
		TLongIterator seedIt;//goes through nodes
		Iterator<Relationship> reli;//cycles through edges
		Node tempNode;//holds current node
		Node otherNode;//holds node incoming to current node
		Relationship follow;//holds current edge
		double tTotal = 0;//holds influencing edge weigh totals
		TLongHashSet neighbours = new TLongHashSet();//holds nodes to be tested - neighbours of seed nodes
		TLongHashSet neighNodes = new TLongHashSet();//holds neighbours of current node
		TLongHashSet tempSeeds = new TLongHashSet();
		
		//graphDB1.beginTx();
		seedIt = seeds.iterator();
		
		while (seedIt.hasNext()) {//iterate through seed nodes
			tempNode = graphDB1.getNodeById(seedIt.next());
			reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
			while (reli.hasNext()) {//cycle through current nodes edges
				follow = reli.next();//get next edge
				otherNode = follow.getOtherNode(tempNode);//get node on other end of edge
				if (!seeds.contains(otherNode.getId())) {//see if nodes not a seed node, thus must be tested
					neighbours.add(otherNode.getId());//add node to test set
				}
			}
		}
	
		while (stop == false) {//run influence cascade until no changes occur
			stop = true;//changes have no occured yet - set to stop to true
			seedIt = neighbours.iterator();//iterate through test set
			
			while (seedIt.hasNext()) {//check all test nodes
				tempNode = graphDB1.getNodeById(seedIt.next());
				//System.out.println (tempNode.getId() + " is a neighbour node, and is being tested");
				reli = tempNode.getRelationships(Direction.INCOMING).iterator();
				while (reli.hasNext()) {//check edges for current node
					follow = reli.next();
					otherNode = follow.getOtherNode(tempNode);//get other node
					//System.out.println ("checking neighbour " + otherNode.getId());
					if (seeds.contains(otherNode.getId())) {//is other node in infected set?
						tTotal= tTotal +(Double)follow.getProperty("weight");//add influence from other node to total
						//System.out.println ("Node " + otherNode.getId() + " has added " + follow.getProperty("weight"));
					}
				}
				if (tTotal >= (Double)tempNode.getProperty("weight")) {//threshold has been broken
					tempSeeds.add(tempNode.getId());//current node is now infected
					reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
					while (reli.hasNext()) {//check edges for current node
						follow = reli.next();//get next edge
						otherNode = follow.getOtherNode(tempNode);//get node on other end of edge
						if (!seeds.contains(otherNode.getId()) && !tempSeeds.contains(otherNode.getId())) {//see if nodes not a seed node, thus must be tested
							neighNodes.add(otherNode.getId());//add node to test set
						}
					}
					stop = false; // change has occured.
					//System.out.println ("Adding " + tempNode.getId());
				}
				tTotal = 0;
									
			}
			
			//System.out.println ("adding new nodes");
			seeds.addAll(tempSeeds);
			neighbours.addAll(neighNodes);//remove any infected nodes and add new nodes to test to test set
			//-System.out.println (seeds.size());
			neighbours.removeAll(seeds);
			tempSeeds.clear();
			seeds.iterator();
			
	
			neighNodes.clear();//clear for next interation
		}
		//graphDB1.shutdown();
		return seeds.size();//shut down graph and return new size of infected nodes
	}
	
	//Function performs a competitive version of linear threshold - weighted model.
	//'seeds' should hold all infected nodes regardless of colour.
	public void linRunWeightedMulti (TLongHashSet seeds, String test, int tracks,GraphDatabaseService graphDB1) {
		boolean stop = false;//decides if cascade stops
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );//retrieve graph
		double[] totals = new double [tracks];//holds influence totals for each possible source
		int colour = 0;//holds colour of influencing node, representing source of influence
		int i;
		TLongIterator seedIt;
		Iterator<Relationship> reli;//cycles through edges
		Node tempNode;
		Node otherNode;
		Relationship follow;//used to hold needed nodes and edges
		double tTotal = 0;//holds overall influence total
		TLongHashSet infNodes = new TLongHashSet();//holds all infected nodes to be removed from neighbour set
		TLongHashSet neighbours = new TLongHashSet();//holds nodes to test
		TLongHashSet neighNodes = new TLongHashSet();//holds additional nodes to be tested next iteration
		TLongHashSet neighNodesTemp = new TLongHashSet();//holds nodes that may need to be tested next iteration
		Random rand = new Random();//gens random numbers
		double r = 0;//holds numbers gen'd
		Transaction tx;
		//graphDB1.beginTx();
		seedIt = seeds.iterator();
		
		while (seedIt.hasNext()) {//goes through to get initial neighbours set, and make seeds hold only one colour of influence
			tempNode = graphDB1.getNodeById(seedIt.next());//get next node
			reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
			while (reli.hasNext()) {//cycle through edges of current node
				follow = reli.next();
				otherNode = follow.getOtherNode(tempNode);//get other edge node
				if (!otherNode.hasProperty("colour")) {//make sure neighbour nodes isn't infected
					neighbours.add(otherNode.getId());//add uninfected node to neighbour/test set
				}
			}
			if (!((Integer)tempNode.getProperty("colour") == 0)) {//see if current node is not in 'our' influence track
				infNodes.add(tempNode.getId());//add to set of nodes to remove from seed set
			}
		}
		
		seeds.removeAll(infNodes);//update seed set, seed set now contains only 'our' nodes
		infNodes.clear();//clear infnodes for use later on

		while (stop == false) {//run influence cascade until no more changes 
			stop = true;//set stop to true, cascade stops unless new nodes infected
			seedIt = neighbours.iterator();
			
			while (seedIt.hasNext()) {//cycle through current test set
				tempNode = graphDB1.getNodeById(seedIt.next());//get next node
				//System.out.println (tempNode.getId() + " is a neighbour node, and is being tested");
				reli = tempNode.getRelationships(Direction.INCOMING).iterator();
				while (reli.hasNext()) {//go through all edges of current node
					follow = reli.next();//current edge
					otherNode = follow.getOtherNode(tempNode);//get other node
					//System.out.println ("checking neighbour " + otherNode.getId());
					if (otherNode.hasProperty("colour") && !neighbours.contains(otherNode.getId())) {//check if other node is infected by a track
						colour = (Integer)otherNode.getProperty("colour");//retrieve track infecting node
						totals[colour] = totals[colour] + (Double) follow.getProperty("weight");//increase total of influence for that track
						tTotal= tTotal +(Double)follow.getProperty("weight");//increase total influence
					}
				}
				
				//if statement checks if threshold broken, and picks influence for current node based on weighted probability
				if (tTotal >= (Double)tempNode.getProperty("weight")) {//check if threshold has been broken
					totals[0] = totals[0]/tTotal;//normalise total for first track
					r = rand.nextDouble();//gen random double
					i = 0;
					while (r > totals[0]) {//cycle through until correct track is reached
						i++;//add on total for next track
//						//System.out.println ("R equals " + r + ". total[0] is currently: " + totals[0] + ", and i is " + i );
						totals[0] = totals[0] + (totals[i]/tTotal);
						//System.out.println ("R equals " + r + ". total[0] is currently: " + totals[0] + ", and i is " + i );

					}
					tx = graphDB1.beginTx();
					//loop exited, i holds value of chosen track
					tempNode.setProperty("colour", i);//current node now influenced by track i
					//System.out.println ("Node " + tempNode.getId() + " changed to colour " + i);
					tx.success();
					tx.finish();
					if (i == 0) {seeds.add(tempNode.getId());}//means 'our' track has infected another node
					infNodes.add(tempNode.getId());//add to set to remove from current test set
					reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
					while (reli.hasNext()) {//cycle through edges of current node
						follow = reli.next();
						otherNode = follow.getOtherNode(tempNode);//get other edge node
						if (!otherNode.hasProperty("colour")) {//make sure neighbour nodes isn't infected
							neighNodes.add(otherNode.getId());//add uninfected node to neighbour/test set
						}
					}
					stop = false;//change has occurred
				}
					
				for (i=0; i<tracks; i++) {//track totals must be reset for next node
					totals[i] = 0;
				}	
				tTotal = 0;	//overall total must be reset
				neighNodesTemp.clear();//temp neighbours cleared
				System.gc();//call garbage collector to keep memory consumption low
			}
			
			//System.out.println ("adding new nodes");
			neighbours.addAll(neighNodes);//add new neighbour nodes
			neighbours.removeAll(infNodes);//remove newly infected nodes
			
			//System.out.println ("THESE NODES ARE BEING ADDED");
			//System.out.println (infNodes.toString());
			//System.out.println (seeds.size());
			
			seeds.iterator();
			
			infNodes.clear();
			neighNodes.clear();//clear temp sets for next node
		}
		//graphDB1.shutdown();
		System.out.println ("Weighted Threshold Model returns the following spreads: ");
		multiResults (tracks, test,true,graphDB1);
		//colourKill (test,graphDB1);
	}
	
	//Function runs competitive version of linear threshold cascade - multiple thresholds
	public void linRunMultiThresh (TLongHashSet seeds, String test, int tracks,GraphDatabaseService graphDB1) {
		
		multiThreshMaker (test, tracks, graphDB1);//graph must be adapted to multi thresholds, and given thresholds for all influence tracks.
		boolean stop = false;
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );//retrieve new database
		double[] totals = new double [tracks];//holds totals for different tracks
		int[] posInf = new int [tracks];//holds tracks that may infect node
		int colour = 0;//holds colour of a node
		int i;
		TLongIterator seedIt;
		Iterator<Relationship> reli;//cycles through edges
		Node tempNode;
		Node otherNode;
		Relationship follow;//holds needed nodes and edges
		TLongHashSet infNodes = new TLongHashSet();//holds all infected nodes to be removed from neighbour set
		TLongHashSet neighbours = new TLongHashSet();//holds nodes to test
		TLongHashSet neighNodes = new TLongHashSet();//holds additional nodes to be tested next iteration
		new TLongHashSet();
		Random rand = new Random();//gens random nums
		int r = 0;//holds random numbers
		Transaction tx;

		seedIt = seeds.iterator();
		
		while (seedIt.hasNext()) {//go through seed set to get neighbour set for testing and remove all nodes not in 'our' track
			tempNode = graphDB1.getNodeById(seedIt.next());//get current node
			reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
			while (reli.hasNext()) {//goes through each edge of current node
				follow = reli.next();//get next edge
				otherNode = follow.getOtherNode(tempNode);//get other edge node
				if (!otherNode.hasProperty("colour")) {//check node is not being influenced
					neighbours.add(otherNode.getId());//add to test set
				}
			}
			if (!((Integer)tempNode.getProperty("colour") == 0)) {//check node is not one of 'our' nodes
				infNodes.add(tempNode.getId());//add to set to remove from seed set
			}
		}
		
		seeds.removeAll(infNodes);//remove all nodes not influenced by our track
		infNodes.clear();//clear set for later use

		while (stop == false) {//run cascade until no changes occur
			stop = true;//cascade will stop unless changes occur
			seedIt = neighbours.iterator();
			
			while (seedIt.hasNext()) {//go through all test nodes
				tempNode = graphDB1.getNodeById(seedIt.next());//get next node
				//System.out.println (tempNode.getId() + " is a neighbour node, and is being tested");
				reli = tempNode.getRelationships(Direction.INCOMING).iterator();
				while (reli.hasNext()) {//cycle through edges for current node
					follow = reli.next();//next edge
					otherNode = follow.getOtherNode(tempNode);
					//System.out.println ("checking neighbour " + otherNode.getId());
					if (otherNode.hasProperty("colour") && !neighbours.contains(otherNode.getId())) {//check if other node is infected, and was infected in a previous step
						colour = (Integer)otherNode.getProperty("colour");
						//System.out.println ("Has colour " + colour);
						totals[colour] = totals[colour] + (Double) follow.getProperty("weight");//increase correpsonding tracks total by edge weight for this node
					}
				}
				i = 0;
				for (int j = 0; j < tracks; j++){//must check if any track has broken its corresponding threshold
					if (totals[j] >= (Double)tempNode.getProperty("weight"+j)) {//tracks has broken threshold
						posInf [i] = j;
						i++;//add track to possible influencers of node
					}
					//System.out.println ("Thresh for track " + j + " is " + tempNode.getProperty("weight"+j));
				}
				
				if (i > 0) {//more then one track could influence, must pick uniformly at random
					r = rand.nextInt(i);
					tx = graphDB1.beginTx();
					tempNode.setProperty("colour", posInf[r]);//set node to influence by chosen track
					//System.out.println (tempNode.getId() + " now colour " + posInf[r]);
					tx.success();
					tx.finish();
					if (posInf[0] == 0) {seeds.add(tempNode.getId());}//add to set of nodes infected by 'us'
					infNodes.add(tempNode.getId());//add to set of nodes to remove later
					reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
					while (reli.hasNext()) {//cycle through edges of current node
						follow = reli.next();
						otherNode = follow.getOtherNode(tempNode);//get other edge node
						if (!otherNode.hasProperty("colour")) {//make sure neighbour nodes isn't infected
							neighNodes.add(otherNode.getId());//add uninfected node to neighbour/test set
						}
					}
					stop = false;//changes have occurred
				}
				
				for (i=0; i<tracks; i++) {//clear all track totals for next node
					totals[i] = 0;
				}	
			}
			
			//System.out.println ("adding new nodes");
			neighbours.addAll(neighNodes);//add neighbour nodes for testing
			neighbours.removeAll(infNodes);//remove all infected nodes
			//System.out.println ("THESE NODES ARE BEING ADDED");
			//System.out.println (infNodes.toString());
			//System.out.println (seeds.size());
			
			seeds.iterator();
			
			infNodes.clear();
			neighNodes.clear();//clear sets for next iteration
		}
		//graphDB1.shutdown();
		System.out.println ("Multiple Threshold Model returns the following spreads: ");
		multiResults (tracks, test,true,graphDB1);
		//colourKill (test,graphDB1);
	}
	
	//Function prepares graph for multiple threshold cascades
	public void multiThreshMaker (String test, int tracks, GraphDatabaseService graphDB1) {
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook(graphDB1);//retreive original graph
		
		Iterator<Node> nodeIt;//used to cycle through nodes
		nodeIt = graphDB1.getAllNodes().iterator();
		Node n;
		MapUtil.map();
		Random rand = new Random();//used to assign threshold values
		Transaction tx = graphDB1.beginTx();
		int c = 0;
		nodeIt.next();
		while (nodeIt.hasNext()) {//go through every node of old graph
			n = nodeIt.next();//get next node
			
			for (int i = 0; i< tracks; i++) {//add new weight properties for all tracks
				n.setProperty("weight"+i, rand.nextDouble());
				c++;
				if (c>100000) {c=0;tx.success();tx.finish();tx=graphDB1.beginTx();}
			}
		}
		tx.success();
		tx.finish();
		//graphDB1.shutdown();
	}
	
	//Function performs independent cascade on graph
	public int indCas (TLongHashSet seeds, String test, GraphDatabaseService graphDB1) {
		//double prob = 0.05;
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);//retrieve graph
		boolean stop = false;//
		
		TLongIterator seedIt;
		Iterator<Relationship> reli;//cycles through edges
		TLongHashSet infNodes = new TLongHashSet();//holds nodes to be added to seed set
		
		Node tempNode;
		Node otherNode;
		Relationship follow;//holds nodes and edges needed for instructions
		Random rand = new Random();//gens random numbers
		
		while (stop == false) {//run through influence cascade until no changes occur
			stop = true;//cascade will stop if no changes occur
			seedIt = seeds.iterator();
			
			while (seedIt.hasNext()) {//go through all seed nodes and test neighbours
				tempNode = graphDB1.getNodeById(seedIt.next());
				//System.out.println (tempNode.getId() + " is a seed node and is having it's neighbours tested");
				reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
				
				while (reli.hasNext()) {//go through every edge of curent node
					follow = reli.next();//get edge
					otherNode = follow.getOtherNode(tempNode);//get other node
					if (!seeds.contains(otherNode.getId())&& !infNodes.contains(otherNode.getId())){//test node is not infected
						if (p > rand.nextDouble()) {//see if node had infected neighbour with chance p
							infNodes.add(otherNode.getId());//node infected
							stop = false;//change occurred
						}
					}
				}
			}
			
			//System.out.println ("adding new nodes");
			seeds.addAll(infNodes);//add newly infected nodes
			//System.out.println ("THESE NODES ARE BEING ADDED");
			//System.out.println (infNodes.toString());
			//System.out.println (seeds.size());
			infNodes.clear();//clear for next pass
			
		}
		//graphDB1.shutdown();
		return seeds.size();
	}
	
	public void waveCas (TLongHashSet seeds, String test, int tracks,GraphDatabaseService graphDB1) {
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );//retrieve graph
		TLongIterator seedIt;
		Iterator<Relationship> reli;//cycles through edges
		Node tempNode;
		Node otherNode;
		Relationship follow;//used to hold needed nodes and edges
		TLongHashSet infNodes = new TLongHashSet();//holds all infected nodes to be removed from neighbour set
		TLongHashSet neighbours = new TLongHashSet();//holds nodes to test
		TLongHashSet neighNodes = new TLongHashSet();//holds additional nodes to be tested next iteration
		new TLongHashSet();
		TLongLinkedList pressures = new TLongLinkedList();
		Random rand = new Random();//gens random numbers
		int r = 0;//holds numbers gen'd
		//graphDB1.beginTx();
		seedIt = seeds.iterator();
		Transaction tx;
		
		while (seedIt.hasNext()) {//goes through to get initial neighbours set, and make seeds hold only one colour of influence
			tempNode = graphDB1.getNodeById(seedIt.next());//get next node
			reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
			while (reli.hasNext()) {//cycle through edges of current node
				follow = reli.next();
				otherNode = follow.getOtherNode(tempNode);//get other edge node
				if (!otherNode.hasProperty("colour")) {//make sure neighbour nodes isn't infected
					neighbours.add(otherNode.getId());//add uninfected node to neighbour/test set
				}
			}
			if (!((Integer)tempNode.getProperty("colour") == 0)) {//see if current node is not in 'our' influence track
				infNodes.add(tempNode.getId());//add to set of nodes to remove from seed set
			}
		}
		
		seeds.removeAll(infNodes);//update seed set, seed set now contains only 'our' nodes
		infNodes.clear();//clear infnodes for use later on
		
		while (neighbours.size() > 0) {
			seedIt = neighbours.iterator();
			
			while (seedIt.hasNext()) {//go through all test nodes
				tempNode = graphDB1.getNodeById(seedIt.next());//get next node
				//System.out.println (tempNode.getId() + " is a neighbour node, and is being tested");
				reli = tempNode.getRelationships(Direction.INCOMING).iterator();
				while (reli.hasNext()) {//cycle through edges for current node
					follow = reli.next();//next edge
					otherNode = follow.getOtherNode(tempNode);
					//System.out.println ("checking neighbour " + otherNode.getId());
					if (otherNode.hasProperty("colour") && !neighbours.contains(otherNode.getId())) {//check if other node is infected, and was infected in a previous step
						pressures.add(otherNode.getId());
					}
				}
				//System.out.println (pressures.size());
				r = rand.nextInt (pressures.size());
				tx = graphDB1.beginTx();
				tempNode.setProperty("colour", graphDB1.getNodeById(pressures.get(r)).getProperty("colour"));
				infNodes.add(tempNode.getId());
				//System.out.println (tempNode.getId() + " now set to colour " + tempNode.getProperty("colour"));
				reli = tempNode.getRelationships(Direction.OUTGOING).iterator();
				while (reli.hasNext()) {//cycle through edges of current node
					follow = reli.next();
					otherNode = follow.getOtherNode(tempNode);//get other edge node
					if (!otherNode.hasProperty("colour")) {//make sure neighbour nodes isn't infected
						neighNodes.add(otherNode.getId());//add uninfected node to neighbour/test set
					}
				}
				tx.success();
				tx.finish();
				pressures.clear();
			}
			//System.out.println ("Next iteration");
			//System.out.println (infNodes.toString());
			neighbours.addAll(neighNodes);
			neighbours.removeAll(infNodes);
			infNodes.clear();
			neighNodes.clear();

		}
		//graphDB1.shutdown();
		System.out.println ("Wave Cascade returns the following spreads:");
		multiResults (tracks,test,true,graphDB1);

	}
	public void changKill (String test,GraphDatabaseService graphDB1) {
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );
		
		Iterator<Node> allNod = graphDB1.getAllNodes().iterator();
		
		allNod.next();
		while (allNod.hasNext()) {
			allNod.next().removeProperty("change");
		}
		//graphDB1.shutdown();
		
	}
	public void multiLin (double colNum, String test, double fullc, int thresh,GraphDatabaseService graphDB1) {
		changKill(test,graphDB1);
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );
		
		double al = 0.99;
		double tf = 0.1;
		double W = 0;
		double tot = 0;
		double nodeC = 0;
		double be = 0.8;
		double bound = 0;
		double consta = be/colNum;
		double m = 0;
		int chang = 0;
		double[] prob = new double[(int) colNum];
		double[] globProb = new double[(int) colNum];
		int j = 0;
		Transaction tx;
		boolean told = false;
		int pointer = 0;
		int imin = 0;
		int imax = 0;
		int f = 0;
		
		TIntArrayStack changes = new TIntArrayStack(5000);
		Random rand = new Random();
		Iterator<Node> allNod = graphDB1.getAllNodes().iterator();
		Iterator<Relationship> allRel;
		Node n = null;
		Relationship follow;
		allNod.next();

		tx = graphDB1.beginTx();
		int c = 0;
		while (allNod.hasNext()) {
			n = allNod.next();
			nodeC++;
			allRel = n.getRelationships(Direction.OUTGOING).iterator();
			while (allRel.hasNext()) {
				allRel.next();
				tot = tot + 0.5;
			}
			allRel = n.getRelationships(Direction.INCOMING).iterator();
			while (allRel.hasNext()) {
				allRel.next();
				tot = tot + 1;
			}
			W = W + tot;
			n.setProperty("wi", tot);
			c++;
			if (c == 100000) {c=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
			tot = 0;
		}
		tx.success();
		tx.finish();
		
		tx = graphDB1.beginTx();
		TLongLinkedList ref = new TLongLinkedList();
		TDoubleLinkedList boun = new TDoubleLinkedList();
		c = 0;
		allNod = graphDB1.getAllNodes().iterator();
		allNod.next();
		tot = 0;
		while (allNod.hasNext()) {
			n = allNod.next();
			tot = tot + (((Double)n.getProperty("wi"))/W);
			boun.add(tot);
			ref.add(n.getId());
			c++;
			if (c == 100000) {c=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
		}
		tx.success();
		tx.finish();
		
		imax = ref.size();
		//System.out.println ("About to make first round of changes");
		while (m < nodeC*fullc){
			bound = rand.nextDouble();
			for (j=0; j<colNum;j++) {
				prob[j] = 0;
			}
			while (told == false) {
				if (boun.get(pointer) > bound &&  boun.get(pointer-1) < bound) {
					told = true;
				}
				else if (boun.get(pointer) > bound) {
					imax = pointer - 1;
				}
				else if (boun.get(pointer) < bound) {
					imin = pointer + 1;
				}
				pointer = imin + ((imax - imin) / 2);
				if (pointer==0) {told = true;}
			}
			n = graphDB1.getNodeById(ref.get(pointer));
			told = false;
			imax = ref.size();
			imin = 0;
			pointer = imax/2;
			allRel = n.getRelationships().iterator();
			while (allRel.hasNext()) {
				follow = allRel.next();
				j = (Integer)follow.getOtherNode(n).getProperty("colour");
				if (follow.getEndNode().equals(n)) {
					prob[j] = prob[j] + 1;
				}
				else {prob[j] = prob[j] + 0.5;}
			}
			//System.out.println ("wi at " + n.getProperty("wi"));
			for (j = 0; j< colNum; j++) {
				//System.out.println ("prob"+j+" is at " + prob[j]);
				prob[j] = consta + ((1-be)*(prob[j]/(Double)n.getProperty("wi")));
			}
			
			bound = rand.nextDouble();
			tot = 0;
			j = -1;
			while (tot < bound) {
				j++;
				tot = tot + prob[j];
				//System.out.println ("prob"+j+" is at " + prob[j]);
				//System.out.println ("tot is " + tot);
				//System.out.println ("bound is " + bound);
			}
			tx = graphDB1.beginTx();
			n.setProperty("colour", j);
			if (!n.hasProperty("change")) {
				n.setProperty("change",1);
				chang = 1;
			}
			else {
				chang = (Integer) n.getProperty("change");
				chang++;
				n.setProperty("change", chang);
				
			}
			if (chang == thresh ) {m++; n.setProperty("checker", "0");}
			
			tx.success();
			tx.finish();
			
			changes.push(j);

			f++;
			System.out.println ("Iteration " + f + " has values of m at " + m +".(Using i="+fullc+" and size= "+thresh);
		}
		int k = 0;
		while (changes.size() > 0) {
			globProb[changes.pop()]++;
		}
		
		for (int i =0; i<colNum; i++) {
			if (globProb[i] > globProb[k]) {
				k = i;
			}
		}
		allNod = graphDB1.getAllNodes().iterator();
		allNod.next();
		tx = graphDB1.beginTx();
		c = 0;
		System.out.println ("setting all colours to same");
		while (allNod.hasNext()) {
			allNod.next().setProperty("colour", k);
			System.out.println  ("Changing node " + c);
			c++;
			if (c == 100000) {c=0;tx.success();tx.finish();tx = graphDB1.beginTx();}
		}
		tx.success();
		tx.finish();
		
		double titer = 1;
		for (int i =0; i<prob.length; i++){
			prob[i] = 0;
		}
		System.out.println ("Final Pass");
		while (titer > tf) {
			allNod = graphDB1.getAllNodes().iterator();
			allNod.next();
			while (allNod.hasNext()){
				n = allNod.next();
				System.out.println ("Next node: " + n.getId());
				allRel = n.getRelationships().iterator();
				while (allRel.hasNext()) {
					follow = allRel.next();
					j = (Integer)follow.getOtherNode(n).getProperty("colour");
					if (follow.getEndNode().equals(n)){
						prob[j] = prob[j] + 1;
					}
					else {
						prob[j] = prob[j] + 0.5;
					}
				}
				
				for (j = 0; j< colNum; j++) {
					prob[j] = consta + ((1-be)*(prob[j]/(Double)n.getProperty("wi")));
				}
				
				bound = rand.nextDouble();
				tot = 0;
				j = -1;
				while (tot < bound) {
					j++;
					tot = tot + prob[j];
				}
				tx = graphDB1.beginTx();
				n.setProperty("colour", j);
				tx.success();
				tx.finish();
			}
			titer = al * titer;
			System.out.println (titer);
		}
		//graphDB1.shutdown();
		System.out.println ("Full multi lin returns following spreads:");
		multiResults(colNum, test, true,graphDB1);
		colourKill (test,graphDB1);
	}
	
	public static void colourKill (String samper,GraphDatabaseService graphDB1) {
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		//registerShutdownHook( graphDB1 );
		
		Iterator<Node> allNod = graphDB1.getAllNodes().iterator();
		allNod.next();
		
		int c = 0;
		Transaction tx = graphDB1.beginTx();
		
		while (allNod.hasNext()) {
			allNod.next().removeProperty("colour");
			c++;
			if (c>100000) {c=0;tx.success();tx.finish();tx=graphDB1.beginTx();}
			
		}
		tx.success();
		tx.finish();
		//graphDB1.shutdown();
	}
	
	public static int[] multiResults (double track, String test, boolean talk,GraphDatabaseService graphDB1) {
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (test);
		//registerShutdownHook( graphDB1 );
		
		int[] counts = new int[(int)track];
		int j =0;
		Iterator<Node> allNod = graphDB1.getAllNodes().iterator();
		Transaction tx;
		allNod.next();
		Node n;
		int c = 0;
		
		tx = graphDB1.beginTx();
		while (allNod.hasNext()){
			n = allNod.next();
			if (n.hasProperty("colour")) {
				j = (Integer) n.getProperty("colour");
				counts[j]++;
				n.removeProperty("change");
				n.removeProperty("checker");
				c++;
				if (c>100000) {c=0;tx.success();tx.finish();tx=graphDB1.beginTx();}
			}
		}
		tx.success();
		tx.finish();
		
		for (int i = 0; i < track; i++) {
			if (talk == true) {
				System.out.println ("Influence track " + i + " has " + counts[i] + " members.");
				
				if (graphDB1.getReferenceNode().hasProperty("track"+i)){
					System.out.println ("Compared to previous: " + graphDB1.getReferenceNode().getProperty("track"+i, 0));
				}
			}
			tx = graphDB1.beginTx();
			graphDB1.getReferenceNode().setProperty("track"+i, counts[i]);
			tx.success();
			tx.finish();
		}
		return counts;
		//graphDB1.shutdown();
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
