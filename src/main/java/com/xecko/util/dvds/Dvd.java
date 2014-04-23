package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author steve
 * 
 */
public class Dvd {
  ArrayList<Entry> entries; // The entries in this chunk
  long size; // The space taken by these directories
  long maxSize; // The maximum space which may be used
  String name; // The name of the chunk

  public static final long dvdSize = (long) (4.2 * 1024 * 1024 * 1024);

  public Dvd(String name) {
    this.name = name;
    entries = new ArrayList<Entry>();
    size = 0;
    setMaxSize(dvdSize);
  }

  public void dump() {
    System.out.println("Name: " + name + " ( " + size + ")");
    for (Entry entry : entries) {
      entry.dump();
    }
    System.out.println("");
  }

  public void copy(File destinationDirectory) throws IOException {
    File destination = new File(destinationDirectory.getAbsolutePath()
        + File.separator + name);
    for (Entry entry : entries) {
      entry.copy(destination);
    }
  }

  public void setMaxSize(long size) {
    maxSize = size;
  }

  public long getMaxSize() {
    return maxSize;
  }

  public long size() {
    return size;
  }

  public void add(Entry entry) throws IOException {
    if (size + entry.size() > maxSize) {
      throw new IOException("No room");
    }
    entries.add(entry);
    size += entry.size();
  }

  public int getEntryCount() {
    return entries.size();
  }
}
