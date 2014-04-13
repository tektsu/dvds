package com.xecko.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Dvds {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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

		// Examine the source and destination
		String prefix = cmd.getOptionValue("name");
		if (prefix == null) {
			prefix = "DVD";
		}
		int start = 1;
		if (cmd.getOptionValue("start") != null) {
			start = Integer.parseInt(cmd.getOptionValue("start"));
		}
//		Split split = null;
//		try {
//			split = new Split(cmd.getOptionValue("destination"), start, prefix);
//			split.SetSourceDirectory(cmd.getOptionValue("source"));
//		} catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//			System.exit(1);
//		}

	}

}
