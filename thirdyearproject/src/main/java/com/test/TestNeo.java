package com.test;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.ReadableIndex;

public class TestNeo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService graphDB;
		Node firstNode;
		Node secondNode;
		Relationship relationship;
		
		graphDB = new GraphDatabaseFactory().newEmbeddedDatabase ("C:/Users/Fusian/workspace/thirdyearproject/src/main/resources/data");
		registerShutdownHook( graphDB );
		Index<Node> nodeIndex;
		Transaction tx = graphDB.beginTx();
		
		try
		{
			nodeIndex = graphDB.index().forNodes( "nodes" );
			
			firstNode = graphDB.createNode();
			firstNode.setProperty("message", "James");
			nodeIndex.add( firstNode, "message", "James" );
			firstNode = graphDB.createNode();
			firstNode.setProperty("message", "Andrew");
			nodeIndex.add( firstNode, "message", "Andrew" );
			firstNode = graphDB.createNode();
			firstNode.setProperty("message", "Chris");
			nodeIndex.add( firstNode, "message", "Chris" );
			//relationship = firstNode.createRelationshipTo (secondNode, RelTypes.KNOWS);
			//relationship.setProperty ("messgae", "budwiser");
			
			Node foundUser = nodeIndex.get("message", "Chris").getSingle();
			System.out.println (foundUser.getProperty("message"));
			
			tx.success();
		}
		finally {
			tx.finish();
		}
		
		graphDB.shutdown();
		/*System.out.print( firstNode.getProperty( "message" ) + " " );
		System.out.print( relationship.getType());
		System.out.print( " " + secondNode.getProperty( "message" ) );*/
	}
	
	private static enum RelTypes implements RelationshipType {
		KNOWS
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
