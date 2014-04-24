package com.xecko.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
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
  private String prefix;
  private int nextSequence;
  private long maxSize;

  /**
   * Create a DVDs container, which will manage a collection of Dvd objects.
   * Each Dvd object will be given a name formed from the prefix and starting
   * sequence passed to this constructor. The sequence number will be formated
   * as a 4-digit' number with leading zeros.
   * 
   * @param startSequence
   *          An integer to start with in numbering the DVD chunks
   * @param prefix
   *          A prefix to use in constructing the name of each DVD chuck
   */
  public Dvds(int startSequence, String prefix) {
    dvds = new ArrayList<Dvd>();
    this.prefix = prefix;
    nextSequence = startSequence;
    maxSize = Dvd.dvdSize;
  }

  public Dvds(int startSequence) {
    this(startSequence, "DVD");
  }

  public Dvds() {
    this(1, "DVD");
  }

  /**
   * Represent the Dvds object as a string
   */
  @Override
  public String toString() {
    String result = "";
    for (Dvd dvd : dvds) {
      result = dvd + "\n";
    }
    return result;
  }

  /**
   * Copy all of the source files and directories to their assigned DVD chunks
   * in the destination directory.
   * 
   * @throws IOException
   */
  public void copy(String destination) throws IOException {
    // Open the destination directory
    File destDir = new File(destination);
    if (!destDir.exists()) {
      throw new FileNotFoundException("Destination Directory [" + destination
          + "] does not exist");
    }
    if (!destDir.isDirectory()) {
      throw new FileNotFoundException("Destination Directory [" + destination
          + "] is not a directory");
    }

    String[] contents = destDir.list();
    if (contents.length > 0) {
      throw new FileNotFoundException("Destination Directory [" + destination
          + "] is not empty");
    }
    for (Dvd dvd : dvds) {
      dvd.copy(destDir);
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
   * Get the number of DVDs in the collection so far.
   * 
   * @return The number of DVDs
   */
  public int getDvdCount() {
    return dvds.size();
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
      if (entry.size() > maxSize)
        throw new IOException("Entry too large for this collection");
      ourDvd = new Dvd(String.format("%s%04d", prefix, nextSequence++));
      ourDvd.setMaxSize(maxSize);
      dvds.add(ourDvd);
    }

    ourDvd.add(entry);
  }

  public static void main(String[] args) {

    Options options = new Options();
    options.addOption("source", true, "source directory");
    options.addOption("destination", true, "destination directory");
    options.addOption("prefix", true, "Directory Basename");
    options.addOption("sequence", true, "Starting Counter Value");
    options.addOption("n", "no-action", false,
        "Do not copy files to destination");
    options.addOption("h", "help", false, "Print this message");

    CommandLineParser parser = new PosixParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.out.println("Problem parsing command line: " + e.getMessage());
      System.exit(1);
    }
    if (cmd.hasOption("help")) {
      new HelpFormatter().printHelp("dvds", options);
      System.exit(0);
    }

    if (cmd.getOptionValue("source") == null) {
      System.out.println("No source specified");
      System.exit(1);
    }
    if (cmd.getOptionValue("destination") == null) {
      System.out.println("No destination specified");
      System.exit(1);
    }

    String prefix = cmd.getOptionValue("prefix");
    if (prefix == null) {
      prefix = "DVD";
    }
    int sequence = 1;
    if (cmd.getOptionValue("sequence") != null) {
      sequence = Integer.parseInt(cmd.getOptionValue("sequence"));
    }

    Dvds dvds = null;
    Source source = null;
    try {
      dvds = new Dvds(sequence, prefix);
      source = new Source(cmd.getOptionValue("source"));
    }
    catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }

    try {
      ArrayList<Entry> entries = source.getContents();
      for (Entry entry : entries) {
        dvds.add(entry);
      }
    }
    catch (IOException e) {
      System.out.println("Problem adding files to collection: "
          + e.getMessage());
      System.exit(1);
    }

    if (!cmd.hasOption("no-action")) {
      try {
        dvds.copy(cmd.getOptionValue("destination"));
      }
      catch (IOException e) {
        System.out.println("Problem copying files to destination: "
            + e.getMessage());
        System.exit(1);
      }
    }
    System.out.println(dvds);
  }
}
