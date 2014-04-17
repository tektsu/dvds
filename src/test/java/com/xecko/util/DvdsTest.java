package com.xecko.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import com.xecko.util.dvds.Entry;
import com.xecko.util.dvds.Source;
import com.xecko.util.dvds.Dvd;
import com.xecko.util.dvds.Helpers;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DvdsTest {
    private Path tmp = null;

    @BeforeClass
    public void createTempFileSystem() throws IOException {
        tmp = Helpers.setUpFullFileSystem();
    }

	@Test
	public void createContainer() throws FileNotFoundException {
		_createContainer();
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void createContainerWithNoDestination() throws FileNotFoundException {
    	new Dvds(tmp + "/destinatio");
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void createContainerWithFileDestination() throws FileNotFoundException {
    	new Dvds(tmp + "/source/file2");
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void createContainerWithNonemptyDestination() throws FileNotFoundException {
    	new Dvds(tmp + "/source");
	}
	
	@Test
	public void populateDvds() throws IOException {
		Dvds dvds = _populateDvds(Dvd.dvdSize);
        Assert.assertEquals(dvds.getDvdCount(), 1);
	}

	@Test
	public void populateDvds25K() throws IOException {
		Dvds dvds = _populateDvds(25 * 1024);
        Assert.assertEquals(dvds.getDvdCount(), 9);
	}

	@Test
	public void populateDvds30K() throws IOException {
		Dvds dvds = _populateDvds(30 * 1024);
        Assert.assertEquals(dvds.getDvdCount(), 8);
	}

    @AfterClass
    public void removeTempFileSystem() throws IOException {
        FileUtils.deleteDirectory(tmp.toFile());
    }
    
    private Dvds _createContainer() throws FileNotFoundException {
    	return new Dvds(tmp + "/destination", 101, "test_dvd");
    }
    
    private Dvds _populateDvds(long maxSize) throws FileNotFoundException, IOException {
		Dvds dvds = _createContainer();
		Source source = new Source(tmp + "/source");
		dvds.setMaxSize(maxSize);
        ArrayList<Entry> entries = source.getContents();
        for (Entry entry : entries) {
            dvds.add(entry);
        }
        //dvds.dump();
        return dvds;
    }
}
