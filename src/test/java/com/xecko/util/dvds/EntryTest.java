package com.xecko.util.dvds;

import com.xecko.util.dvds.Entry;

import java.io.File;
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
		Files.createDirectory(Paths.get(tmp + "/source/dir1"));
	}

    @Test(expectedExceptions = IOException.class)
    public void testNonexistence() throws IOException {
    	new Entry(new File(tmp + "/i.do.not.exist"));
    }
    
    @Test
    public void dirEntryTest() throws IOException {
    	Entry entry = new Entry(new File(tmp + "/source/dir1"));
    	Assert.assertEquals(0, entry.getSize());
    }
    
    @AfterClass
    public void removeTempFileSystem() throws IOException {
    	FileUtils.deleteDirectory(tmp.toFile());
    }
}
