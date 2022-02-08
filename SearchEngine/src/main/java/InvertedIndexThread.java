import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Utility class for building the inverted index using threads.
 * 
 * @author genesiskaleikau
 *
 */
public class InvertedIndexThread {

	/**
	 * Builds the inverted index by checking if the path is a file or directory.
	 * 
	 * @param threads the amount of threads to use
	 * @param path    the file path
	 * @param index   the inverted index to build
	 * @return the inverted index
	 * @throws IOException if an IO occurs
	 */
	public static ThreadSafeIndex build(int threads, Path path, ThreadSafeIndex index) throws IOException {
		if (threads < 1) {
			throw new InvalidParameterException("");
		}
		WorkQueue workqueue = new WorkQueue(threads);

		if (Files.isRegularFile(path)) {
			workqueue.execute(new Work(path, index));

		} else if (Files.isDirectory(path)) {
			List<Path> paths = TextFileFinder.list(path);
			for (Path currentPath : paths) {
				workqueue.execute(new Work(currentPath, index));

			}
		}
		workqueue.join();
		return index;
	}

	/**
	 * Inner class for threads to do work.
	 * 
	 * @author genesiskaleikau
	 *
	 */
	private static class Work implements Runnable {
		/**
		 * The inverted index.
		 */
		private final ThreadSafeIndex index;
		/**
		 * The file path used to create the inverted index.
		 */
		private final Path path;

		/**
		 * @param path  the file path or directory
		 * @param index the inverted index to build
		 */
		public Work(Path path, ThreadSafeIndex index) {
			this.path = path;
			this.index = index;

		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.addFile(path, local);
				index.addAll(local);

			} catch (IOException e) {
				System.out.println("Unable to build inverted index using threads.");
			}

		}

	}

}
