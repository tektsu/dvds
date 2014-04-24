package com.xecko.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.xecko.util.dvds.Dvd;
import com.xecko.util.dvds.Entry;
import com.xecko.util.dvds.Helpers;
import com.xecko.util.dvds.Source;

public class DvdsTest {
  private Path tmp = null;

  @BeforeClass
  public void createTempFileSystem() throws IOException {
    tmp = Helpers.setUpFullFileSystem();
  }

  @Test
  public void createContainer() throws FileNotFoundException {
    Helpers._createContainer();
  }

  @Test
  public void populateDvds() throws IOException {
    Dvds dvds = Helpers._populateDvds(tmp, Dvd.dvdSize);
    Assert.assertEquals(dvds.getDvdCount(), 1);
  }

  @Test
  public void populateDvds25K() throws IOException {
    Dvds dvds = Helpers._populateDvds(tmp, 25 * 1024);
    Assert.assertEquals(dvds.getDvdCount(), 9);
  }

  @Test
  public void populateDvds30K() throws IOException {
    Dvds dvds = Helpers._populateDvds(tmp, 30 * 1024);
    Assert.assertEquals(dvds.getDvdCount(), 8);
  }
  
  @Test(expectedExceptions = IOException.class)
  public void entryTooLargeForCollection() throws IOException {
    Dvds dvds = Helpers._createContainer();
    Source source = new Source(tmp + "/source");
    dvds.setMaxSize(19 * 1024);
    ArrayList<Entry> entries = source.getContents();
    dvds.add(entries.get(0));
  }
}
