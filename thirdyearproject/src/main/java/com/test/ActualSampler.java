package com.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import gnu.trove.set.hash.TLongHashSet;
public class ActualSampler {
	private String filePath;
	private String fileComments = "" ;
	
	ActualSampler(String filePath)
	{
		this.filePath = filePath;
	}
	
	public boolean Parse(String filep) 
	{
		boolean error = false;
		try
		{
			//set up file reader
			long bound=0;
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader (new InputStreamReader(in));
			TLongHashSet nodes = new TLongHashSet(1000000);
			String strLine;//holds full line
			String tempArr[];//holds line once split into from and to
			//create and set up database
			GraphDatabaseService graphDB1;
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (filep);
			registerShutdownHook( graphDB1 );//helps in safe shut down
			graphDB1.shutdown();//need graph close so batch inserter can access
			
			BatchInserter graphDB;//said inserter
			
			//set up relationship type and initilise batch inserter
			RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
			graphDB = BatchInserters.inserter (filep);
			
			
			long id;
			long id2;
			int i = 0;
			//first file pass, reading and creating all nodes with edges going from them
			while ((strLine = br.readLine()) != null)   {
				
				//matt's if to store comments
				if(strLine.startsWith("#") == true)
				{
						fileComments += strLine + "\n";
				}
				else//now we move onto the meat
				{
					tempArr = strLine.split("\t");//split the line
					id = Long.parseLong(tempArr[0]) + 1;
					id2 = Long.parseLong(tempArr[1]) + 1;
					if (nodes.add(id)){
						graphDB.createNode(id, null);
						if (id > bound) {bound=id;}
						System.out.println ("Making node: " + id);
					}
					if (nodes.add(id2)){
						graphDB.createNode(id2, null);
						if (id2 > bound) {bound=id2;}
						System.out.println ("Making node: " + id2);
					}
					graphDB.createRelationship( id, id2, mailed, null);
					i++;
					System.out.println ("Making relationship: " + i);
				}
			}
			graphDB.shutdown();
			
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (filep);
			registerShutdownHook (graphDB1);
			Transaction tx = graphDB1.beginTx();
			Node ref = graphDB1.getReferenceNode();
			ref.setProperty("bound", bound);
			tx.success();
			tx.finish();
			graphDB1.shutdown();
			Node start;
			int count;
			int txc = 0;
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (filep);
			registerShutdownHook (graphDB1);
			Iterator<Node> nodeIt = graphDB1.getAllNodes().iterator();//used to cycle through nodes
			Iterator<Relationship> reli;//cycles through edges
			
			tx = graphDB1.beginTx();
			while (nodeIt.hasNext()) {
				//tx = graphDB1.beginTx();
				start = nodeIt.next();
				reli = start.getRelationships(Direction.OUTGOING).iterator();
				count = 0;
				while (reli.hasNext()) {
					reli.next();
					count++;
				}
				
				graphDB1.getNodeById(start.getId()).setProperty("count", (double) count);
				System.out.println ("Node has " + count + " edges. Adding.");
				System.out.println (graphDB1.getNodeById(start.getId()).getProperty("count"));
				txc++;
				
				if (txc > 100000) {
					tx.success();
					tx.finish();
					txc = 0;
					tx = graphDB1.beginTx();
				}
			} 
			tx.success();
			tx.finish();
			graphDB1.shutdown();
		}
		catch (FileNotFoundException e){
			System.err.println("File not found!");
			error = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("I/O Error!");
			error = true;
		}
		return error;
	}
	
	public String getComments() 
	{
		return fileComments;
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

