package com.xecko.util;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.xecko.util.dvds.Entry;
import com.xecko.util.dvds.Helpers;

public class DvdsCopyTest {
  private Path tmp = null;

  @BeforeClass
  public void createTempFileSystem() throws IOException {
    tmp = Helpers.setUpFullFileSystem();
  }

  @Test
  public void copy() throws IOException {
    Dvds dvds = Helpers._populateDvds(tmp, 40 * 1024);
    Assert.assertEquals(dvds.getDvdCount(), 6);
    dvds.copy();
    // Spot check to ensure resulting files are in the expected position
    new Entry(tmp + "/destination/test_dvd0101/dir20/file");
    new Entry(tmp + "/destination/test_dvd0102/dir18/file");
    new Entry(tmp + "/destination/test_dvd0106/dir6/file");
  }

  @AfterClass
  public void removeTempFileSystem() throws IOException {
    FileUtils.deleteDirectory(tmp.toFile());
  }
}
