package com.xecko.util.dvds;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EntryTest {
  private Path tmp = null;

  @BeforeClass
  public void setup() throws IOException {
    tmp = Helpers.setUpMinimalFileSystem();
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
  public void fileAsFileTest() throws IOException {
    Entry entry = new Entry(new File(tmp + "/source/file2"));
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
