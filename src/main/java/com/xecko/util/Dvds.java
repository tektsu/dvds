package com.xecko.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import java.util.ArrayList;

import com.xecko.util.dvds.Dvd;
import com.xecko.util.dvds.Entry;

public class Dvds {

	private ArrayList<Dvd> dvds;
	private File source, destination;
	private String prefix;
	private int nextSequence;

	public Dvds(String destination) throws FileNotFoundException {
		this(destination, 1, "DVD");
	}

	public Dvds(String destination, int startSequence)
			throws FileNotFoundException {
		this(destination, startSequence, "DVD");
	}

	public Dvds(String destination, int startSequence, String prefix)
			throws FileNotFoundException {
		dvds = new ArrayList<Dvd>();
		this.prefix = prefix;
		nextSequence = startSequence;

		// Open the destination directory
		this.destination = new File(destination);
		if (!this.destination.exists()) {
			throw new FileNotFoundException("Destination Directory ["
					+ destination + "] does not exist");
		}
		if (!this.destination.isDirectory()) {
			throw new FileNotFoundException("Destination Directory ["
					+ destination + "] is not a directory");
		}

		String[] contents = this.destination.list();
		if (contents.length > 0) {
			throw new FileNotFoundException("Destination Directory ["
					+ destination + "] is not empty");
		}
	}

	public void dump() {
		for (Dvd dvd : dvds) {
			dvd.dump();
		}
	}

	public void copy() throws IOException {
		for (Dvd dvd : dvds) {
			dvd.copy(destination);
		}
	}

	public void SetSourceDirectory(String source) throws FileNotFoundException {
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

	public File[] getSourceContents() {
		// TODO: We need the array sorted, largest files first
		return source.listFiles();
	}

	/**
	 * @param entry
	 */
	public void add(File fileOrDirectory) throws IOException {
		Entry entry = new Entry(fileOrDirectory);

		Dvd ourDvd = null;
		for (Dvd dvd : dvds) {
			if (dvd.getMaxSize() - dvd.size() > entry.size()) {
				ourDvd = dvd;
				break;
			}
		}
		if (ourDvd == null) {
			ourDvd = new Dvd(String.format("%s%04d", prefix, nextSequence++));
			dvds.add(ourDvd);
		}

		ourDvd.add(entry);
	}

	/**
	 * @return
	 */
	public int getDvdCount() {
		return dvds.size();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		// Command line options
		Options options = new Options();
		options.addOption("source", true, "source directory");
		options.addOption("destination", true, "destination directory");
		options.addOption("name", true, "Directory Chunk Basename");
		options.addOption("start", true, "Starting Counter Value");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Problem parsing command line: "
					+ e.getMessage());
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

		// Examine the source and destination
		String prefix = cmd.getOptionValue("name");
		if (prefix == null) {
			prefix = "DVD";
		}
		int start = 1;
		if (cmd.getOptionValue("start") != null) {
			start = Integer.parseInt(cmd.getOptionValue("start"));
		}

		Dvds dvds = new Dvds(cmd.getOptionValue("destination"), start, prefix);
		dvds.SetSourceDirectory(cmd.getOptionValue("source"));

		// For each Directory in the source, Find a chunk to put it in
		File[] contents = dvds.getSourceContents();
		for (File entry : contents) {
			dvds.add(entry);
		}

		// Copy the files to the new location
		dvds.copy();
		dvds.dump();
	}

}
