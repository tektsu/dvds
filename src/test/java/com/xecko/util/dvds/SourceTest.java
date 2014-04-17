package com.xecko.util.dvds;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SourceTest {
    private Path tmp = null;

    @BeforeClass
    public void createTempFileSystem() throws IOException {
        tmp = Helpers.setUpMinimalFileSystem();
    }

	@Test
	public void setSource() throws FileNotFoundException {
		new Source(tmp + "/source");
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void setNonexistentSource() throws FileNotFoundException {
		new Source(tmp + "/sourc");
	}

	@Test(expectedExceptions = FileNotFoundException.class)
	public void setFileSource() throws FileNotFoundException {
		new Source(tmp + "/source/dir1/file1");
	}
	
    @AfterClass
    public void removeTempFileSystem() throws IOException {
        FileUtils.deleteDirectory(tmp.toFile());
    }
}
