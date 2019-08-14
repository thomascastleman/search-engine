package searchengine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	
	private static Pattern apostrophe = Pattern.compile("(\\w+)'(\\w+)");
	private static String[] stops = {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
	private static Set<String> stopWords = new HashSet<String>(Arrays.asList(stops));
	
	public static String strip(String word) {
		String stripped = "";
		// handle apostrophes
		if (word.matches("(\\w+)'(\\w+).*")) {				
			Matcher m = apostrophe.matcher(word);
			
			if (m.find()) {
				stripped = m.group(1) + "'" + m.group(2);
			}
			
		} else {
			stripped = word.replaceAll("\\W", "");
		}
		
		return stripped;
	}
	
	// determine if given word is ignorable
	public static boolean isStopWord(String w) {
		return stopWords.contains(w);
	}

}
