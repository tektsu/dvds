package com.xecko.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.xecko.util.dvds.Dvd;
import com.xecko.util.dvds.Entry;
import com.xecko.util.dvds.Source;

/**
 * Take a source directory full of files and directories, and sort those files
 * and directories into DVD-sized chunks in a destination directory.
 * 
 * @author steve
 */
public class Dvds {

  private ArrayList<Dvd> dvds;
  private File           destination;
  private String         prefix;
  private int            nextSequence;
  private long           maxSize;

  /**
   * Create a DVDs container, which will manage a collection of Dvd objects.
   * Each Dvd object will be given a name formed from the prefix and starting
   * sequence passed to this constructor. The sequence number will be formated
   * as a 4-digit' number with leading zeros.
   * 
   * @param destination
   *          The destination directory, which must exist and be empty
   * @param startSequence
   *          An integer to start with in numbering the DVD chunks
   * @param prefix
   *          A prefix to use in constructing the name of each DVD chuck
   * @throws FileNotFoundException
   */
  public Dvds(String destination, int startSequence, String prefix)
      throws FileNotFoundException {
    dvds = new ArrayList<Dvd>();
    this.prefix = prefix;
    nextSequence = startSequence;
    maxSize = Dvd.dvdSize;

    // Open the destination directory
    this.destination = new File(destination);
    if (!this.destination.exists()) {
      throw new FileNotFoundException("Destination Directory [" + destination
          + "] does not exist");
    }
    if (!this.destination.isDirectory()) {
      throw new FileNotFoundException("Destination Directory [" + destination
          + "] is not a directory");
    }

    String[] contents = this.destination.list();
    if (contents.length > 0) {
      throw new FileNotFoundException("Destination Directory [" + destination
          + "] is not empty");
    }
  }

  public Dvds(String destination) throws FileNotFoundException {
    this(destination, 1, "DVD");
  }

  public Dvds(String destination, int startSequence)
      throws FileNotFoundException {
    this(destination, startSequence, "DVD");
  }

  /**
   * List the contents of all the Dvd objects being managed. The output is sent
   * to stdout.
   */
  public void dump() {
    for (Dvd dvd : dvds) {
      dvd.dump();
    }
  }

  /**
   * Copy all of the source files and directories to their assigned DVD chunks
   * in the destination directory.
   * 
   * @throws IOException
   */
  public void copy() throws IOException {
    for (Dvd dvd : dvds) {
      dvd.copy(destination);
    }
  }

  /**
   * Set the number of bytes each DVD chunk can hold.
   * 
   * @param size
   *          The size in bytes of each DVD
   */
  public void setMaxSize(long size) {
    maxSize = size;
  }

  /**
   * Add a file or directory to the DVD collection. The file or directory will
   * be added to the first DVD in the collection with enough room to hold it.
   * 
   * @param entry
   *          An Entry object describing the file or directory
   */
  public void add(Entry entry) throws IOException {

    Dvd ourDvd = null;
    for (Dvd dvd : dvds) {
      if (dvd.getMaxSize() - dvd.size() > entry.size()) {
        ourDvd = dvd;
        break;
      }
    }
    if (ourDvd == null) {
      ourDvd = new Dvd(String.format("%s%04d", prefix, nextSequence++));
      ourDvd.setMaxSize(maxSize);
      dvds.add(ourDvd);
    }

    ourDvd.add(entry);
  }

  /**
   * Get the number of DVDs in the collection so far.
   * 
   * @return The number of DVDs
   */
  public int getDvdCount() {
    return dvds.size();
  }

  public static void main(String[] args) throws FileNotFoundException,
      IOException {

    Options options = new Options();
    options.addOption("source", true, "source directory");
    options.addOption("destination", true, "destination directory");
    options.addOption("name", true, "Directory Chunk Basename");
    options.addOption("start", true, "Starting Counter Value");

    CommandLineParser parser = new PosixParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.out.println("Problem parsing command line: " + e.getMessage());
      System.exit(1);
    }
    if (cmd.getOptionValue("source") == null) {
      System.out.println("No source specified");
      System.exit(1);
    }
    if (cmd.getOptionValue("destination") == null) {
      System.out.println("No destination specified");
      System.exit(1);
    }

    String prefix = cmd.getOptionValue("name");
    if (prefix == null) {
      prefix = "DVD";
    }
    int start = 1;
    if (cmd.getOptionValue("start") != null) {
      start = Integer.parseInt(cmd.getOptionValue("start"));
    }

    Dvds dvds = new Dvds(cmd.getOptionValue("destination"), start, prefix);
    Source source = new Source(cmd.getOptionValue("source"));

    ArrayList<Entry> entries = source.getContents();
    for (Entry entry : entries) {
      dvds.add(entry);
    }

    dvds.copy();
    dvds.dump();
  }
}
