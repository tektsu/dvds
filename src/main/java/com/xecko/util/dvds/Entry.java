package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * @author steve
 * 
 */
public class Entry extends File {

	private long size;
	private Boolean isDirectory;

	/**
	 * @param entry
	 */
	public Entry(File entry) throws IOException {
		this(entry.getPath());
	}
	
	public Entry(String entry) throws IOException {
		super(entry);
		size = 0;
		if (!this.exists())
			throw new IOException("File does not exist");
		if (this.isDirectory()) {
			isDirectory = true;
			size = FileUtils.sizeOfDirectory(this);
		} else {
			isDirectory = false;
			size = FileUtils.sizeOf(this);
		}
	}

	public void dump() {
		System.out.println((isDirectory ? "Directory: " : "File: ") + this.toString() + " (" + size + ")");
	}

	/**
	 * @param destination
	 */
	public void copy(File destination) throws IOException {
		destination.mkdirs();
		if (isDirectory)
			FileUtils.copyDirectoryToDirectory(this, destination);
		else
			FileUtils.copyFileToDirectory(this, destination, true);
	}
	
	public long size() {
		return size;
	}
}
