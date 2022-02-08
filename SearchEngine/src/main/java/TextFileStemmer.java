import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 * 
 * @author genesiskaleikau
 */
public class TextFileStemmer {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(String line, Stemmer stemmer) {
		ArrayList<String> stemList = new ArrayList<String>();
		stemLine(line, stemmer, stemList);
		return stemList;
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line
	 * using the default stemmer.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #listStems(String, Stemmer)
	 */
	public static ArrayList<String> listStems(String line) {
		SnowballStemmer stem = new SnowballStemmer(DEFAULT);
		return listStems(line, stem);
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to an ArrayList.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static ArrayList<String> listStems(Path inputFile) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			var stems = new ArrayList<String>();

			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, stems);
			}

			return stems;
		}
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line using the default stemmer.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		SnowballStemmer stem = new SnowballStemmer(DEFAULT);
		return uniqueStems(line, stem);
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
		TreeSet<String> stemSet = new TreeSet<String>();
		stemLine(line, stemmer, stemSet);
		return stemSet;
	}

	/**
	 * Void method that adds stemmed words to a collection of strings.
	 * 
	 * @param line    the line of words to stem
	 * @param stemmer the stemmer to use
	 * @param stems   a collection of stems with the stemmed words
	 */
	private static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
		String[] words = TextParser.parse(line);

		for (String string : words) {
			String newWord = stemmer.stem(string).toString();
			stems.add(newWord);
		}
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			TreeSet<String> stemSet = new TreeSet<String>();

			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, stemSet);
			}

			return stemSet;

		}
	}

}
