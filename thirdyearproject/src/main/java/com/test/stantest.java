package com.test;

import gnu.trove.set.hash.TLongHashSet;

import java.util.Map;
import java.util.Random;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;


public class stantest {
	public static void main(String[] args)
	{
		System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		
		//Graph<Long, Long> g = parser.toGraph();
		//System.out.println("We have " + g.getEdgeCount() + " edges");
		
	//	Samples samp = new Samples();
		//samp.getSamp("testDat", "sampled1", 500);
		String dat = "C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/testGraph";
		String samper = "C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/samping";
		String file = "C:/Users/Fusian/Downloads/Wiki-Vote.txt";
		//parseAndSamp (file, dat, samper);
		
		
		//String dat = "resources/testDat";
		//String samper = "resources/samples";
		//String file = "Wiki-Vote.txt";
		
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (dat);
		//graphDB1.shutdown();
		//Map <String, Object> properties  = MapUtil.map("weight", 0.4, "count", 2);
		//BatchInserter graphDB;//said inserter
		//set up relationship type and initilise batch inserter
		//RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		/*graphDB = BatchInserters.inserter (dat);
		graphDB.createNode(1,properties);
		properties  = MapUtil.map("weight", 0.6, "count", 1);
		graphDB.createNode(2,properties);
		properties  = MapUtil.map("weight", 0.01, "count", 1);
		graphDB.createNode(3,properties);
		properties  = MapUtil.map("weight", 0.6, "count", 2);
		graphDB.createNode(4,properties);
		properties  = MapUtil.map("weight", 0.23, "count", 3);
		graphDB.createNode(5,properties);
		properties  = MapUtil.map("weight", 0.7, "count", 4);
		graphDB.createNode(6,properties);
		properties  = MapUtil.map("weight", 0.064, "count", 3);
		graphDB.createNode(7,properties);
		properties  = MapUtil.map("weight", 0.6, "count", 1);
		graphDB.createNode(8,properties);
		properties  = MapUtil.map("weight", 0.2, "count", 0);
		graphDB.createNode(9,properties);
		properties  = MapUtil.map("weight", 0.4, "count", 2);
		graphDB.createNode(10,properties);
		properties  = MapUtil.map("weight", 0.7, "count", 2);
		graphDB.createNode(11,properties);
		properties  = MapUtil.map("weight", 0.3, "count", 0);
		graphDB.createNode(12,properties);
		properties  = MapUtil.map("weight", 0.7, "count", 1);
		graphDB.createNode(13,properties);
		properties  = MapUtil.map("weight", 0.3, "count", 1);
		graphDB.createNode(14,properties);
		properties  = MapUtil.map("weight", 0.76, "count", 3);
		graphDB.createNode(15,properties);
		properties  = MapUtil.map("weight", 0.02, "count", 0);
		graphDB.createNode(16,properties);
		properties  = MapUtil.map("weight", 0.24, "count", 2);
		graphDB.createNode(17,properties);
		properties  = MapUtil.map("weight", 0.76, "count", 1);
		graphDB.createNode(18,properties);
		properties  = MapUtil.map("weight", 0.034, "count", 2);
		graphDB.createNode(19,properties);
		properties  = MapUtil.map("weight", 0.18, "count", 2);
		graphDB.createNode(20,properties);
		properties  = MapUtil.map("weight", 0.1, "count", 1);
		graphDB.createNode(21,properties);
		properties  = MapUtil.map("weight", 0.7, "count", 2);
		graphDB.createNode(22,properties);
		properties  = MapUtil.map("weight", 0.2, "count", 1);
		graphDB.createNode(23,properties);
		properties  = MapUtil.map("weight", 0.8, "count", 0);
		graphDB.createNode(24,properties);
		properties  = MapUtil.map("weight", 0.2, "count", 1);
		graphDB.createNode(25,properties);
		properties  = MapUtil.map("weight", 0.4, "count", 2);
		graphDB.createNode(26,properties);
		properties  = MapUtil.map("weight", 0.3, "count", 2);
		graphDB.createNode(27,properties);
		properties  = MapUtil.map("weight", 0.7, "count", 1);
		graphDB.createNode(28,properties);
		properties  = MapUtil.map("weight", 0.9, "count", 3);
		graphDB.createNode(29,properties);
		properties  = MapUtil.map("weight", 0.45, "count", 1);
		graphDB.createNode(30,properties);
		properties  = MapUtil.map("weight", 0.2, "count", 1);
		graphDB.createNode(31,properties);
		properties  = MapUtil.map("weight", 0.77, "count", 1);
		graphDB.createNode(32,properties);
		properties  = MapUtil.map("weight", 0.1, "count", 2);
		graphDB.createNode(33,properties);
		properties  = MapUtil.map("weight", 0.6, "count", 1);
		graphDB.createNode(34,properties);
		properties  = MapUtil.map("weight", 0.1, "count", 1);
		graphDB.createNode(35,properties);
		properties  = MapUtil.map("weight", 0.4, "count", 2);
		graphDB.createNode(36,properties);
		properties  = MapUtil.map("weight", 0.37, "count", 1);
		graphDB.createNode(37,properties);
		properties  = MapUtil.map("weight", 0.002, "count", 1);
		graphDB.createNode(38,properties);
		properties  = MapUtil.map("weight", 0.3, "count", 1);
		graphDB.createNode(39,properties);
		properties  = MapUtil.map("weight", 0.3, "count", 3);
		graphDB.createNode(40,properties);
		properties  = MapUtil.map("weight", 0.1, "count", 2);
		graphDB.createNode(41,properties);
		properties  = MapUtil.map("weight", 0.23, "count", 2);
		graphDB.createNode(42,properties);
		properties  = MapUtil.map("weight", 0.4, "count", 1);
		graphDB.createNode(43,properties);
		properties  = MapUtil.map("weight", 0.6, "count", 1);
		graphDB.createNode(44,properties);
		properties  = MapUtil.map("weight", 0.4, "count", 0);
		graphDB.createNode(45,properties);
		properties  = MapUtil.map("weight", 0.79, "count", 0);
		graphDB.createNode(46,properties);
		properties  = MapUtil.map("weight", 0.02, "count", 1);
		graphDB.createNode(47,properties);
		properties  = MapUtil.map("weight", 0.9, "count", 2);
		graphDB.createNode(48,properties);
		properties  = MapUtil.map("weight", 0.61, "count", 1);
		graphDB.createNode(49,properties);
		properties  = MapUtil.map("weight", 0.3, "count", 2);
		graphDB.createNode(50,properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(1, 3, mailed, properties);
		properties = MapUtil.map("weight", 0.13);
		graphDB.createRelationship(1, 2, mailed, properties);
		
		properties = MapUtil.map("weight", 0.03);
		graphDB.createRelationship(2, 12, mailed, properties);
		
		properties = MapUtil.map("weight", 0.5);
		graphDB.createRelationship(3, 2, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(4, 5, mailed, properties);
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(4, 1, mailed, properties);
		
		properties = MapUtil.map("weight", 0.034);
		graphDB.createRelationship(5, 6, mailed, properties);
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(5, 4, mailed, properties);
		properties = MapUtil.map("weight", 0.9);
		graphDB.createRelationship(5, 7, mailed, properties);
		
		properties = MapUtil.map("weight", 0.23);
		graphDB.createRelationship(6, 10, mailed, properties);
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(6, 8, mailed, properties);
		properties = MapUtil.map("weight", 0.201);
		graphDB.createRelationship(6, 3, mailed, properties);
		properties = MapUtil.map("weight", 0.32);
		graphDB.createRelationship(6, 15, mailed, properties);
		
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(7, 13, mailed, properties);
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(7, 14, mailed, properties);
		
		properties = MapUtil.map("weight", 0.04);
		graphDB.createRelationship(8, 20, mailed, properties);
		
		properties = MapUtil.map("weight", 0.01);
		graphDB.createRelationship(10, 9, mailed, properties);
		properties = MapUtil.map("weight", 0.8);
		graphDB.createRelationship(10, 18, mailed, properties);
		properties = MapUtil.map("weight", 0.01);
		graphDB.createRelationship(10, 12, mailed, properties);

		properties = MapUtil.map("weight", 0.1);
		graphDB.createRelationship(11, 12, mailed, properties);
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(11, 1, mailed, properties);
		
		properties = MapUtil.map("weight", 0.7);
		graphDB.createRelationship(13, 4, mailed, properties);
		
		properties = MapUtil.map("weight", 0.17);
		graphDB.createRelationship(14, 17, mailed, properties);
		
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(15, 16, mailed, properties);
		properties = MapUtil.map("weight", 0.8);
		graphDB.createRelationship(15, 19, mailed, properties);
		properties = MapUtil.map("weight", 0.63);
		graphDB.createRelationship(15, 32, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(17, 13, mailed, properties);
		properties = MapUtil.map("weight", 0.6);
		graphDB.createRelationship(17, 16, mailed, properties);
		
		properties = MapUtil.map("weight", 0.8);
		graphDB.createRelationship(18, 19, mailed, properties);
		
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(19, 8, mailed, properties);
		properties = MapUtil.map("weight", 0.24);
		graphDB.createRelationship(19, 20, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(20, 6, mailed, properties);
		properties = MapUtil.map("weight", 0.14);
		graphDB.createRelationship(20, 15, mailed, properties);
		
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(21, 24, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(22, 21, mailed, properties);
		properties = MapUtil.map("weight", 0.23);
		graphDB.createRelationship(22, 4, mailed, properties);
		
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(23, 22, mailed, properties);

		properties = MapUtil.map("weight", 0.5);
		graphDB.createRelationship(25, 24, mailed, properties);
		
		properties = MapUtil.map("weight", 0.64);
		graphDB.createRelationship(26, 29, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(27, 32, mailed, properties);
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(27, 26, mailed, properties);
		
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(28, 29, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(29, 33, mailed, properties);
		properties = MapUtil.map("weight", 0.9);
		graphDB.createRelationship(29, 37, mailed, properties);
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(29, 35, mailed, properties);
		
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(30, 18, mailed, properties);

		properties = MapUtil.map("weight", 0.15);
		graphDB.createRelationship(31, 30, mailed, properties);

		properties = MapUtil.map("weight", 0.21);
		graphDB.createRelationship(32, 33, mailed, properties);
		
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(33, 27, mailed, properties);
		properties = MapUtil.map("weight", 0.03);
		graphDB.createRelationship(33, 38, mailed, properties);
		
		properties = MapUtil.map("weight", 0.14);
		graphDB.createRelationship(34, 30, mailed, properties);
		
		properties = MapUtil.map("weight", 0.03);
		graphDB.createRelationship(35, 33, mailed, properties);
		
		properties = MapUtil.map("weight", 0.15);
		graphDB.createRelationship(36, 34, mailed, properties);
		properties = MapUtil.map("weight", 0.32);
		graphDB.createRelationship(36, 38, mailed, properties);
		
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(37, 28, mailed, properties);
		
		properties = MapUtil.map("weight", 0.22);
		graphDB.createRelationship(38, 44, mailed, properties);
		
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(39, 38, mailed, properties);
		properties = MapUtil.map("weight", 0.24);
		graphDB.createRelationship(39, 35, mailed, properties);
		
		properties = MapUtil.map("weight", 0.7);
		graphDB.createRelationship(40, 37, mailed, properties);
		properties = MapUtil.map("weight", 0.8);
		graphDB.createRelationship(40, 35, mailed, properties);
		properties = MapUtil.map("weight", 0.6);
		graphDB.createRelationship(40, 39, mailed, properties);
		
		properties = MapUtil.map("weight", 0.37);
		graphDB.createRelationship(41, 38, mailed, properties);
		properties = MapUtil.map("weight", 0.2);
		graphDB.createRelationship(41, 46, mailed, properties);
		
		properties = MapUtil.map("weight", 0.03);
		graphDB.createRelationship(42, 39, mailed, properties);
		properties = MapUtil.map("weight", 0.44);
		graphDB.createRelationship(42, 43, mailed, properties);
		
		properties = MapUtil.map("weight", 0.27);
		graphDB.createRelationship(43, 40, mailed, properties);
		
		properties = MapUtil.map("weight", 0.3);
		graphDB.createRelationship(44, 45, mailed, properties);
		
		properties = MapUtil.map("weight", 0.4);
		graphDB.createRelationship(47, 46, mailed, properties);
		
		properties = MapUtil.map("weight", 0.37);
		graphDB.createRelationship(48, 49, mailed, properties);
		properties = MapUtil.map("weight", 0.6);
		graphDB.createRelationship(48, 42, mailed, properties);
		
		properties = MapUtil.map("weight", 0.42);
		graphDB.createRelationship(49, 43, mailed, properties);
		
		properties = MapUtil.map("weight", 0.04);
		graphDB.createRelationship(50, 47, mailed, properties);
		properties = MapUtil.map("weight", 0.1);
		graphDB.createRelationship(50, 44, mailed, properties);
		
		graphDB.shutdown();*/
		
		//GraphDatabaseService graphDB1;
		/*graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (dat);
		Transaction tx = graphDB1.beginTx();
		Node bound = graphDB1.getReferenceNode();
		bound.setProperty("bound", (long) 50);
		tx.success();
		tx.finish();
		graphDB1.shutdown();*/
		//parseAndSamp (file, dat, samper);
		TLongHashSet seeds = new TLongHashSet();
		seeds.add(22);
		seeds.add(25);
		LinearThresh lin = new LinearThresh();
		lin.infRun(seeds,samper);
		
		//IndependentCas infCas = new IndependentCas();
		//seeds = infCas.DegDis(samper, 50);
		//infCas.infRun (seeds, samper);
		
		//GraphDatabaseService graphDB1;
		//graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		
		//BatchInserter graphDB;//said inserter
		//set up relationship type and initilise batch inserter
		//RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		//graphDB = BatchInserters.inserter (samper);
		
		
		//Random rand = new Random(500);
		//long pick;
		//long bound = (Long) graphDB1.getReferenceNode().getProperty("bound");
		/*while (seeds.size()<100){
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
		graphDB1.shutdown();*/
		//LinearThresh lin = new LinearThresh();
		//lin.infRun(seeds,samper);
		
		/*IndependentCas infCas = new IndependentCas();
		seeds = infCas.DegDis(samper, 50);
		infCas.infRun (seeds, samper);*/
	}

	public static void parseAndSamp (String origin, String dat, String samper) {
		//ActualSampler parser = new ActualSampler(origin);
		//ActualSampler parser = new ActualSampler("C:/Users/Fusian/Downloads/soc-LiveJournal/soc-LiveJournal1.txt");
		//parser.Parse(dat);
		//System.out.println(parser.getComments());
		//System.out.println(parser.getDataSet().get(1)[0]);
		Samples samp = new Samples();
		samp.getSamp(dat, samper, 10);
	}
}


























