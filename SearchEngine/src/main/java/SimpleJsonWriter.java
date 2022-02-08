import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 * 
 * 
 * @author genesiskaleikau
 *
 */
public class SimpleJsonWriter {

	/**
	 * Writes to a file an empty index.
	 * 
	 * @param writer to writer with
	 * @throws IOException if an IO error occurs
	 */
	public static void empty(Writer writer) throws IOException {
		writer.write("{\n}");
		writer.close();
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the level to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void asCount(Map<String, Integer> elements, Writer writer, int level) throws IOException {

		writer.write("{");

		Iterator<String> iterator = elements.keySet().iterator();

		if (iterator.hasNext()) {
			String file = iterator.next();
			if (elements.get(file) != 0) {
				writer.write("\n\t\"" + file + "\"");
				writer.write(": " + elements.get(file));
			}
		}

		while (iterator.hasNext()) {
			String file = iterator.next();
			if (elements.get(file) != 0) {
				writer.write(",\n");
				writer.write("\t\"" + file + "\"");
				writer.write(": " + elements.get(file));
			}
		}

		writer.write("\n}");
		writer.close();

	}

	/**
	 * Prints out one search result.
	 * 
	 * @param result the search result
	 * @param writer the writer to write
	 * @param level  the indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void searchResult(InvertedIndex.Result result, Writer writer, int level) throws IOException {
		indent(writer, level);
		writer.write("\"where\": \"" + result.getLocation() + "\",\n");

		indent(writer, level);
		writer.write("\"count\": " + Integer.toString(result.getTotal()) + ",\n");

		indent(writer, level);
		writer.write("\"score\": " + result.getScore());

	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		Iterator<Integer> iterator = elements.iterator();

		writer.write("[");

		if (iterator.hasNext()) {
			Integer integer = iterator.next();
			writer.write("\n");
			indent(integer, writer, level + 1);

		}

		while (iterator.hasNext()) {
			Integer integer = iterator.next();
			writer.write(",\n");
			indent(integer, writer, level + 1);

		}

		writer.write("\n");
		indent(writer, level);
		writer.write("]");

	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();

		writer.write("{");

		if (iterator.hasNext()) {
			String key = iterator.next();
			writer.write("\n");
			indent(key, writer, level + 1);
			writer.write(": " + elements.get(key).toString());

		}

		while (iterator.hasNext()) {
			String key = iterator.next();
			writer.write(",");
			writer.write("\n");
			indent(key, writer, level + 1);
			writer.write(": " + elements.get(key).toString());

		}

		writer.write("\n}");

	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();

		writer.write("{");

		if (iterator.hasNext()) {
			var key = iterator.next();
			writer.write("\n");
			indent(writer, level + 1);
			indent(key, writer, level);
			writer.write(": ");
			asArray(elements.get(key), writer, level + 1);

		}

		while (iterator.hasNext()) {
			var key = iterator.next();
			writer.write(",\n");
			indent(key, writer, level + 1);
			writer.write(": ");
			asArray(elements.get(key), writer, level + 1);

		}

		writer.write("\n}");

	}

	/**
	 * Writes the inverted index as a pretty JSON object.
	 * 
	 * @param inverted the inverted index
	 * @param writer   the writer used to write to the file
	 * @param level    the indentation level
	 * @throws IOException if an I/O occurs
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> inverted, Writer writer,
			int level) throws IOException {

		Iterator<String> iterator = inverted.keySet().iterator();

		writer.write("{");

		if (iterator.hasNext()) {
			writer.write("\n");
			var key = iterator.next();
			indent(key, writer, level);
			writer.write(": ");
			asNestedArray(inverted.get(key), writer, level + 2);

		}

		while (iterator.hasNext()) {
			writer.write(",");
			writer.write("\n");
			var key = iterator.next();
			indent(key, writer, level);
			writer.write(": ");
			asNestedArray(inverted.get(key), writer, level + 2);

		}

		writer.write("\n}");

	}

	/**
	 * 
	 * Prints search results in pretty JSON format.
	 *
	 * @param writer  the writer to write with
	 * @param results the map of elements to print
	 * @throws IOException if an IO error occurs
	 */
	public static void asSearchResult(Writer writer, TreeMap<String, ArrayList<InvertedIndex.Result>> results)
			throws IOException {

		writer.write("{\n");
		for (Map.Entry<String, ArrayList<InvertedIndex.Result>> query : results.entrySet()) {
			if (query.getKey().isEmpty()) {
				continue;
			}
			indent(writer, 1);
			writer.write("\"" + query.getKey() + "\": [\n");

			ArrayList<InvertedIndex.Result> resultList = query.getValue();

			if (resultList.size() > 0) {
				int counter = 0;
				indent(writer, 2);
				writer.write("{\n");
				searchResult(resultList.get(counter), writer, 3);
				indent(writer, 2);
				writer.write("\n}");

				for (counter = 1; counter < resultList.size(); counter++) {
					writer.write(",\n");
					indent(writer, 2);
					writer.write("{\n");

					searchResult(resultList.get(counter), writer, 3);

					indent(writer, 2);
					writer.write("\n}");
				}
			}

			if (resultList.size() != 0) {
				writer.write("\n");
			}

			indent(writer, 1);
			writer.write("]");

			if (results.lastKey().equals(query.getKey())) {
				writer.write("\n");
			} else {
				writer.write(",\n");
			}
		}

		writer.write("}");

	}

	/**
	 * @param element the element
	 * @param writer  the writer to write
	 * @param times   the times needed to indent
	 * @throws IOException in case an I/O error occurs
	 */
	public static void indent(TreeSet<String> element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		int counter = 0;
		for (String s : element) {
			counter++;
			if (counter < element.size()) {
				writer.write(s + " ");
			} else {
				writer.write(s);
			}

		}
		writer.write('"');
	}

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the integer element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes a map entry in pretty JSON format.
	 *
	 * @param entry  the nested entry to write
	 * @param writer the writer to use
	 * @param level  the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeEntry(Entry<String, Integer> entry, Writer writer, int level) throws IOException {
		writer.write('\n');
		indent(entry.getKey(), writer, level);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/*
	 * These methods are provided for you. No changes are required.
	 */

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

}
