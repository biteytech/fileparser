package test.cc.fileparser;

enum FileSuffix {

	TAB('\t'), CSV(',');

	private final char delim;

	private FileSuffix(char delim) {
		this.delim = delim;
	}

	char getDelim() {
		return delim;
	}
}
