import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {
	/**
	 * Test server code on port 5000
	 */
	private static String[] test2 = { "-url",
			"https://www.cs.usfca.edu/~cs212/docs/jdk-14.0.2_doc-all/api/allclasses-index.html", "-max", "30",
			"-threads", "10", "-server", "5000" };

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * 
	 */
	public static void main(String[] args) {
		InvertedIndex index = null;
		System.out.println(test2);
		ArgumentMap map = new ArgumentMap(args);
		Result result = null;
		ThreadSafeIndex threadSafeIndex = null;
		int threads = 0;
		SearchEngineServer server = null;

		if (map.hasFlag("-threads") || map.hasFlag("-url")) {
			try {
				threadSafeIndex = new ThreadSafeIndex();
				index = threadSafeIndex;
				threads = map.getInteger("-threads", 5);
				result = new BuildResultThread(threadSafeIndex, threads);

			} catch (NumberFormatException | InvalidParameterException e) {
				System.out.println("Incorrect format for threads value.");
			}
		} else {
			index = new InvertedIndex();
			result = new BuildResult(index);
		}
		if (map.hasFlag("-url")) {
			URL seed;
			try {
				seed = map.getUrl("-url");
				System.out.println("Seed: " + seed.toString());
				WebCrawler web = new WebCrawler(threadSafeIndex, map.getInteger("-max", 1), threads, seed.toString());
				web.crawl();
			} catch (MalformedURLException | URISyntaxException e) {
				e.printStackTrace();
			}

		}

		if (map.hasFlag("-path") || !map.hasValue("-path")) {
			Path inputPath = map.getPath("-path");

			try {
				if (threadSafeIndex != null) {
					InvertedIndexThread.build(threads, inputPath, threadSafeIndex);

				} else {
					InvertedIndexBuilder.build(inputPath, index);

				}

			} catch (NumberFormatException | IOException | InvalidParameterException | NullPointerException e) {
				System.out.println("Incorrect format for threads value.");
			}

		}
		int PORT = 0;
		if (map.hasFlag("-server")) {
			PORT = map.getInteger("-server", 8080);
		}
		server = new SearchEngineServer(threadSafeIndex, PORT);

		// server = new SearchEngineServer(threadSafeIndex);

		try {
			server.run();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (map.hasFlag("-index")) {
			Path outputPath = map.getPath("-index", Path.of("index.json"));
			try {
				index.printJson(outputPath);

			} catch (IOException e) {
				System.out.println("Unable write index to file " + outputPath);
			}
		}

		if (map.hasFlag("-counts")) {
			Path counts = map.getPath("-counts");
			try (Writer writer = Files.newBufferedWriter(counts, StandardCharsets.UTF_8);) {
				SimpleJsonWriter.asCount(index.getWordCount(), writer, 1);

			} catch (IOException e) {
				System.out.println("Unable write index to file " + counts);
			}
		}

		if (map.hasFlag("-queries")) {
			try {
				Path query = map.getPath("-queries");
				result.buildQueries(query, map.hasFlag("-exact"));

			} catch (IOException | NullPointerException e) {
				System.out.println("Unable to read queries from path: " + map.getPath("-queries"));
			}
		}

		if (map.hasFlag("-results")) {
			Path results = map.getPath("-results", Path.of("results.json"));

			try (Writer writer = Files.newBufferedWriter(results, StandardCharsets.UTF_8);) {
				result.writeJson(writer);

			} catch (IOException | NullPointerException e) {
				System.out.println("Unable to write results to path: " + results);
			}

		}

	}

}