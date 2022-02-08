import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Utility class for building a query and search result data structure.
 * 
 * @author genesiskaleikau
 *
 */
public class BuildResult implements Result {
	/**
	 * A map for the list of results where the key are the queries.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> results;

	/**
	 * The inverted index.
	 */
	private final InvertedIndex index;

	/**
	 * Constructor for BuildResult class
	 * 
	 * @param index the inverted index
	 */
	public BuildResult(InvertedIndex index) {
		this.index = index;
		this.results = new TreeMap<String, ArrayList<InvertedIndex.Result>>();
	}

	/**
	 * Writes the results from the specified path.
	 *
	 * @param writer the writer to write
	 * @throws IOException If an IO occurs
	 */
	@Override
	public void writeJson(Writer writer) throws IOException {
		SimpleJsonWriter.asSearchResult(writer, results);
		writer.close();
	}

	/**
	 * Builds queries given one line from the file and flag for exact or partial
	 * search.
	 * 
	 * @param line  one line in the file
	 * @param exact the boolean flag
	 */
	@Override
	public void buildQueries(String line, boolean exact) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		if (!stems.isEmpty()) {
			String key = String.join(" ", stems);
			if (!results.containsKey(key)) {
				if (exact) {
					var local = index.exactSearch(stems);
					results.put(key, local);

				} else {
					var local = index.partialSearch(stems);
					results.put(key, local);

				}
			}

		}
	}

}
