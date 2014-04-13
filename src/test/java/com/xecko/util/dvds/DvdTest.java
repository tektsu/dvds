package com.xecko.util.dvds;

import com.xecko.util.dvds.Entry;
import com.xecko.util.dvds.Dvd;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class DvdTest {
	private Path tmp = null;
	private Dvd dvd = null;
	
	@BeforeClass
	public void createTempFileSystem() throws IOException {
		tmp = Files.createTempDirectory("__EntryTest__");
		Files.createDirectory(Paths.get(tmp + "/destination"));
		Files.createDirectory(Paths.get(tmp + "/source"));
		Files.createDirectory(Paths.get(tmp + "/source/emptydir"));
		Files.createDirectory(Paths.get(tmp + "/source/dir1"));
		RandomAccessFile fh = new RandomAccessFile(tmp + "/source/dir1/file1", "rw");
		fh.setLength(1024);
		fh.close();
		fh = new RandomAccessFile(tmp + "/source/file2", "rw");
		fh.setLength(1024 * 6);
		fh.close();
	}
	
	@BeforeTest
	public void createDvd() {
		dvd = new Dvd("DVD");
	}
	
	@Test
	public void maxSizeTest() {
		Assert.assertEquals(dvd.getMaxSize(), (long)(4.2 * 1024 * 1024 * 1024));
		dvd.setMaxSize(10);
		Assert.assertEquals(dvd.getMaxSize(), 10);
	}
	
	@Test
	public void addEntryTest() throws IOException {
		dvd.add(new Entry(tmp + "/source/dir1"));
		Assert.assertEquals(dvd.size(), 1024);
		dvd.add(new Entry(tmp + "/source/file2"));
		Assert.assertEquals(dvd.size(), 7 * 1024);
		Assert.assertEquals(dvd.getEntryCount(), 2);
	}
    
	@Test(expectedExceptions = IOException.class)
	public void overflowTest() throws IOException {
		dvd.setMaxSize(2 * 1024);
		dvd.add(new Entry(tmp + "/source/file2"));
	}
	
	@Test
	public void copyTest() throws IOException {
		dvd.add(new Entry(tmp + "/source/dir1"));
		dvd.add(new Entry(tmp + "/source/file2"));
		dvd.copy(new File(tmp + "/destination"));
    	Entry dirEntry = new Entry(tmp + "/destination/DVD/dir1");
    	Assert.assertEquals(dirEntry.size(), 1024);
    	Entry fileEntry = new Entry(tmp + "/destination/DVD/file2");
    	Assert.assertEquals(fileEntry.size(), 1024 * 6);
	}
    
    @AfterClass
    public void removeTempFileSystem() throws IOException {
    	FileUtils.deleteDirectory(tmp.toFile());
    }
}
