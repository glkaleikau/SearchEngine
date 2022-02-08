import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Utility class for building a query and search result data structure using
 * threads.
 * 
 * @author genesiskaleikau
 *
 */
public class BuildResultThread implements Result {
	/**
	 * A map for the list of results where the key are the queries.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> results;

	/**
	 * The inverted index.
	 */
	private final ThreadSafeIndex index;

	/** The amount of threads to use */
	private int threads;

	/**
	 * Constructor for BuildResultThread
	 * 
	 * @param index   thread safe index
	 * @param threads the amount of threads to use
	 */
	public BuildResultThread(ThreadSafeIndex index, int threads) {
		this.results = new TreeMap<String, ArrayList<InvertedIndex.Result>>();
		this.index = index;
		this.threads = threads;

	}

	/**
	 * Builds queries given an amount of threads, a file path and flag for exact or
	 * partial search.
	 * 
	 * @param path  the file path
	 * @param exact the boolean flag for exact search
	 * @throws IOException if an IO error occurs
	 */
	@Override
	public void buildQueries(Path path, boolean exact) throws IOException {

		WorkQueue workqueue = new WorkQueue(this.threads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				workqueue.execute(new Work(line, exact));
			}
		} finally {
			workqueue.join();
		}

	}

	/**
	 * Builds queries by reading line by line.
	 * 
	 * @param line  line to parse
	 * @param exact the boolean flag
	 */
	@Override
	public void buildQueries(String line, boolean exact) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		String key = String.join(" ", stems);

		synchronized (results) {
			if (results.containsKey(key)) {
				return;
			}
		}

		ArrayList<InvertedIndex.Result> local = null;
		if (exact) {
			local = index.exactSearch(stems);
		} else {
			local = index.partialSearch(stems);
		}

		synchronized (results) {
			results.put(key, local);
		}

	}

	/**
	 * Writes the results in JSON format.
	 * 
	 * @param writer writer to write
	 * @throws IOException if an IO error occurs
	 */
	@Override
	public void writeJson(Writer writer) throws IOException {
		synchronized (results) {
			SimpleJsonWriter.asSearchResult(writer, results);
			writer.close();
		}

	}

	/**
	 * Inner class for threads to do work.
	 * 
	 * @author genesiskaleikau
	 *
	 */
	private class Work implements Runnable {

		/** The line from query file */
		private final String line;

		/** The boolean flag for exact search */
		private final boolean exact;

		/**
		 * The work for the threads
		 * 
		 * @param line  the query file
		 * @param exact the boolean flag for exact search
		 */
		public Work(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			buildQueries(line, exact);

		}

	}

}
