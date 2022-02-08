import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for building an index by stemming files and adding them to an
 * inverted index.
 * 
 * @author genesiskaleikau
 *
 */
public class InvertedIndexBuilder {

	/**
	 * Builds the inverted index by checking if the path is a file or directory.
	 * 
	 * @param path  the file path
	 * @param index the inverted index to build
	 * @throws IOException if an IO occurs
	 */
	public static void build(Path path, InvertedIndex index) throws IOException {
		if (Files.isRegularFile(path)) {
			addFile(path, index);
		} else if (Files.isDirectory(path)) {
			List<Path> paths = TextFileFinder.list(path);
			for (Path currentPath : paths) {
				addFile(currentPath, index);
			}
		}
	}

	/**
	 * Parses the given file path for the cleaned stems and adds them to the
	 * inverted index.
	 * 
	 * @param path  the file path
	 * @param index the inverted index to add to
	 * @throws IOException if an IO error occurs
	 */
	public static void addFile(Path path, InvertedIndex index) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			Stemmer stemmer = new SnowballStemmer(TextFileStemmer.DEFAULT);
			String line;
			int counter = 1;
			String filePath = path.toString();
			while ((line = reader.readLine()) != null) {

				for (String word : TextParser.parse(line)) {
					index.add(stemmer.stem(word).toString(), filePath, counter);
					counter++;
				}
			}
		}
	}

}
