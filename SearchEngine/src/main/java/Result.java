import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Result interface.
 * 
 * @author genesiskaleikau
 *
 */
public interface Result {

	/**
	 * Builds queries given a file path and flag for exact or partial search.
	 * 
	 * @param path  the file path
	 * @param exact the boolean flag
	 * @throws IOException if an IO occurs
	 */
	public default void buildQueries(Path path, boolean exact) throws IOException {
		// read line by line
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				buildQueries(line, exact);
			}
		}
	}

	/**
	 * Builds queries given one line from the file and flag for exact or partial
	 * search.
	 * 
	 * @param line  one line in the file
	 * @param exact the boolean flag
	 */
	public void buildQueries(String line, boolean exact);

	/**
	 * Writes the results from the specified path.
	 *
	 * @param writer the writer to write
	 * @throws IOException If an IO occurs
	 */
	public void writeJson(Writer writer) throws IOException;

}
