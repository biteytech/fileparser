package test.cc.fileparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class DelimitedParser implements FileParser {

	// splits lines into tokens
	private final Pattern splitter;

	DelimitedParser(char delim) {

		if (delim == '\r' || delim == '\n')
			throw new IllegalArgumentException("delimiter cannot be CR or LF");

		this.splitter = Pattern.compile(Pattern.quote(String.valueOf(delim)));
	}

	@Override
	public Stream<List<String>> stream(BufferedReader in) {

		return in.lines().map(splitter::split).map(Arrays::asList).onClose(() -> {
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

}
