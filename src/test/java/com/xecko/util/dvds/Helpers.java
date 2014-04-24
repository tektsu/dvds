package com.xecko.util.dvds;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.xecko.util.Dvds;

public class Helpers {

  public static void createEmptyFile(String name, long size)
      throws FileNotFoundException, IOException {
    RandomAccessFile fh = new RandomAccessFile(name, "rw");
    fh.setLength(size);
    fh.close();
  }

  public static Path setUpMinimalFileSystem() throws IOException {
    Path tmp = Files.createTempDirectory("__EntryTest__");
    String[] directories = new String[] { "destination", "source",
        "source/emptydir", "source/dir1", };
    for (String directory : directories) {
      Files.createDirectory(Paths.get(tmp + "/" + directory));
    }

    createEmptyFile(tmp + "/source/dir1/file1", 1024);
    createEmptyFile(tmp + "/source/file2", 1024 * 6);
    return tmp;
  }

  public static Path setUpFullFileSystem() throws IOException {
    Path tmp = setUpMinimalFileSystem();
    int[] sizes = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        16, 17, 18, 19, 20 };
    for (int size : sizes) {
      String directory = tmp + "/source/dir" + String.valueOf(size);
      Files.createDirectory(Paths.get(directory));
      createEmptyFile(directory + "/file", size * 1024);
    }
    return tmp;
  }

  public static Dvds _createContainer() {
    return new Dvds(101, "test_dvd");
  }

  public static Dvds _populateDvds(Path tmp, long maxSize) throws IOException {
    Dvds dvds = _createContainer();
    Source source = new Source(tmp + "/source");
    dvds.setMaxSize(maxSize);
    ArrayList<Entry> entries = source.getContents();
    for (Entry entry : entries) {
      dvds.add(entry);
    }
    return dvds;
  }
}
