package test.cc.fileparser;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class FixedWidthParser implements FileParser {

	// used to pad tokens to the necessary width
	private final char pad;

	// the token widths
	private final int[] widths;

	FixedWidthParser(char pad, int[] widths) {

		if (widths == null || widths.length == 0)
			throw new IllegalArgumentException("widths cannot be null or empty");

		for (int width : widths)
			if (width < 1)
				throw new IllegalArgumentException("widths must be strictly positive");

		this.pad = pad;
		this.widths = widths.clone();
	}

	@Override
	public Stream<List<String>> stream(BufferedReader in) {

		FixedWidthIterator iter = new FixedWidthIterator(in);

		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED | Spliterator.NONNULL), false)
				.onClose(iter::close);
	}

	private class FixedWidthIterator implements Iterator<List<String>> {

		final BufferedReader in;

		// sized to hold exactly one line
		final char[] lineBuffer;

		// The next tokenized line to be returned, or null if reached EOF or Stream is
		// closed.
		List<String> next;

		FixedWidthIterator(BufferedReader in) {

			this.in = in;

			lineBuffer = new char[Arrays.stream(widths).sum()];

			next = tokenizeLine();
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public List<String> next() {

			List<String> next = this.next;

			if (next == null)
				throw new NoSuchElementException();

			this.next = tokenizeLine();

			return next;
		}

		List<String> tokenizeLine() {
			List<String> tokens = new ArrayList<>(widths.length);

			// read line into buffer
			try {
				int read = 0;
				while (read < lineBuffer.length) {

					int r = in.read(lineBuffer, read, lineBuffer.length - read);

					if (r == -1) {
						if (read == 0) // done - no more lines
							return null;
						else
							throw new EOFException("Unexpected end of fixed-width file.");
					}

					read += r;
				}
			} catch (IOException e) {
				close();
				throw new RuntimeException(e);
			}

			// tokenize line
			int offset = 0;
			for (int width : widths) {

				int count = width;

				while (count > 0 && lineBuffer[offset + count - 1] == pad)
					count--;

				String token = new String(lineBuffer, offset, count);
				tokens.add(token);

				offset += width;
			}

			return tokens;
		}

		void close() {
			next = null;

			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("%s[pad=%s, widths=%s]", getClass().getSimpleName(), pad, Arrays.toString(widths));
	}
}
