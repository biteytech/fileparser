# fileparser
File parser exercise

## Build and command line usage
Requires Java 11+

```shell
# build
mvn clean package

# print usage
java -jar target/fileparser.jar

# tab delimited file
java -jar target/fileparser.jar src/test/resources/test/cc/fileparser/test.tab

# fixed width file
java -jar target/fileparser.jar --pad=! --widths=6,5,6,4 src/test/resources/test/cc/fileparser/test.fixed
```

## API
```java
// create a tab delimited FileParser
FileParser parser = FileParserFactory.delimited('\t');

// stream the tokenized lines of a file
Path file = ...;
try (
    Stream<List<String>> stream = parser.stream(file);
) {
    stream.forEach(...);
}

// or buffer the entire file
List<List<String>> list = parser.buffer(file);
```
