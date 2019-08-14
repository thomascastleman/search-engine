package searchengine;

public class Main {

	public static void main(String[] args) {
		
		// CREATING AN INDEX FROM A CORPUS --------------------------
		
//		IndexConstructor indexer = new IndexConstructor("/home/tcastleman/workspace/searchengine/src/animalcorp.xml");
//		Index index = new Index(indexer);
//		index.writeIndexFile("/home/tcastleman/Desktop/animalindex.txt");

		
		// MAKING QUERIES ----------------------
		
		Index ind = new Index("/home/tcastleman/Desktop/animalindex.txt");
		Querier q = new Querier(ind);
		
		q.query("What is cat burning?", 10);
	}

}