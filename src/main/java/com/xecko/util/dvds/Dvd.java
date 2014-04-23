package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manage a directory which will eventually be burned to a DVD. Entry objects
 * can be added, but the maximum size of the directory cannot be exceeded.
 * 
 * Actually, the directory can be any size, but the size of a DVD is the
 * default.
 * 
 * @author steve
 */
public class Dvd {
  ArrayList<Entry> entries; // The entries in this chunk
  long size; // The space taken by these directories
  long maxSize; // The maximum space which may be used
  String name; // The name of the chunk

  public static final long dvdSize = (long) (4.2 * 1024 * 1024 * 1024);

  /**
   * Create a Dvd object.
   * 
   * @param name
   *          The name of the DVD
   */
  public Dvd(String name) {
    this.name = name;
    entries = new ArrayList<Entry>();
    size = 0;
    setMaxSize(dvdSize);
  }

  /**
   * Represent the Dvd object as a string
   */
  @Override
  public String toString() {
    String result = "Name: " + name + " ( " + size + ")\n";
    for (Entry entry : entries) {
      result += entry + "\n";
    }
    return result;
  }

  /**
   * Copy the DVD entries to a destination directory.
   * 
   * @param destinationDirectory
   * @throws IOException
   */
  public void copy(File destinationDirectory) throws IOException {
    File destination = new File(destinationDirectory.getAbsolutePath()
        + File.separator + name);
    for (Entry entry : entries) {
      entry.copy(destination);
    }
  }

  /**
   * Set the maximum size of the DVD.
   * 
   * @param size
   *          The maximum size in bytes
   */
  public void setMaxSize(long size) {
    maxSize = size;
  }

  /**
   * Get the maximum size of the DVD.
   * 
   * @return The maximum size in bytes
   */
  public long getMaxSize() {
    return maxSize;
  }

  /**
   * Get the current size of the DVD.
   * 
   * @return The current size in bytes
   */
  public long size() {
    return size;
  }

  /**
   * Get the number of entries in the DVD.
   * 
   * @return The number of entries
   */
  public int getEntryCount() {
    return entries.size();
  }

  /**
   * Add an entry to the DVD.
   * 
   * @param entry
   *          An Entry object for the entity to add
   * @throws IOException
   */
  public void add(Entry entry) throws IOException {
    if (size + entry.size() > maxSize) {
      throw new IOException("No room");
    }
    entries.add(entry);
    size += entry.size();
  }
}
