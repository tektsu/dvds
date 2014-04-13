package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.*;

/**
 * @author steve
 * 
 */
public class Entry {

	private File entry;
	private long size;
	private Boolean isDirectory;

	/**
	 * @param entry
	 */
	public Entry(File entry) throws IOException {
		this.entry = entry;
		this.size = 0;
		if (!this.entry.exists())
			throw new IOException("File does not exist");
		if (this.entry.isDirectory()) {
			this.isDirectory = true;
			this.size = FileUtils.sizeOfDirectory(this.entry);
		} else {
			this.isDirectory = false;
			this.size = FileUtils.sizeOf(this.entry);
		}
	}

	public void dump() {
		System.out.println((this.isDirectory ? "Directory: " : "File: ") + this.entry.toString() + " (" + this.size + ")");
	}

	/**
	 * @param destination
	 */
	public void copy(File destination) {
		destination.mkdirs();
		try {
			FileUtils.copyDirectoryToDirectory(this.entry, destination);
		} catch (IOException e) {
			System.out.println("Problem copying [" + this.entry.toString() + "] to ["
					+ destination + ": " + e.getMessage());
		}
	}

	public String getPath() {
		return this.entry.getParent();
	}

	public String getName() {
		return this.entry.getName();
	}

	public long getSize() {
		return this.size;
	}

	public String getDirectory() {
		return this.entry.getPath();
	}

	public Boolean exists() {
		return this.entry.exists();
	}
}
