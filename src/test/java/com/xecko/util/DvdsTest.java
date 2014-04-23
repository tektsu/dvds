package com.xecko.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.xecko.util.dvds.Dvd;
import com.xecko.util.dvds.Helpers;

public class DvdsTest {
  private Path tmp = null;

  @BeforeClass
  public void createTempFileSystem() throws IOException {
    tmp = Helpers.setUpFullFileSystem();
  }

  @Test
  public void createContainer() throws FileNotFoundException {
    Helpers._createContainer(tmp);
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
  public void createContainerWithNonemptyDestination()
      throws FileNotFoundException {
    new Dvds(tmp + "/source");
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
}
