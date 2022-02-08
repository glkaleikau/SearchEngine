import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * functions and streams.
 *
 * @author Genesis Kaleikau
 * 
 */
public class TextFileFinder {

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive). Useful for
	 * {@link Files#walk(Path, FileVisitOption...)}.
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 * @see Path#getFileName()
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static final Predicate<Path> IS_TEXT = p -> {
		if (Files.isRegularFile(p, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
			String lower = p.toString().toLowerCase();
			return lower.endsWith(".txt") || lower.endsWith(".text");
		}
		return false;
	};

	/**
	 * Returns a stream of matching files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @param keep  function that determines whether to keep a file
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #IS_TEXT
	 * @see FileVisitOption#FOLLOW_LINKS
	 * @see Files#walk(Path, FileVisitOption...)
	 */
	public static Stream<Path> find(Path start, Predicate<Path> keep) throws IOException {
		return Files.walk(start, FileVisitOption.FOLLOW_LINKS).filter(keep);

	};

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path, Predicate)
	 * @see #IS_TEXT
	 */
	public static Stream<Path> find(Path start) throws IOException {
		return find(start, IS_TEXT);
	}

	/**
	 * Returns a list of text files using streams.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see #find(Path)
	 * @see Collectors#toList()
	 */
	public static List<Path> list(Path start) throws IOException {
		List<Path> path = new ArrayList<Path>();
		find(start).forEach(path::add);
		return path;
	}

}
