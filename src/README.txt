
HDS Final Project - Search Engine
By Thomas Castleman

This project was heavily drawn from Brown CS18's search project (https://cs.brown.edu/courses/csci0180/content/projects/search-scala.pdf)


------------------- THE CORPUS -------------------
The index constructor works with XML files that are comprised of <doc> tags which contain the plaintext (excluding metadata) of an entire wikipedia document. I created several corpuses (corpora?) by pulling broad categories of pages from the Wikipedia exports page (https://en.wikipedia.org/wiki/Special:Export).

When pulled directly from here, these wiki pages are very hard to read, and so before they can be used with my engine they need to be stripped of a lot of stuff.

To accomplish this I used a script written by Giuseppe Attardi (http://medialab.di.unipi.it/wiki/Wikipedia_Extractor) to remove much of the tags from the database dump and format each document between <doc> ... </doc> tags.

From here, through Sublime Text, I was able to remove all tags that weren't these <doc> tags through the following regular expression:

<[^/\?doc].*>

as well as remove all "Category" and "Portal" pages, which often don't really have much plain text that I could use, and were cluttering up my results. For this the following expression was used:

<doc.+?>\n(Category|Portal):(.|\n)*?</doc>

From here the corpus is ready to be used to construct an index. Ideally, I would have built this stripping of the raw XML into the indexer itself, so this preprocessing wouldn't have been necessary, but I chose to focus my energy more on the nature of indexing and efficiently searching instead.

In the /src/ directory I've included a properly-formatted corpus which contains articles spanning several animal categories, such as cats, dogs, and koalas, to use as a test corpus.


------------------- CONSTRUCTING AN INDEX (FROM A CORPUS) -------------------

I left the following three lines in the main function to illustrate how one constructs an index to store for later use:

	IndexConstructor indexer = new IndexConstructor("/home/tcastleman/workspace/searchengine/animalcorp.xml");
	Index index = new Index(indexer);
	index.writeIndexFile("/home/tcastleman/Desktop/animalindex.txt");

The IndexConstructor class is responsible for taking a corpus (in the format specified above), and extracting ALL the important information we need. In short, it scores every non-negligible word stem in the corpus with every document that it appears in.

In truth, most of this parsing and scoring is offloaded onto the Document class, as the IndexConstructor takes the corpus and breaks it up into Document objects, which then each parse themselves and generate stem-to-score hashmaps.

The Index class then takes the important information stored in each Document in the IndexConstructor instance, and extracts it into a more space-efficient format. Specifically, it stores a map of the following nature:

MAP: stem --> (MAP: ID --> score)

Essentially, it takes all the information the IndexConstructor just extracted and stores ONLY the meaningful stuff, in a reduced data structure. It is this same Index class that will be used by the Querier to make searches later on.

As you can see from the third line, the Index is then serialized in the following form:

	- first, an integer: the number of documents in the corpus
	- then, each line looks like: <document title>,<document URL>
	- once all documents are stored, the rest of the index takes the following form:
		- per each line: <word stem>,<doc0 ID>:<doc0 score>;<doc1 ID>:<doc1 score>; ... <docN ID>:<docN score>;


------------------- USING AN INDEX (MAKING QUERIES) -------------------

The next three lines exemplify how an existing index can be used to make searches:

	Index ind = new Index("/home/tcastleman/Desktop/animalindex.txt");
	Querier q = new Querier(ind);
	q.query("What is cat burning?", 10);

Notice that there are two ways to construct an Index, which is kinda nice:
	- from an IndexConstructor instance, for the purpose of extracting the essential info
	- from a text file, for querying a previously indexed corpus

From here the Querier is given an Index to reference for scoring of stems, and this can be used to make free text queries through the "query" function, which takes in a string free text query as well as an integer maximum number of search results to display.

The Querier strips queries down to ignore stop words, and stems relevant terms to search in the Index. In this example only the words "cat" and "burning" would be used in the search and all punctuation as well as capitalization was removed.

------------------- SCORING -------------------

Stems (which were attained using the Java Porter Stemmer implementation found on Porter's site, http://www.tartarus.org/~martin/PorterStemmer) are scored with documents by multiplying two pieces of information:

	- the term frequency of a stem
	- the inverse document frequency of a stem

This approach was taken from CS18's project. 

The term frequency is simply the frequency of a stem in a document, over the MAXIMUM frequency of any term in that document. This normalizes the frequency data, which is better than just straight-up frequency.

Inverse document frequency of a term is given by the log of (the TOTAL number of documents over the number of documents that this term appears in). This means that for terms that only appear in a few documents, these documents will be weighted as very relevant, whereas if something appears in almost every document in the corpus, it is less likely to appear important.