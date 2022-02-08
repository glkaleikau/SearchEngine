import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class to build an inverted index using clean stemmed words from a directory
 * or files to show the amount of times a word appears in each file
 * 
 * @author genesiskaleikau
 *
 */
public class InvertedIndex {
	/**
	 * The inverted index used to sort the words in a file or directory.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> inverted;

	/**
	 * The wordCount data structure for each file.
	 */
	private final TreeMap<String, Integer> wordCount;

	/**
	 * Empty constructor for creating an inverted index.
	 */
	public InvertedIndex() {
		inverted = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		wordCount = new TreeMap<String, Integer>();
	}

	/**
	 * Retrieves word count data structure.
	 * 
	 * @return word count data structure
	 */
	public Map<String, Integer> getWordCount() {
		return Collections.unmodifiableMap(wordCount);
	}

	/**
	 * Retrieves the total number of words in a specified file.
	 * 
	 * @param file the file to find in the word count data structure
	 * @return the total amount of words for the file or 0 if not found
	 */
	public int getTotal(String file) {
		if (wordCount.keySet().contains(file)) {
			return wordCount.get(file);
		}
		return 0;
	}

	/**
	 * Retrieves all the words from the inverted index.
	 * 
	 * @return a string set of the words in the index
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(inverted.keySet());
	}

	/**
	 * Retrieves all files containing a word from the inverted index.
	 * 
	 * @param word the word in the inverted index
	 * @return an unmodifiable string set of all files where the key appears or an
	 *         empty set if the key is
	 */
	public Set<String> getFiles(String word) {
		if (inverted.containsKey(word)) {
			return Collections.unmodifiableSet(inverted.get(word).keySet());
		}

		return Collections.emptySet();

	}

	/**
	 * Retrieves positions of a word in a specific file from the inverted index.
	 * 
	 * @param word the word to use
	 * @param file the file to look in
	 * @return an unmodifiable set of integers containing the positions
	 */
	public Set<Integer> getPositions(String word, String file) {
		if (inverted.containsKey(word) && inverted.get(word).containsKey(file)) {
			return Collections.unmodifiableSet(inverted.get(word).get(file));
		}

		return Collections.emptySet();
	}

	/**
	 * Checks if the inverted index contains a specific word.
	 * 
	 * @param word the word to search for
	 * @return true if the index contains the word and false if it does not contain
	 *         the word.
	 */
	public boolean hasWord(String word) {
		return inverted.containsKey(word);
	}

	/**
	 * Checks if a specific word in the index contains a file.
	 * 
	 * @param word the word in the index
	 * @param file the file to check for
	 * @return true if the file is present and false if it is not
	 */
	public boolean hasFile(String word, String file) {
		return (inverted.containsKey(word) && inverted.get(word).containsKey(file));

	}

	/**
	 * Checks if a word in a file is at a specific position.
	 * 
	 * @param word the word in the index
	 * @param file the file in the index
	 * @param pos  the position of the word in the file
	 * @return true if the word in a file is in the position and false if it is not
	 */
	public boolean hasPosition(String word, String file, int pos) {
		return (inverted.containsKey(word) && inverted.get(word).containsKey(file)
				&& inverted.get(word).get(file).contains(pos));

	}

	/**
	 * Retrieves the size of the index.
	 * 
	 * @return the size of the inverted index
	 */
	public int size() {
		return inverted.size();
	}

	/**
	 * Retrieves the size of the nested map of the word.
	 * 
	 * @param word the key to the inverted index
	 * @return the size of the nested map of the word
	 */
	public int size(String word) {
		if (inverted.containsKey(word)) {
			return inverted.get(word).size();
		}

		return 0;

	}

	/**
	 * Retrieves the size of the positions in the nested map.
	 * 
	 * @param word the key to the inverted index
	 * @param file the key to the nested map
	 * @return the size of the nested map of a word in a specific file
	 */
	public int size(String word, String file) {
		if (inverted.containsKey(word) && inverted.get(word).containsKey(file)) {
			return inverted.get(word).get(file).size();
		}

		return 0;

	}

	/**
	 * Adds a word, location and position to the inverted index.
	 * 
	 * @param word     the word to add
	 * @param location the file path
	 * @param position the position of the word in the file
	 */
	public void add(String word, String location, int position) {
		inverted.putIfAbsent(word, new TreeMap<>());
		inverted.get(word).putIfAbsent(location, new TreeSet<Integer>());

		if (inverted.get(word).get(location).add(position) == true) {
			int num = wordCount.getOrDefault(location, 0) + 1;
			wordCount.put(location, num);
		}
	}

	/**
	 * Adds all the words and positions of a file to the inverted index.
	 * 
	 * @param location the file path
	 * @param words    the words in the file
	 * 
	 */
	public void addAll(String location, List<String> words) {
		int counter = 1;
		for (String word : words) {
			add(word, location, counter);
			counter++;
		}
	}

	/**
	 * Adds all the elements in one inverted index to another.
	 * 
	 * @param index the new inverted index
	 */
	public void addAll(InvertedIndex index) {
		for (String word : index.inverted.keySet()) {
			if (!inverted.containsKey(word)) {
				this.inverted.put(word, index.inverted.get(word));
			} else {
				for (String location : index.inverted.get(word).keySet()) {
					if (!this.inverted.get(word).containsKey(location)) {
						this.inverted.get(word).put(location, index.inverted.get(word).get(location));
					} else {
						this.inverted.get(word).get(location).addAll(index.inverted.get(word).get(location));
					}
				}
			}
		}

		for (String location : index.wordCount.keySet()) {
			if (!wordCount.containsKey(location)) {
				wordCount.put(location, index.wordCount.get(location));
			} else {
				int totalWords = wordCount.get(location) + index.wordCount.get(location);
				wordCount.put(location, totalWords);
			}
		}

	}

	/**
	 * Overridden toString method
	 * 
	 */
	@Override
	public String toString() {
		return ("Inverted Index: " + inverted);
	}

	/**
	 * Prints the inverted index in pretty JSON format.
	 * 
	 * @param path the file path
	 * @throws IOException if an IO error occurs
	 */
	public void printJson(Path path) throws IOException {
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			SimpleJsonWriter.asNestedObject(this.inverted, writer, 1);
		}
	}

	/**
	 * Performs an exact search on the inverted index using the set queries.
	 * 
	 * @param queries the queries to use
	 * @return a list of search results
	 */
	public ArrayList<Result> exactSearch(Set<String> queries) {
		ArrayList<Result> results = new ArrayList<Result>();
		Map<String, Result> lookup = new HashMap<String, Result>();

		for (String word : queries) {
			if (inverted.containsKey(word)) {
				build(queries, results, lookup, word);
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Performs a partial search on the inverted index using the set queries.
	 * 
	 * @param queries the queries to use
	 * @return a list of search results
	 */
	public ArrayList<Result> partialSearch(Set<String> queries) {
		ArrayList<Result> results = new ArrayList<Result>();
		Map<String, Result> lookup = new HashMap<String, Result>();

		for (String query : queries) {
			for (String oneWord : inverted.tailMap(query).keySet()) {
				if (oneWord.startsWith(query) || oneWord.equalsIgnoreCase(query)) {
					build(queries, results, lookup, oneWord);
				} else {
					break;
				}
			}

		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Builds the list of search results
	 * 
	 * @param queries the queries being search for.
	 * @param results the search results
	 * @param lookup  the lookup map
	 * @param word    the singular word we're inserting
	 */
	private void build(Set<String> queries, ArrayList<Result> results, Map<String, Result> lookup, String word) {
		for (String location : inverted.get(word).keySet()) {
			if (!lookup.containsKey(location)) {
				Result newSearch = new Result(location);
				lookup.put(location, newSearch);
				results.add(newSearch);
			}
			lookup.get(location).updateTotal(word);
		}

	}

	/**
	 * Singular search query result including the file location, the total times a
	 * query occurs in the file location, and the score (the total times divided by
	 * the total word count of the file
	 * 
	 * @author genesiskaleikau
	 */
	public class Result implements Comparable<Result> {
		/**
		 * The file path.
		 */
		private final String location;
		/**
		 * The total times a search query is in the file location.
		 */
		private int total;

		/**
		 * The score of the specified word in a file.s
		 */
		private double score;

		/**
		 * Result constructor for an object of Result
		 * 
		 * @param location the file location of the result
		 */
		public Result(String location) {
			this.location = location;
			this.total = 0;
			this.score = 0;
		}

		/**
		 * Constructor for a Result using location, total and score.
		 * 
		 * @param location the file path.
		 * @param total    the total times the search query appears.
		 * @param score    The total times the search query occurs in this file divided
		 *                 by the total word count of the file.
		 */
		public Result(String location, int total, int score) {
			this.location = location;
			this.total = total;
			this.score = score;
		}

		/**
		 * Gets the location of this search result.
		 * 
		 * @return the file location
		 */
		public String getLocation() {
			return new String(this.location);
		}

		/**
		 * Gets the total of this search result.
		 * 
		 * @return the total
		 */
		public int getTotal() {
			return this.total;
		}

		/**
		 * Gets the score of this search result.
		 * 
		 * @return the score
		 */
		public String getScore() {
			Double score = (double) total;
			score = score / wordCount.get(location);
			return String.format("%.8f", score);
		}

		/**
		 * Sets the score
		 * 
		 * @param score the new score
		 */
		public void setScore(double score) {
			this.score = score;
		}

		/**
		 * Updates the total words of a specified file.
		 * 
		 * @param word the word in the index
		 */
		private void updateTotal(String word) {
			this.total += inverted.get(word).get(location).size();
		}

		/**
		 * toString method
		 */
		@Override
		public String toString() {
			return ("where: " + this.location + " count: " + total + " score: " + String.format("%.8f", score));
		}

		/**
		 * Compares the Result object to another Result object by score, total or file
		 * name.
		 * 
		 * @param result the SearchResult object to use for comparison
		 * @return an integer less than 0, greater than 0 or equal to 0
		 */
		@Override
		public int compareTo(Result result) {
			int score = result.getScore().compareTo(this.getScore());
			if (score == 0) {
				score = Double.compare(result.total, this.total);
				if (score == 0) {
					score = this.location.compareTo(result.location);
				}
			}
			return score;
		}
	}
}
