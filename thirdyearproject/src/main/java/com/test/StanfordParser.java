package com.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.LuceneBatchInserterIndexProvider;


public class StanfordParser {

	private String filePath;
	private String fileComments = "" ;
	private ArrayList<String> dest = new ArrayList<String>();
	
	
	StanfordParser(String filePath)
	{
		this.filePath = filePath;
	}
	
	public boolean Parse() 
	{
		boolean error = false;
		try
		{
			//set up file reader
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader (new InputStreamReader(in));
			
			String strLine;//holds full line
			String tempArr[];//holds line once split into from and to
			String newCheckF = "-1";//used to avoid unneeded indexing
			String newCheckS = "-1";//ditto
			
			//create and set up database
			GraphDatabaseService graphDB1;
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase ("C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/data");
			registerShutdownHook( graphDB1 );//helps in safe shut down
			
			graphDB1.shutdown();//need graph close so batch inserter can access
			
			BatchInserter graphDB;//said inserter
			
			long fromNode = 0;//store long version of from node
			long toNode = 0;//store long version of to node
			
			//set up relationship type and initilise batch inserter
			RelationshipType mailed = DynamicRelationshipType.withName( "MAILED" );
			graphDB = BatchInserters.inserter ("C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/data");
			
			//set up batch indexer
			BatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider( graphDB );
			BatchInserterIndex ids = indexProvider.nodeIndex( "ids", MapUtil.stringMap( "type", "exact" ) );
			ids.setCacheCapacity( "id", 100000 );
			
			//these just count nodes and edges made to ensure they are all created. Cross reference with comments at top of data set.
			int i = 0;
			int j =0;
		
			//first file pass, reading and creating all nodes with edges going from them
			while ((strLine = br.readLine()) != null)   {
				
				//matt's if to store comments
				if(strLine.startsWith("#") == true )
				{
					fileComments += strLine + "\n";
				}
				else//now we move onto the meat
				{
					tempArr = strLine.split("\t");//split the line
					if (!newCheckF.equals(tempArr[0])) {//checks if we moved onto to a new node
							newCheckF = tempArr[0];
							Map <String, Object> properties = MapUtil.map( "id", tempArr[0] );//this makes the id property for the node
							fromNode = graphDB.createNode( properties );//creates node
							ids.add( fromNode, properties );//indexes
							
							//just some sanity check printlines
							System.out.println(tempArr[0]);
							j++;
							System.out.println("Making node:" + j);
					}
				}
			}
			
			ids.flush();//this adds all nodes indexed so we can use them later. must be done sparsely
			
			//reset file reader:
			in.close();
			fstream = new FileInputStream(filePath);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			//now we add any 'to nodes' that weren't added in the first pass:
			while ((strLine = br.readLine()) != null)   {
				if (strLine.startsWith ("#") == false){//makes sure its not a comment
					tempArr = strLine.split("\t");
					
					//if checks node is not already indexed, and has not yet been met in this loop. The array list acts as a temporary index
					//meaning we dont have to keep flushing the indexer.
					if (ids.get("id", tempArr[1]).getSingle()== null && dest.contains(tempArr[1]) == false){
						
						//create node, properties and index it
						Map <String, Object> properties = MapUtil.map( "id", tempArr[1] );
						toNode = graphDB.createNode( properties );
						ids.add( toNode, properties );
						
						//some tracking print lines
						System.out.println(tempArr[1]);
						j++;
						System.out.println("Making node:" + j);
						
						//adds node to temp index, and sets newCheckS, which is used later.
						dest.add(tempArr[1]);
						newCheckS = tempArr[1];
					}
				}
			}
			
			//we flush, free up the mem of the array list, and reset the reader.
			ids.flush();
			dest.clear();
			in.close();
			fstream = new FileInputStream(filePath);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			//now we pass through and create edges
			while ((strLine = br.readLine()) != null)   {
				if (strLine.startsWith ("#") == false){//makes sure we're not reading a comment
					
					tempArr = strLine.split("\t");
					
					//see's if fromnode is already set to the node we need to prevent unneeded index searches. 
					//if not, we get the node and carry on.
					if (newCheckF != tempArr[0] ) {
						fromNode = ids.get("id", tempArr[0]).getSingle();
						newCheckF = tempArr[0];
					}
					
					//does the same for tonode.
					if (newCheckS != tempArr[1] ) {
						toNode = ids.get("id", tempArr[1]).getSingle();
						newCheckS = tempArr[1];
					}
					
					//creates the relationship
					graphDB.createRelationship( fromNode, toNode, mailed, null );
					
					//tracking printlines
					i++;
					System.out.println ("Making: " + i);
				}
			}
			
			//close the stream, indexer and database.
			in.close();
			indexProvider.shutdown();
			graphDB.shutdown();
			
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

