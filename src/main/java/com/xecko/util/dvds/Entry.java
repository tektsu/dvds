package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Extend a File object to be aware of its size, whether a file or directory. If
 * a directory, the size includes all files and subdirectories it contains.
 * 
 * @author steve
 */
public class Entry extends File {

  private static final long serialVersionUID = 1L;
  private long size;
  private Boolean isDirectory;

  /**
   * Create the entry object.
   * 
   * @param entry
   *          The path to the entity
   */
  public Entry(String entry) throws IOException {
    super(entry);
    size = 0;
    if (!this.exists()) throw new IOException("File does not exist");
    if (this.isDirectory()) {
      isDirectory = true;
      size = FileUtils.sizeOfDirectory(this);
    }
    else {
      isDirectory = false;
      size = FileUtils.sizeOf(this);
    }
  }

  /**
   * Create the entry object.
   * 
   * @param entry
   *          An existing File object representing the entity
   */
  public Entry(File entry) throws IOException {
    this(entry.getPath());
  }

  /**
   * Represent the Entry object as a string
   */
  @Override
  public String toString() {
    return (isDirectory ? "Directory: " : "File: ") + super.toString() + " ("
        + size + ")";
  }

  /**
   * Copy the entity to another directory.
   * 
   * @param destination
   *          A destination directory to which to copy the entity
   */
  public void copy(File destination) throws IOException {
    destination.mkdirs();
    if (isDirectory)
      FileUtils.copyDirectoryToDirectory(this, destination);
    else
      FileUtils.copyFileToDirectory(this, destination, true);
  }

  /**
   * Get the size of the entity
   * 
   * @return The size of the entity in bytes
   */
  public long size() {
    return size;
  }
}
