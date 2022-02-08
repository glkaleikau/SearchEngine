import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Thread-safe version of inverted index.
 * 
 * @author genesiskaleikau
 *
 */
public class ThreadSafeIndex extends InvertedIndex {

	/**
	 * The lock used to protect concurrent access to the underlying set.
	 */
	private final ReadWriteLock lock;

	/**
	 * Default constructor for thread safe index.
	 */
	public ThreadSafeIndex() {
		super();
		this.lock = new ReadWriteLock();
	}

	/**
	 * Returns the identity hashcode of the lock object. Not particularly useful.
	 *
	 * @return the identity hashcode of the lock object
	 */
	public int lockCode() {
		return System.identityHashCode(lock);
	}

	/**
	 * Safely retrieves word count data structure.
	 */

	@Override
	public Map<String, Integer> getWordCount() {
		lock.readLock().lock();
		try {
			return super.getWordCount();

		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Safely retrieves the total number of words in a specified file.
	 * 
	 * @param file the file to find in the word count data structure
	 * @return the total amount of words for the file or 0 if not found
	 */
	@Override
	public int getTotal(String file) {
		lock.readLock().lock();
		try {
			return super.getTotal(file);

		} finally {
			lock.readLock().unlock();

		}
	}

	/**
	 * Safely retrieves all the words from the inverted index.
	 * 
	 * @return a string set of the words in the index
	 */
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Safely retrieves all files containing a word from the inverted index.
	 * 
	 * @param word the word in the inverted index
	 * @return an unmodifiable string set of all files where the key appears or an
	 *         empty set if the key is
	 */
	@Override
	public Set<String> getFiles(String word) {
		lock.readLock().lock();
		try {
			return super.getFiles(word);

		} finally {
			lock.readLock().unlock();

		}

	}

	/**
	 * Safely retrieves positions of a word in a specific file from the inverted
	 * index.
	 * 
	 * @param word the word to use
	 * @param file the file to look in
	 * @return an unmodifiable set of integers containing the positions
	 */
	@Override
	public Set<Integer> getPositions(String word, String file) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Safely checks if the inverted index contains a specific word.
	 * 
	 * @param word the word to search for
	 * @return true if the index contains the word and false if it does not contain
	 *         the word.
	 */
	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Safely checks if a specific word in the index contains a file.
	 * 
	 * @param word the word in the index
	 * @param file the file to check for
	 * @return true if the file is present and false if it is not
	 */
	@Override
	public boolean hasFile(String word, String file) {
		lock.readLock().lock();
		try {
			return super.hasFile(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Safely checks if a word in a file is at a specific position.
	 * 
	 * @param word the word in the index
	 * @param file the file in the index
	 * @param pos  the position of the word in the file
	 * @return true if the word in a file is in the position and false if it is not
	 */
	@Override
	public boolean hasPosition(String word, String file, int pos) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, file, pos);

		} finally {
			lock.readLock().unlock();

		}

	}

	/**
	 * Safely retrieves the size of the index.
	 * 
	 * @return the size of the inverted index
	 */
	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();

		} finally {
			lock.readLock().unlock();

		}
	}

	/**
	 * Safely retrieves the size of the nested map of the word.
	 * 
	 * @param word the key to the inverted index
	 * @return the size of the nested map of the word
	 */
	@Override
	public int size(String word) {
		lock.readLock().lock();
		try {
			return super.size(word);

		} finally {
			lock.readLock().unlock();

		}

	}

	/**
	 * Safely retrieves the size of the positions in the nested map.
	 * 
	 * @param word the key to the inverted index
	 * @param file the key to the nested map
	 * @return the size of the nested map of a word in a specific file
	 */
	@Override
	public int size(String word, String file) {
		lock.readLock().lock();
		try {
			return super.size(word, file);

		} finally {
			lock.readLock().unlock();

		}

	}

	/**
	 * Safely adds a word, location and position to the inverted index.
	 * 
	 * @param word     the word to add
	 * @param location the file path
	 * @param position the position of the word in the file
	 */
	@Override
	public void add(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			super.add(word, location, position);

		} finally {
			lock.writeLock().unlock();

		}
	}

	/**
	 * Safely adds all the words and positions of a file to the inverted index.
	 * 
	 * @param location the file path
	 * @param words    the words in the file
	 * 
	 */
	@Override
	public void addAll(String location, List<String> words) {
		lock.writeLock().lock();
		try {
			super.addAll(location, words);

		} finally {
			lock.writeLock().unlock();

		}

	}

	/**
	 * Safely adds all the elements in one inverted index to another.
	 * 
	 * @param index the new inverted index
	 */
	@Override
	public void addAll(InvertedIndex index) {
		lock.writeLock().lock();
		try {
			super.addAll(index);

		} finally {
			lock.writeLock().unlock();

		}

	}

	/**
	 * Overridden toString method
	 * 
	 */
	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();

		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Prints the inverted index in pretty JSON format.
	 * 
	 * @param path the file path
	 * @throws IOException if an IO error occurs
	 */
	@Override
	public void printJson(Path path) throws IOException {
		lock.writeLock().lock();
		try {
			super.printJson(path);
		} finally {
			lock.writeLock().unlock();
		}

	}

	/**
	 * Safely performs an exact search on the inverted index using the set queries.
	 * 
	 * @param queries the queries to use
	 * @return a list of search results
	 */
	@Override
	public ArrayList<Result> exactSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queries);

		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Safely performs a partial search on the inverted index using the set queries.
	 * 
	 * @param queries the queries to use
	 * @return a list of search results
	 */
	@Override
	public ArrayList<Result> partialSearch(Set<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queries);

		} finally {
			lock.readLock().unlock();
		}
	}

}
