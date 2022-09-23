package test.cc.fileparser;

/**
 * Factory for creating {@link FileParser} instances.
 * 
 * @author Lior Privman
 */
public enum FileParserFactory {
	; // static methods only (empty enum prevents instantiation)

	/**
	 * Returns a file parser which tokenizes lines using the specified delimiter.
	 * 
	 * @param delim - the delimiter
	 * 
	 * @return a file parser which tokenizes lines using the specified delimiter.
	 * 
	 * @throws IllegalArgumentException if {@code delim} is '\r' or '\n'
	 */
	public static FileParser delimited(char delim) {
		return new DelimitedParser(delim);
	}

	/**
	 * Returns a file parser which tokenizes lines using fixed widths.
	 * 
	 * @param pad    - the padding character
	 * @param widths - a non-empty array of column widths, all strictly positive
	 * 
	 * @return a file parser which tokenizes lines using fixed widths.
	 * 
	 * @throws IllegalArgumentException if {@code widths} is null or empty or any
	 *                                  width is not strictly positive
	 */
	public static FileParser fixedWidth(char pad, int... widths) {
		return new FixedWidthParser(pad, widths);
	}
}
