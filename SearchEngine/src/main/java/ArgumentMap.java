import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses and stores command-line arguments into simple key = value pairs.
 * 
 * @author genesiskaleikau
 *
 */
public class ArgumentMap {

	/**
	 * Stores command-line arguments in key = value pairs.
	 */
	private final Map<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() {
		this.map = new HashMap<String, String>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentMap(String[] args) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may not
	 * have associated values. If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (isFlag(args[i])) {
				if (i + 1 < args.length && isValue(args[i + 1])) {
					this.map.put(args[i], args[i + 1]);
				} else {
					this.map.put(args[i], null);
				}
			}
		}

	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#charAt(int)
	 * @see Character#isDigit(char)
	 */
	public static boolean isFlag(String arg) {
		if (arg == null || !arg.startsWith("-") || arg.length() < 2) {
			return false;
		} else {
			if (Character.isDigit(arg.charAt(1))) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 */
	public boolean isValue(String arg) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return this.map.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return this.map.containsKey(flag);

	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return this.map.get(flag) != null;

	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {
		return this.map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or the default value if there is no mapping.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default value
	 *         if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {

		if (hasValue(flag) && this.map.get(flag) != null) {
			return this.map.get(flag).toString();
		}

		else {
			return defaultValue;
		}

	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {

		if (hasValue(flag)) {
			return (Path.of(this.map.get(flag).toString()));

		}
		return null;

	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public Path getPath(String flag, Path defaultValue) {

		if (hasValue(flag)) {
			return (Path.of(this.map.get(flag).toString()));
		}

		return defaultValue;

	}

	/**
	 * Returns the value the specified flag is mapped as an int value, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to an int or if no value exists).
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a int, or the default value
	 *         if there is no valid mapping
	 */
	public int getInteger(String flag, int defaultValue) {
		try {
			int var = Integer.parseInt(getString(flag));
			if (var <= 0) {
				return defaultValue;
			}
			return var;
		} catch (Exception e) {
			return defaultValue;
		}

	}

	/**
	 * Returns the value of the url from the map.
	 * 
	 * @param flag the flag for the url
	 * @return the url of the flag
	 * @throws MalformedURLException an exception for if the url is malformed
	 */
	public URL getUrl(String flag) throws MalformedURLException {
		return new URL(getString(flag));
	}

	@Override
	public String toString() {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return this.map.toString();
	}
}