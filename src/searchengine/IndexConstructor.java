package searchengine;


import java.io.*;
import java.util.*;

public class IndexConstructor {
	
	private FileReader fr;
	private Scanner s;
	public HashMap<String, ArrayList<Integer>> stemsToDocs = new HashMap<String, ArrayList<Integer>>();
	public HashMap<Integer, Document> idsToDocs = new HashMap<Integer, Document>();
	
	// construct an index from a corpus file
	public IndexConstructor(String file) {
		ArrayList<Document> docs = this.readDocuments(file);
		
		// add IDs linked to doc objects, finalize stem scores
		for (int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i);
			this.idsToDocs.put(i, doc);
			doc.calculateScores(docs.size());
		}
	}
	
	// read document contents from file into Doc objects
	private ArrayList<Document> readDocuments(String file) {
		ArrayList<Document> docs = new ArrayList<Document>();
		int id = 0;
		
		try {
			fr = new FileReader(file);
			s = new Scanner(fr);
			
			String docText = "";
			String line;
			
			while (s.hasNextLine()) {
				line = s.nextLine();
				docText += line + " ";
				if (line.matches("</doc>")) {
					docs.add(new Document(id++, docText, this));
					docText = "";
				}
			}
			
			fr.close();
			
		} catch (Exception e) {
			System.out.println("ERROR: FAILED TO READ CORPUS");
		}
		
		return docs;
	}
}