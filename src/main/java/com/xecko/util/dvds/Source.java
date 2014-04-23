package com.xecko.util.dvds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Manage a source directory. The purpose of this class is to make it easy to
 * get a list of all the file and directories sorted by size.
 * 
 * @author steve
 */
public class Source {
  File source = null;

  /**
   * Create a Source object
   * 
   * @param source
   *          A path to a directory
   * @throws FileNotFoundException
   */
  public Source(String source) throws FileNotFoundException {
    this.source = new File(source);
    if (!this.source.exists()) {
      throw new FileNotFoundException("Source Directory [" + source
          + "] does not exist");
    }
    if (!this.source.isDirectory()) {
      throw new FileNotFoundException("Source Directory [" + source
          + "] is not a directory");
    }
  }

  /**
   * Compare the size of two Entry objects representing files or directories in
   * the source directory. Used to order the entries by size.
   * 
   * @author steve
   */
  class SizeComparator implements Comparator<Entry> {
    @Override
    public int compare(Entry e1, Entry e2) {
      long e1Size = e1.size();
      long e2Size = e2.size();

      if (e1Size > e2Size) {
        return -1;
      }
      else if (e1Size < e2Size) {
        return 1;
      }
      else {
        return 0;
      }
    }
  }

  /**
   * Get a list of Entry objects representing the files and directories in the
   * source directory.
   * 
   * @return An ArrayList of entries, sorted by size, largest first
   * @throws IOException
   */
  public ArrayList<Entry> getContents() throws IOException {
    String[] list = source.list();
    ArrayList<Entry> entries = new ArrayList<Entry>();
    for (String file : list) {
      if (file.equals(".DS_Store")) continue;
      entries.add(new Entry(source + "/" + file));
    }
    Collections.sort(entries, new SizeComparator());
    return entries;
  }
}
