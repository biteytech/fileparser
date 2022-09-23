package test.cc.fileparser;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "java -jar fileparser.jar", description = "Parses a delimited or fixed-width file and prints it back out in Java's List::toString format.")
class Main implements Callable<Integer> {

	@Parameters(index = "0", description = "The file to parse.")
	private File file;

	@ArgGroup(exclusive = false)
	private FixedWidthOptions fixedWidthOptions;

	private static class FixedWidthOptions {
		@Option(names = "--pad", required = true, description = "Padding character for fixed-width files")
		char pad;

		@Option(names = "--widths", required = true, description = "Comma delimited list of column widths, e.g. `6,3,10'. Widths must be strictly positive.")
		String widths;
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {

		final FileParser parser;

		if (fixedWidthOptions != null) { // fixed-width parsing

			// process --widths
			int[] widths;
			try {
				widths = Pattern.compile(",").splitAsStream(fixedWidthOptions.widths).mapToInt(Integer::valueOf)
						.toArray();
			} catch (Exception e) {
				throw new IllegalArgumentException("failed to parse --widths", e);
			}

			parser = FileParserFactory.fixedWidth(fixedWidthOptions.pad, widths);
		} else { // delimited parsing

			// determine delimiter by file suffix
			String[] suffixTokens = file.getName().split("\\.");
			String suffixText = suffixTokens[suffixTokens.length - 1];
			FileSuffix suffix;
			try {
				suffix = FileSuffix.valueOf(suffixText.toUpperCase());
			} catch (Exception e) {
				throw new UnsupportedOperationException("unsupported file type: " + suffixText, e);
			}

			parser = FileParserFactory.delimited(suffix.getDelim());
		}

		// tokenize and print
		try (Stream<List<String>> stream = parser.stream(file.toPath())) {
			stream.forEach(System.out::println);
		}

		return 0;
	}
}
