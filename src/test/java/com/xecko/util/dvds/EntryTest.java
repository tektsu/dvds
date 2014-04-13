package com.xecko.util.dvds;

import com.xecko.util.dvds.Entry;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class EntryTest {
	private Path tmp = null;
	
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

    @Test(expectedExceptions = IOException.class)
    public void testNonexistence() throws IOException {
    	new Entry(tmp + "/i.do.not.exist");
    }
    
    @Test
    public void dirEmptyTest() throws IOException {
    	Entry entry = new Entry(tmp + "/source/emptydir");
    	Assert.assertEquals(entry.size(), 0);
    }
    
    @Test
    public void dirTest() throws IOException {
    	Entry entry = new Entry(tmp + "/source/dir1");
    	Assert.assertEquals(entry.getPath(), tmp + "/source/dir1");
    	Assert.assertEquals(entry.getParent(), tmp + "/source");
    	Assert.assertEquals(entry.getName(), "dir1");
    	Assert.assertEquals(entry.size(), 1024);
    }
    
    @Test
    public void fileTest() throws IOException {
    	Entry entry = new Entry(tmp + "/source/file2");
    	Assert.assertEquals(entry.getPath(), tmp + "/source/file2");
    	Assert.assertEquals(entry.getParent(), tmp + "/source");
    	Assert.assertEquals(entry.getName(), "file2");
    	Assert.assertEquals(entry.size(), 1024 * 6);
    }
    
    @Test
    public void dirCopyTest() throws IOException {
    	Entry srcEntry = new Entry(tmp + "/source/dir1");
    	File destination = new File(tmp + "/destination");
    	Assert.assertEquals(destination.exists(), true);
    	srcEntry.copy(destination);
    	Entry destEntry = new Entry(tmp + "/destination/dir1");
    	Assert.assertEquals(destEntry.size(), 1024);
    }
    
    @Test
    public void fileCopyTest() throws IOException {
    	Entry srcEntry = new Entry(tmp + "/source/file2");
    	File destination = new File(tmp + "/destination");
    	Assert.assertEquals(destination.exists(), true);
    	srcEntry.copy(destination);
    	Entry destEntry = new Entry(tmp + "/destination/file2");
    	Assert.assertEquals(destEntry.size(), 1024 * 6);
    }
    
    @AfterClass
    public void removeTempFileSystem() throws IOException {
    	FileUtils.deleteDirectory(tmp.toFile());
    }
}
