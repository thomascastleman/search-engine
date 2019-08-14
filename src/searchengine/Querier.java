package searchengine;

import java.util.HashMap;
import java.util.Set;

public class Querier {
	
	private Index index;
	
	// construct a querier off of an Index
	public Querier(Index _index) {
		this.index = _index;
	}
	
	// make free text query
	public void query(String query, int size) {
		// split query into words
		String[] words = query.toLowerCase().split(" ");
		
		HashMap<Integer, Double> scores;
		HashMap<Integer, Double> results = new HashMap<Integer, Double>();
		
		// for each word in query
		for (String w : words) {
			w = Util.strip(w);
			
			// if non-empty and not stop word
			if (!w.equals("") && !Util.isStopWord(w)) {
				System.out.println("Valid term: " + w); // debug
				
				
				w = PorterStemmer.getStem(w);	// stem word
				scores = this.index.getScores(w);	// get associated doc scores
				
				// if stem exists in corpus
				if (scores != null) {
					// for each doc / score pair
					for (int n : scores.keySet()) {
						Double score = scores.get(n);
						Double cumulative = results.get(n);
						
						// add to cumulative query scores
						if (cumulative == null) {
							results.put(n, score);
						} else {
							results.put(n, cumulative + score);
						}
					}
				}
			}
		}
		
		Set<Integer> ids = results.keySet();
		
		if (ids.size() > 0) {
			
			String[] documents = new String[size < ids.size() ? size : ids.size()];
			Integer max_id = null;
			
			// extract and format documents ordered by score
			for (int i = 0; i < documents.length; i++) {
				for (int id : ids) {
					if (max_id == null || results.get(id) > results.get(max_id)) {
						max_id = id;
					}
				}
				
				String[] docInfo = this.index.getDocument(max_id).split(",");
				
				documents[i] = (i + 1) + ") " + docInfo[0] + " (" + docInfo[1] + ")";
				results.remove(max_id);
				max_id = null;
			}
			
			// display query results
			for (String s : documents) {
				System.out.println(s);
			}
			
		} else {
			System.out.println("No relevant results found.");
		}
	}
}
