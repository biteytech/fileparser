package test.cc.fileparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads lines of text from a file and parses each line into a list of tokens.
 * 
 * @see FileParserFactory
 * 
 * @author Lior Privman
 */
public interface FileParser {

	/**
	 * Returns a stream of tokenized lines.
	 * <p>
	 * Closing the {@link Stream} will close the {@link BufferedReader}.
	 * 
	 * @param in - contents of the file to parse
	 * @return a stream of tokenized lines.
	 */
	Stream<List<String>> stream(BufferedReader in);

	/**
	 * Returns a stream of tokenized lines.
	 * <p>
	 * <b>!! Must close the {@link Stream} to free resources.</b>
	 * 
	 * @param file - the file to parse
	 * @return a stream of tokenized lines.
	 */
	default Stream<List<String>> stream(Path file) throws IOException {
		return stream(Files.newBufferedReader(file));
	}

	/**
	 * Returns a stream of tokenized lines.
	 * 
	 * @param string - contents of the file to parse
	 * @return a stream of tokenized lines.
	 */
	default Stream<List<String>> stream(String string) {
		return stream(new BufferedReader(new StringReader(string)));
	}

	/**
	 * Returns a list of tokenized lines.
	 * 
	 * @param in - contents of the file to parse
	 * @return a list of tokenized lines.
	 */
	default List<List<String>> buffer(BufferedReader in) {
		try (Stream<List<String>> stream = stream(in);) {
			return stream.collect(Collectors.toList());
		}
	}

	/**
	 * Returns a list of tokenized lines.
	 * 
	 * @param file - the file to parse
	 * @return a list of tokenized lines.
	 */
	default List<List<String>> buffer(Path file) throws IOException {
		return buffer(Files.newBufferedReader(file));
	}

	/**
	 * Returns a list of tokenized lines.
	 * 
	 * @param string - contents of the file to parse
	 * @return a list of tokenized lines.
	 */
	default List<List<String>> buffer(String string) {
		return buffer(new BufferedReader(new StringReader(string)));
	}
}
