

import gnu.trove.set.hash.TLongHashSet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
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

		/*Parser p = new Parser ("live.txt");
		p.Parse("datab/live");
		
		Samples s = new Samples();
		s.getSamp("datab/live", "datab/live-mhda-50000", 50000);
		s.getSnow("datab/live", "datab/live-snow-50000", 50000);
		
		p = new Parser ("google.txt");
		p.Parse("datab/google");
		
		//Samples s = new Samples();
		s.getSamp("datab/google", "datab/google-mhda-50000", 50000);
		s.getSnow("datab/google", "datab/google-snow-50000", 50000);*/
		
		String samper = "datab/epin-mhda-20000-2";
		String dat = "datab/epinG";
		int seds = 200;
		int nod = 2000;
		String ty = "mhda";
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook(graphDB1);//graph set up
		SeedPicker se = new SeedPicker();
		InfSpread inf = new InfSpread();
		long time = 0;
		double secs;
		String res = "results19.txt";
		double fullc = 0.9;
		
		try{
		//FileWriter fstream;
		//int j = 1;
		fullc = 0.2;
		/*fstream = new FileWriter(res, true);
		BufferedWriter out = new BufferedWriter(fstream);
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();*/
		
		FileWriter fstream = new FileWriter(res, true);
		BufferedWriter out = new BufferedWriter(fstream);
		int j=10;
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();
		
		fstream = new FileWriter(res, true);
		out = new BufferedWriter(fstream);
		j=25;
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();
		
		fstream = new FileWriter(res, true);
		out = new BufferedWriter(fstream);
		j=50;
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();
		
		/*out = new BufferedWriter(fstream);
		fullc = 0.2;
		j = 1;
		fstream = new FileWriter(res, true);
		out = new BufferedWriter(fstream);
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();
		
		out = new BufferedWriter(fstream);
		j=10;
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();
		
		out = new BufferedWriter(fstream);
		j=25;
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		out.close();
		
		out = new BufferedWriter(fstream);
		j=50;
		time = System.currentTimeMillis();
		se.randSeedMultiFull(samper, 10, graphDB1);
		inf.multiLin(10, samper, fullc, j, graphDB1);
		secs = (System.currentTimeMillis()-time)/1000;
		out.write("Running Full Multiple Influence on 20000 MHDA with 10 tracks, "+fullc+" fullc value and " +j+ " thresh value took " +secs+ " seconds");
		*/} 
		catch (IOException e) {}

		graphDB1.shutdown();
	}
	
		
		
		//Samples s = new Samples();
		//s.getSamp(dat, samper, 100000);
		/*GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
		registerShutdownHook (graphDB1);
		
		Iterator<Node> nodeIt;//used to cycle through nodes
		
		nodeIt = graphDB1.getAllNodes().iterator();
		int g = 0;
		
		while (nodeIt.hasNext()) {
		nodeIt.next();
			g++;
			System.out.println (g);
		}
		graphDB1.shutdown();*/
			
			
	private static void MultiTester (String samper, int gSize, String gType, int seeder, int seedSize, int model, int iters, int tracks, String fp) {
		try{
			FileWriter fstream = new FileWriter(fp,true);
			BufferedWriter out = new BufferedWriter(fstream);
			double[] time = new double[iters]; 
			double[] seedT = new double [iters];
			double mins;
			GraphDatabaseService graphDB1;
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
			registerShutdownHook(graphDB1);//graph set up
			SeedPicker se = new SeedPicker();
			InfSpread inf = new InfSpread();
			TLongHashSet seeds = new TLongHashSet();
			int[][] size = new int[iters][tracks];
			int i, j; 
			String seeding = null;
			String modeling = null;
			
			switch (seeder) {
			case 1: seeding = "Random seeds";break;
			case 2: seeding = "Degree Discount (IC)";break;
			case 3: seeding = "Degree Discount (Edge)";break;
			case 4: seeding = "Degree Discount (Weighted)";break;
			case 5: seeding = "Degree Discount (Weighted-If)";break;
			}
			
			switch (model) {
			case 1: modeling = "Wave Cascade";break;
			case 2: modeling = "Weighted Threshold";break;
			case 3: modeling = "Multiple Threshold";break;
			}
			
			for (i =0; i <iters; i++){
				seedT[i] = System.currentTimeMillis();
				System.out.println ("Iteration " +i);
				InfSpread.colourKill(samper, graphDB1);
				//inf.changKill(samper, graphDB1);
				
				switch (seeder) {
				case 1: seeds = se.randSeedMulti(samper, tracks, seedSize, graphDB1);break;
				case 2: seeds = se.degDisMulti(samper, seedSize, tracks, 0, graphDB1);break;
				case 3: seeds = se.degDisMulti(samper, seedSize, tracks, 1, graphDB1);break;
				case 4: seeds = se.degDisMulti(samper, seedSize, tracks, 2, graphDB1);break;
				case 5: seeds = se.degDisMulti(samper, seedSize, tracks, 3, graphDB1);break;
				}
				
				seedT[i] = (System.currentTimeMillis() - seedT[i])/1000;
				mins = seedT[i]/60;
				System.out.println ("Execution for " +seeding+ " on " +gSize+ " " + gType +" for "+seedSize+" seeds took " + seedT[i] + " seconds or " + mins+ " minutes");
				
				
				time[i] = System.currentTimeMillis();
				
				switch (model){
				case 1: inf.waveCas(seeds, samper, tracks, graphDB1);break;
				case 2: inf.linRunWeightedMulti(seeds, samper, tracks, graphDB1);break;
				case 3: inf.linRunMultiThresh(seeds, samper, tracks, graphDB1);break;
				}
				if (i%25 == 0) {
					System.gc();
				}
				time[i] = (System.currentTimeMillis() - time[i])/1000;
				mins = time[i]/60;
				System.out.println ("Execution for " +modeling+ " model using " +tracks+ " influence tracks on " +gSize+ " " + gType +" for "+seedSize+" seeds took " + time[i] + " seconds or " + mins+ " minutes");
				size[i] = InfSpread.multiResults(tracks, samper, false, graphDB1);
			}
			
			double[] sizeM = new double[tracks];
			double[] sizeSD = new double[tracks];
			double timeM = 0, seedM = 0;
			double seedSD = 0,timeSD = 0;
			double temp;
			
			for (i = 0; i < iters; i++) {
				seedM+=seedT[i];
				timeM+=time[i];
				for (j=0; j<tracks; j++) {
					sizeM[j]+=size[i][j];
				}
				
			}
			
			seedM=seedM/iters;
			timeM=timeM/iters;
			for (j=0; j<tracks; j++) {
				sizeM[j]=sizeM[j]/iters;
			}
			
			for (i = 0; i < iters; i++) {
				temp = seedT[i] - seedM;
				temp = (temp * temp)/iters;
				seedSD+=temp;
				temp = time[i] - timeM;
				temp = (temp * temp)/iters;
				timeSD+=temp;
				for (j=0; j<tracks; j++) {
					temp = size[i][j] - sizeM[j];
					temp = (temp * temp)/iters;
					sizeSD[j]+=temp;
				}
			}
			
			seedSD = Math.sqrt(seedSD);
			timeSD = Math.sqrt(timeSD);
			for (j=0; j<tracks; j++) {
				sizeSD[j] = Math.sqrt(sizeSD[j]);
			}
			
			out.write("Data average over " +iters+" runs for " +gSize+ " " + gType +" using " +seeding+" with seed size " + seedSize+ " using model " + modeling+ " with " +tracks+ " tracks of influence is as follows:\r\n");
			out.write("Average time to collect seed set: " +seedM+"\r\n");
			out.write("Standard deviation of seed mean: " +seedSD+"\r\n");
			out.write("Average time to perform influence cascade: " +timeM+"\r\n");
			out.write("Standard deviation of cascade mean: " +timeSD+"\r\n");
			out.write("Average size of 'our' final influenced set: " +sizeM[0]+"\r\n");
			out.write("Standard deviation of 'our' size mean: " +sizeSD[0]+"\r\n");
			for (j=1; j<tracks; j++) {
				out.write("Average size of final influenced set " +j+ ": " +sizeM[j]+"\r\n");
				out.write("Standard deviation of size mean " +j+ ": " +sizeSD[j]+"\r\n");
			}
			out.write("\r\n");
			out.close();
			graphDB1.shutdown();
		}
		catch (Exception e) {}
	}
	private static void Tester (String samper, int gSize, String gType, int seeder, int seedSize, int model, int iters, boolean apphend, String fp) {
		try{
			FileWriter fstream = new FileWriter(fp,apphend);
			BufferedWriter out = new BufferedWriter(fstream);
			double[] time = new double[iters]; 
			double[] seedT = new double [iters];
			double mins;
			GraphDatabaseService graphDB1;
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (samper);
			registerShutdownHook(graphDB1);//graph set up
			SeedPicker se = new SeedPicker();
			InfSpread inf = new InfSpread();
			TLongHashSet seeds = new TLongHashSet();
			int[] size = new int[iters];
			int i; 
			String seeding = null;
			String modeling = null;
			
			switch (seeder) {
			case 1: seeding = "Max Deg";break;
			case 2: seeding = "Min Deg";break;
			case 3: seeding = "Random seeds";break;
			case 4: seeding = "Degree Discount (IC)";break;
			case 5: seeding = "Degree Discount (Edge)";break;
			case 6: seeding = "Degree Discount (Weighted)";break;
			case 7: seeding = "Degree Discount (Weighted-If)";break;
			}
			
			switch (model) {
			case 1: modeling = "Linear Threshold";break;
			case 2: modeling = "Independent Cascade";break;
			}
			
			for (i =0; i <iters; i++){
				seedT[i] = System.currentTimeMillis();
				System.out.println ("Iteration " +i);
				switch (seeder) {
				case 1: seeds = se.degMaxMin(samper, seedSize, true, graphDB1);break;
				case 2: seeds = se.degMaxMin(samper, seedSize, false, graphDB1);break;
				case 3: seeds = se.randSeed(samper, seedSize,graphDB1);break;
				case 4: seeds = se.degDis(samper, seedSize, false, 0,graphDB1);break;
				case 5: seeds = se.degDis(samper, seedSize, false, 1,graphDB1);break;
				case 6: seeds = se.degDis(samper, seedSize, false, 2,graphDB1);break;
				case 7: seeds = se.degDis(samper, seedSize, false, 3,graphDB1);break;
				}
				
				seedT[i] = (System.currentTimeMillis() - seedT[i])/1000;
				mins = seedT[i]/60;
				System.out.println ("Execution for " +seeding+ " on " +gSize+ " " + gType +" for "+seedSize+" seeds took " + seedT[i] + " seconds or " + mins+ " minutes");
				
				
				time[i] = System.currentTimeMillis();
				
				switch (model){
				case 1: size[i] = inf.linRun(seeds, samper, graphDB1);break;
				case 2: size[i] = inf.indCas(seeds, samper,graphDB1);break;
				}
				if (i%25 == 0) {
					System.gc();
				}
				time[i] = (System.currentTimeMillis() - time[i])/1000;
				mins = time[i]/60;
				System.out.println ("Execution for " +modeling+ " model on " +gSize+ " " + gType +" for "+seedSize+" seeds took " + time[i] + " seconds or " + mins+ " minutes");
				System.out.println ("Final influence set size: " + size[i]);
			}
			
			double seedM = 0, timeM = 0, sizeM = 0;
			double seedSD = 0,timeSD = 0,sizeSD = 0;
			double temp;
			
			for (i = 0; i < iters; i++) {
				seedM+=seedT[i];
				timeM+=time[i];
				sizeM+=size[i];
			}
			
			seedM=seedM/iters;
			timeM=timeM/iters;
			sizeM=sizeM/iters;
			
			for (i = 0; i < iters; i++) {
				temp = seedT[i] - seedM;
				temp = (temp * temp)/iters;
				seedSD+=temp;
				temp = time[i] - timeM;
				temp = (temp * temp)/iters;
				timeSD+=temp;
				temp = size[i] - sizeM;
				temp = (temp * temp)/iters;
				sizeSD+=temp;
			}
			
			seedSD = Math.sqrt(seedSD);
			timeSD = Math.sqrt(timeSD);
			sizeSD = Math.sqrt(sizeSD);
			
			out.write("Data average over " +iters+" runs for " +gSize+ " " + gType +" using " +seeding+" with seed size " + seedSize+ " using model " + modeling+ " is as follows:\r\n");
			out.write("Average time to collect seed set: " +seedM+"\r\n");
			out.write("Standard deviation of seed mean: " +seedSD+"\r\n");
			out.write("Average time to perform influence cascade: " +timeM+"\r\n");
			out.write("Standard deviation of cascade mean: " +timeSD+"\r\n");
			out.write("Average size of final influenced set: " +sizeM+"\r\n");
			out.write("Standard deviation of size mean: " +sizeSD+"\r\n");
			out.write("\r\n");
			out.close();
			graphDB1.shutdown();
		}
		catch (Exception e) {}
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
	public static void parseAndSamp (String origin, String dat, String samper, int size) {
		Parser parser = new Parser(origin);
		parser.Parse(dat);
		Samples samp = new Samples();
		samp.getSnow(dat, samper, size);
		//samp.getSamp(dat, samper+"2", size);
	}
	
	public static void testG(String dat) {
		GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (dat);
		graphDB1.shutdown();
		Map <String, Object> properties  = MapUtil.map("weight", 0.4, "count", 2);
		BatchInserter graphDB;//said inserter
		//set up relationship type and initilise batch inserter
		RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
		graphDB = BatchInserters.inserter (dat);
		graphDB.createNode(1,properties);
		properties  = MapUtil.map("weight", 0.6);
		graphDB.createNode(2,properties);
		properties  = MapUtil.map("weight", 0.01);
		graphDB.createNode(3,properties);
		properties  = MapUtil.map("weight", 0.6);
		graphDB.createNode(4,properties);
		properties  = MapUtil.map("weight", 0.23);
		graphDB.createNode(5,properties);
		properties  = MapUtil.map("weight", 0.7);
		graphDB.createNode(6,properties);
		properties  = MapUtil.map("weight", 0.064);
		graphDB.createNode(7,properties);
		properties  = MapUtil.map("weight", 0.6);
		graphDB.createNode(8,properties);
		properties  = MapUtil.map("weight", 0.2);
		graphDB.createNode(9,properties);
		properties  = MapUtil.map("weight", 0.4);
		graphDB.createNode(10,properties);
		properties  = MapUtil.map("weight", 0.7);
		graphDB.createNode(11,properties);
		properties  = MapUtil.map("weight", 0.3);
		graphDB.createNode(12,properties);
		properties  = MapUtil.map("weight", 0.7);
		graphDB.createNode(13,properties);
		properties  = MapUtil.map("weight", 0.3);
		graphDB.createNode(14,properties);
		properties  = MapUtil.map("weight", 0.76);
		graphDB.createNode(15,properties);
		properties  = MapUtil.map("weight", 0.02);
		graphDB.createNode(16,properties);
		properties  = MapUtil.map("weight", 0.24);
		graphDB.createNode(17,properties);
		properties  = MapUtil.map("weight", 0.76);
		graphDB.createNode(18,properties);
		properties  = MapUtil.map("weight", 0.034);
		graphDB.createNode(19,properties);
		properties  = MapUtil.map("weight", 0.18);
		graphDB.createNode(20,properties);
		properties  = MapUtil.map("weight", 0.1);
		graphDB.createNode(21,properties);
		properties  = MapUtil.map("weight", 0.7);
		graphDB.createNode(22,properties);
		properties  = MapUtil.map("weight", 0.2);
		graphDB.createNode(23,properties);
		properties  = MapUtil.map("weight", 0.8);
		graphDB.createNode(24,properties);
		properties  = MapUtil.map("weight", 0.2);
		graphDB.createNode(25,properties);
		properties  = MapUtil.map("weight", 0.4);
		graphDB.createNode(26,properties);
		properties  = MapUtil.map("weight", 0.3);
		graphDB.createNode(27,properties);
		properties  = MapUtil.map("weight", 0.7);
		graphDB.createNode(28,properties);
		properties  = MapUtil.map("weight", 0.9);
		graphDB.createNode(29,properties);
		properties  = MapUtil.map("weight", 0.45);
		graphDB.createNode(30,properties);
		properties  = MapUtil.map("weight", 0.2);
		graphDB.createNode(31,properties);
		properties  = MapUtil.map("weight", 0.77);
		graphDB.createNode(32,properties);
		properties  = MapUtil.map("weight", 0.1);
		graphDB.createNode(33,properties);
		properties  = MapUtil.map("weight", 0.6);
		graphDB.createNode(34,properties);
		properties  = MapUtil.map("weight", 0.1);
		graphDB.createNode(35,properties);
		properties  = MapUtil.map("weight", 0.4);
		graphDB.createNode(36,properties);
		properties  = MapUtil.map("weight", 0.37);
		graphDB.createNode(37,properties);
		properties  = MapUtil.map("weight", 0.002);
		graphDB.createNode(38,properties);
		properties  = MapUtil.map("weight", 0.3);
		graphDB.createNode(39,properties);
		properties  = MapUtil.map("weight", 0.3);
		graphDB.createNode(40,properties);
		properties  = MapUtil.map("weight", 0.1);
		graphDB.createNode(41,properties);
		properties  = MapUtil.map("weight", 0.23);
		graphDB.createNode(42,properties);
		properties  = MapUtil.map("weight", 0.4);
		graphDB.createNode(43,properties);
		properties  = MapUtil.map("weight", 0.6);
		graphDB.createNode(44,properties);
		properties  = MapUtil.map("weight", 0.4);
		graphDB.createNode(45,properties);
		properties  = MapUtil.map("weight", 0.79);
		graphDB.createNode(46,properties);
		properties  = MapUtil.map("weight", 0.02);
		graphDB.createNode(47,properties);
		properties  = MapUtil.map("weight", 0.9);
		graphDB.createNode(48,properties);
		properties  = MapUtil.map("weight", 0.61);
		graphDB.createNode(49,properties);
		properties  = MapUtil.map("weight", 0.3);
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
		properties = MapUtil.map("weight", 0.064);
		graphDB.createRelationship(7, 16, mailed, properties);
		
		properties = MapUtil.map("weight", 0.04);
		graphDB.createRelationship(8, 20, mailed, properties);
		properties = MapUtil.map("weight", 0.7);
		graphDB.createRelationship(8, 10, mailed, properties);
		
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
		properties = MapUtil.map("weight", 0.23);
		graphDB.createRelationship(26, 28, mailed, properties);
		
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
		
		graphDB.shutdown();
		
		//GraphDatabaseService graphDB1;
		graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (dat);
		Transaction tx = graphDB1.beginTx();
		Node bound = graphDB1.getReferenceNode();
		bound.setProperty("bound", (long) 50);
		tx.success();
		tx.finish();
		graphDB1.shutdown();
		
		Samples s = new Samples();
		s.addCounts(dat);
	}
}


























