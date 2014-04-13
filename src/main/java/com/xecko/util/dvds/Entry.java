package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

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
		size = 0;
		if (!this.entry.exists())
			throw new IOException("File does not exist");
		if (this.entry.isDirectory()) {
			isDirectory = true;
			size = FileUtils.sizeOfDirectory(this.entry);
		} else {
			isDirectory = false;
			size = FileUtils.sizeOf(this.entry);
		}
	}

	public void dump() {
		System.out.println((this.isDirectory ? "Directory: " : "File: ") + this.entry.toString() + " (" + this.size + ")");
	}

	/**
	 * @param destination
	 */
	public void copy(File destination) throws IOException {
		destination.mkdirs();
		if (isDirectory)
			FileUtils.copyDirectoryToDirectory(entry, destination);
		else
			FileUtils.copyFileToDirectory(entry, destination, true);
	}

	public String getParent() {
		return entry.getParent();
	}

	public String getName() {
		return entry.getName();
	}

	public long size() {
		return size;
	}

	public String getPath() {
		return entry.getPath();
	}
}
