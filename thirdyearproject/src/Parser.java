import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import gnu.trove.set.hash.TLongHashSet;
public class Parser {
	private String filePath;
	private String fileComments = "" ;
	
	Parser(String filePath)
	{
		this.filePath = filePath;
	}
	
	public boolean Parse(String filep) 
	{
		boolean error = false;
		try
		{
			//set up file reader
			long bound=0;//holds highest id of a node
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader (new InputStreamReader(in));
			TLongHashSet nodes = new TLongHashSet(1000000);//holds id of nodes
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
					if (nodes.add(id)){//functions returns false if node already in set, and adds it if not
						graphDB.createNode(id, null);//create node in graph
						if (id > bound) {bound=id;}//update bound var
						System.out.println ("Making node: " + id);
					}
					if (nodes.add(id2)){//check for second node and do the same as above
						graphDB.createNode(id2, null);
						if (id2 > bound) {bound=id2;}
						System.out.println ("Making node: " + id2);
					}
					graphDB.createRelationship( id, id2, mailed, null);//create the relationship between the two nodes
					i++;
					System.out.println ("Making relationship: " + i);
				}
			}
			graphDB.shutdown();//shutdown graph
			
			//following is setup and shutdown needed to add bound as property to reference node of graph
			graphDB1 = new GraphDatabaseFactory().newEmbeddedDatabase (filep);
			registerShutdownHook (graphDB1);
			Transaction tx = graphDB1.beginTx();
			Node ref = graphDB1.getReferenceNode();
			ref.setProperty("bound", bound);
			tx.success();
			tx.finish();
			graphDB1.shutdown();
			
			Samples s = new Samples();
			s.addCounts(filep);//calls function to add edge counts to graph - used in other functions
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

