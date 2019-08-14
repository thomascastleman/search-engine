package searchengine;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Index {
	
	// stems to doc ID's to scores
	private HashMap<String, HashMap<Integer, Double>> stems = new HashMap<String, HashMap<Integer, Double>>();
	
	// document info strings "<title>,<url>"
	private String[] documents;
	
	// construct index from an IndexConstructor
	public Index(IndexConstructor i) {
		ArrayList<Integer> ids;
		HashMap<Integer, Double> scores;
		Document doc;
		
		documents = new String[i.idsToDocs.keySet().size()];
		
		// for every stem
		for (String s : i.stemsToDocs.keySet()) {
			ids = i.stemsToDocs.get(s);
			
			scores = new HashMap<Integer, Double>();
			
			// for each doc this stem appears in
			for (int n : ids) {
				// add score to map
				doc = i.idsToDocs.get(n);
				scores.put(n, doc.stemsToScores.get(s));
				
				// keep track of doc info
				if (documents[n] == null) {
					documents[n] = doc.title + "," + doc.url;
				}
			}
			
			// add map of ids to scores to this index's map of stems
			this.stems.put(s, scores);
		}
	}
	
	// construct index from index file
	public Index(String file) {
		try {
			FileReader fr = new FileReader(file);
			Scanner s = new Scanner(fr);
			String line;
			
			if (s.hasNextLine()) {
				// read corpus size
				this.documents = new String[Integer.parseInt(s.nextLine())];
				
				// read document infos
				for (int i = 0; i < this.documents.length; i++) {
					if (s.hasNextLine()) this.documents[i] = s.nextLine();
				}
				
				// read scores
				while (s.hasNextLine()) {
					line = s.nextLine();
					
					HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
					
					String[] comma = line.split(",");
					String[] semi = comma[1].split(";");
					
					// for each document/score pair
					for (String pair : semi) {
						String[] colon = pair.split(":");
						scores.put(Integer.parseInt(colon[0]), Double.parseDouble(colon[1]));
					}
					
					// add stem to score pairs
					this.stems.put(comma[0], scores);
				}
			}
			
			fr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// serialize this index into a given file
	public void writeIndexFile(String file) {
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			writer.write(this.documents.length + "\n");
			
			for (int i = 0; i < this.documents.length; i++) {
				writer.write(this.documents[i] + "\n");
			}
			
			for (String s : this.stems.keySet()) {
				writer.write(s + ",");
				HashMap<Integer, Double> scores = this.stems.get(s);
				for (int n : scores.keySet()) {
					double score = scores.get(n);
					
					writer.write(n + ":" + score + ";");
				}
				writer.write('\n');
			}
			
			writer.close();
			System.out.println("Finished writing index to " + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// get the document / score pairings associated with stem
	public HashMap<Integer, Double> getScores(String stem) {
		return this.stems.get(stem);
	}
	
	// get document info
	public String getDocument(int i) {
		return this.documents[i];
	}

}