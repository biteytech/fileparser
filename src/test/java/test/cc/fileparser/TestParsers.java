package test.cc.fileparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.EOFException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestParsers {

	@Test
	public void delimitedFile() throws Exception {

		var expected = List.of(List.of("This", "is", "a", "test"), List.of("red", "green", "blue"));

		Path file = Paths.get(TestParsers.class.getResource("test.tab").toURI());
		var actual = FileParserFactory.delimited('\t').buffer(file);

		assertEquals(expected, actual);
	}

	@Test
	public void delimitedSuccessCornerCases() throws Exception {

		FileParser parser = FileParserFactory.delimited(',');

		assertEquals(List.of(), parser.buffer(""));
		assertEquals(List.of(List.of()), parser.buffer(","));
		assertEquals(List.of(List.of("")), parser.buffer("\n"));
		assertEquals(List.of(List.of(), List.of()), parser.buffer(",\r\n,"));
		assertEquals(List.of(List.of("x")), parser.buffer("x"));
		assertEquals(List.of(List.of("你好")), parser.buffer("你好"));
		assertEquals(List.of(List.of("x"), List.of("x")), parser.buffer("x\nx"));
		assertEquals(List.of(List.of("", "", "x")), parser.buffer(",,x"));
		assertEquals(List.of(List.of("x")), parser.buffer("x,,"));
	}

	@Test
	public void fixedWidthFile() throws Exception {

		var expected = List.of(List.of("This", "is", "a", "test"), List.of("red", "green", "blue", "x"));

		Path file = Paths.get(TestParsers.class.getResource("test.fixed").toURI());
		var actual = FileParserFactory.fixedWidth('!', 6, 5, 6, 4).buffer(file);

		assertEquals(expected, actual);
	}

	@Test
	public void fixedWidthSuccessCornerCases() throws Exception {
		{
			var expected = List.of();
			var actual = FileParserFactory.fixedWidth('.', 5).buffer("");
			assertEquals(expected, actual);
		}
		{
			var expected = List.of(List.of(""));
			var actual = FileParserFactory.fixedWidth('.', 1).buffer(".");
			assertEquals(expected, actual);
		}
		{
			var expected = List.of(List.of(""), List.of(""));
			var actual = FileParserFactory.fixedWidth('.', 1).buffer("..");
			assertEquals(expected, actual);
		}
		{
			var expected = List.of(List.of("", ""));
			var actual = FileParserFactory.fixedWidth('.', 1, 1).buffer("..");
			assertEquals(expected, actual);
		}
		{
			var expected = List.of(List.of("x"));
			var actual = FileParserFactory.fixedWidth('.', 1).buffer("x");
			assertEquals(expected, actual);
		}
		{
			var expected = List.of(List.of("hi: 你好"), List.of("...你好"));
			var actual = FileParserFactory.fixedWidth('.', 6).buffer("hi: 你好...你好.");
			assertEquals(expected, actual);
		}
	}

	@Test
	public void fixedWidthVeryLongField() throws Exception {

		char[] field = new char[10000];
		Arrays.fill(field, ' ');

		var expected = List.of(List.of(""));

		var actual = FileParserFactory.fixedWidth(' ', field.length).buffer(new String(field));
		assertEquals(expected, actual);
	}

	@Test
	public void fixedWidthUnexpectedEOF() throws Exception {
		RuntimeException ex = assertThrows(RuntimeException.class, () -> {
			FileParserFactory.fixedWidth('.', 3).buffer("x.");
		});

		assertEquals(ex.getCause().getClass(), EOFException.class);
	}
}
