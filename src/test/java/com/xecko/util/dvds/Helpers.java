package com.xecko.util.dvds;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class Helpers {
	
	public static Path setUpFileSystem() throws IOException {
		Path tmp = Files.createTempDirectory("__EntryTest__");
		Files.createDirectory(Paths.get(tmp + "/destination"));
		Files.createDirectory(Paths.get(tmp + "/source"));
		Files.createDirectory(Paths.get(tmp + "/source/emptydir"));
		Files.createDirectory(Paths.get(tmp + "/source/dir1"));
		RandomAccessFile fh = new RandomAccessFile(tmp + "/source/dir1/file1", "rw");
		fh.setLength(1024);
		fh.close();
		fh = new RandomAccessFile(tmp + "/source/file2", "rw");
		fh.setLength(1024 * 6);
		fh.close();
		return tmp;
	}

}
