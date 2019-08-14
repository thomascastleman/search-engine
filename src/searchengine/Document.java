package searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
	
	private int id;
	public String url;	// document URL
	public String title;	// title of document
	private IndexConstructor index; // reference to parent indexer
	
	private double maxFreq = 0.0;	// maximum frequency of stem
	
	// map of every stem in doc to its corresponding score with this doc
	public HashMap<String, Double> stemsToScores = new HashMap<String, Double>();
	
	public Document(int id, String text, IndexConstructor index) {
		this.id = id;
		this.index = index;
		
		// extract document content & count stem frequencies
		this.getFrequencies(this.parseText(text));
	}
	
	// extract document metadata, and return only document content
	private String parseText(String text) {
		Pattern header = Pattern.compile("<doc url=\"(.*)\" title=\"(.*)\">(.*)</doc>");
		Matcher m = header.matcher(text);
		
		// extract URL, title, and document content
		if (m.find()) {
			this.url = m.group(1);
			this.title = m.group(2);
			return m.group(3);
		}
		return "";
	}
	
	// strip content, count stem frequencies and divide all by max frequency
	private void getFrequencies(String content) {
		String[] words = content.toLowerCase().split(" ");
		
		for (String w : words) {
			w = Util.strip(w);
			
			// if non-empty and not stop word
			if (!w.equals("") && !Util.isStopWord(w)) {
				w = PorterStemmer.getStem(w);	// stem word
				
				// retrieve current frequency
				Double freq = this.stemsToScores.get(w);
				if (freq == null) {
					freq = 0.0;
					
					ArrayList<Integer> ids = this.index.stemsToDocs.get(w);
					if (ids == null) {
						// track stem with this doc
						ids = new ArrayList<Integer>();
						ids.add(this.id);
						this.index.stemsToDocs.put(w, ids);
					} else {
						// add doc to docs associated with this stem
						ids.add(this.id);
					}
				}
				
				// add frequency to map
				this.stemsToScores.put(w, freq + 1);
				
				// if new max frequency, update
				if (freq + 1 > this.maxFreq) {
					this.maxFreq = freq + 1;
				}
			}
		}
	}
	
	// finalize scores for each stem by multiplying by inverse document frequency
	public void calculateScores(int totalDocs) {
		// for each stem in this document
		for (String s : this.stemsToScores.keySet()) {
			double score = this.stemsToScores.get(s);
			score *= Math.log((double) totalDocs / this.index.stemsToDocs.get(s).size()) / this.maxFreq;
			this.stemsToScores.put(s, score);
		}
	}

}