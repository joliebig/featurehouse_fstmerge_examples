

package edu.rice.cs.util;

import junit.framework.*;
import java.io.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.newjvm.ExecJVM;
import edu.rice.cs.util.FileOps;


public class FileOpsTest extends DrJavaTestCase {
  public static final String TEXT = "hi, dude.";
  public static final String PREFIX = "prefix";
  public static final String SUFFIX = ".suffix";

  public void testCreateTempDirectory() throws IOException {
    File dir = FileOps.createTempDirectory(PREFIX);
    try {
      assertTrue("createTempDirectory result is a directory", dir.isDirectory());
      assertTrue("temp directory has correct prefix",
                 dir.getName().startsWith(PREFIX));
    }
    finally { assertTrue("delete directory", dir.delete()); }
  }

  public void testReadAndWriteTempFile() throws IOException {
    File file = FileOps.writeStringToNewTempFile(PREFIX, SUFFIX, TEXT);
    try {
      assertTrue("temp file has correct prefix",
                 file.getName().startsWith(PREFIX));
      assertTrue("temp file has correct suffix",
                 file.getName().endsWith(SUFFIX));

      String read = FileOps.readFileAsString(file);
      assertEquals("contents after read", TEXT, read);
    }
    finally {
      assertTrue("delete file", file.delete());
    }
  }

  public void testRecursiveDirectoryDelete() throws IOException {
    final File baseDir = FileOps.createTempDirectory(PREFIX);

    File parentDir = baseDir;
    boolean ret;

    
    for (int i = 0; i < 5; i++) {
      File subdir = new File(parentDir, "subdir" + i);
      ret = subdir.mkdir();
      assertTrue("create directory " + subdir, ret);

      for (int j = 0; j < 2; j++) {
        File file = new File(parentDir, "file" + i + "-" + j);
        FileOps.writeStringToFile(file,
                                  "Some text for file "+file.getAbsolutePath());
        assertTrue(file + " exists", file.exists());
      }

      parentDir = subdir;
    }

    
    ret = FileOps.deleteDirectory(baseDir);
    assertTrue("delete directory result", ret);
    assertEquals("directory exists after deleting it", false, baseDir.exists());
  }


  
  public void testSaveFile() throws IOException {
    File writeTo = File.createTempFile("fileops", ".test").getCanonicalFile();
    writeTo.deleteOnExit();
    File backup = new File(writeTo.getPath() + "~");

    FileOps.saveFile(new FileOps.DefaultFileSaver(writeTo) {
      public void saveTo(OutputStream os) throws IOException {
        String output = "version 1";
        os.write(output.getBytes());
      }
      public boolean shouldBackup() {
        return false;
      }
    });
    assertEquals("save w/o backup", "version 1", FileOps.readFileAsString(writeTo));
    assertEquals("save w/o backup did not backup", false, backup.exists());

    FileOps.saveFile(new FileOps.DefaultFileSaver(writeTo) {
      public void saveTo(OutputStream os) throws IOException {
        String output = "version 2";
        os.write(output.getBytes());
      }
    });
    assertEquals("save2 w backup", "version 2", FileOps.readFileAsString(writeTo));
    assertEquals("save2 w backup did backup", "version 1",
                 FileOps.readFileAsString(backup));

    FileOps.saveFile(new FileOps.DefaultFileSaver(writeTo) {
      public void saveTo(OutputStream os) throws IOException {
        String output =  "version 3";
        os.write(output.getBytes());
      }
    });
    assertEquals("save3 w backup on", "version 3", FileOps.readFileAsString(writeTo));
    assertEquals("save3 w backup on did not backup", "version 1",
                 FileOps.readFileAsString(backup));


    
    try {
      FileOps.saveFile(new FileOps.DefaultFileSaver(writeTo) {
        public void saveTo(OutputStream os) throws IOException {
          String output = "version 4";
          os.write(output.getBytes());
          throw new IOException();
        }
      });
      fail("IOException not propagated");
    }
    catch (IOException ioe){ }
    assertEquals("failed save4 w/o backup", "version 3",
                 FileOps.readFileAsString(writeTo));
    assertEquals("failed save4 w/o backup check original backup", "version 1",
                 FileOps.readFileAsString(backup));

    
    try {
      FileOps.saveFile(new FileOps.DefaultFileSaver(writeTo) {
        public boolean shouldBackup () {
          return true;
        }
        public void saveTo(OutputStream os) throws IOException {
          String output =  "version 5";
          os.write(output.getBytes());
          throw new IOException();
        }
      });
      fail("IOException not propagated spot 2");
    }
    catch(IOException ioe){ } 
    assertEquals("failed save5 w backup", "version 3",
                 FileOps.readFileAsString(writeTo));

    
    
    try {
      FileOps.readFileAsString(backup);
      fail("The backup file should no longer exist.");
    }
    catch(FileNotFoundException e) { } 

    
    writeTo.setReadOnly();
    try {
      FileOps.saveFile(new FileOps.DefaultFileSaver(writeTo) {
        public boolean shouldBackup () {
          return true;
        }
        public void saveTo(OutputStream os) throws IOException {
          String output =  "version 6";
          os.write(output.getBytes());
        }
      });
      fail("The file to be saved was read-only!");
    }
    catch(IOException ioe){ } 
    assertEquals("failed save6 w backup", "version 3",
                 FileOps.readFileAsString(writeTo));

    
    
    try {
      FileOps.readFileAsString(backup);
      fail("The backup file should no longer exist.");
    }
    catch(FileNotFoundException e) { } 
  }

  
  public void testPackageExplore() throws IOException {
    File rootDir = FileOps.createTempDirectory("fileOpsTest");
    File subDir0 = new File(rootDir, "sub0");
    subDir0.mkdir();
    File subDir1 = new File(rootDir, "sub1");
    subDir1.mkdir();
    File subsubDir0 = new File(subDir0, "subsub0");
    subsubDir0.mkdir();
    File javasubsub = new File(subsubDir0, "aclass.java");
    FileOps.writeStringToFile(javasubsub, "contents of this file are unimportant");
    File javasub1 = new File(subDir1, "myclass.java");
    FileOps.writeStringToFile(javasub1, "this file is pretty much empty");
    File javaroot = new File(rootDir, "someclass.java");
    FileOps.writeStringToFile(javaroot, "i can write anything i want here");

    LinkedList packages = FileOps.packageExplore("hello", rootDir);
    assertEquals("package count a", 3, packages.size());
    assertTrue("packages contents a0", packages.contains("hello.sub0.subsub0"));
    assertTrue("packages contents a1", packages.contains("hello.sub1"));
    assertTrue("packages contents a2", packages.contains("hello"));

    
    
    packages = FileOps.packageExplore("", rootDir);
    assertEquals("package count b", 2, packages.size());
    assertTrue("packages contents b0", packages.contains("sub0.subsub0"));
    assertTrue("packages contents b1", packages.contains("sub1"));


    assertTrue("deleting temp directory", FileOps.deleteDirectory(rootDir));
  }


  
  public void testDeleteDirectoryOnExit() throws IOException, InterruptedException {
    
    
    
    
    
    File dir1 = FileOps.createTempDirectory("DrJavaTestTempDir");
    assertTrue("dir1 exists", dir1.exists());
    File file1 = File.createTempFile("DrJavaTest-", ".temp", dir1).getCanonicalFile();
    assertTrue("file1 exists", file1.exists());
    File dir2 = FileOps.createTempDirectory("TempDir", dir1).getCanonicalFile();
    assertTrue("dir2 exists", dir2.exists());
    File file2 = File.createTempFile("DrJavaTest-", ".temp", dir2).getCanonicalFile();
    assertTrue("file2 exists", file2.exists());

    String className = "edu.rice.cs.util.FileOpsTest";
    String[] args = new String[] { dir1.getAbsolutePath() };

    Process process = ExecJVM.runJVMPropagateClassPath(className, args, FileOption.NULL_FILE);
    int status = process.waitFor();
    assertEquals("Delete on exit test exited with an error!", 0, status);

    assertTrue("dir1 should be deleted", !dir1.exists());
    assertTrue("file1 should be deleted", !file1.exists());
    assertTrue("dir2 should be deleted", !dir2.exists());
    assertTrue("file2 should be deleted", !file2.exists());
  }

  public void testSplitFile() {
    String[] parts = new String[]{"","home","username","dir"};
    String path1 = "";
    for (String s : parts) {
      path1 += s + File.separator;
    }

    File f = new File(path1);
    String[] res = FileOps.splitFile(f);

    assertTrue( "Inconsitent results. Expected " +
               java.util.Arrays.asList(parts).toString() + ", but found " +
               java.util.Arrays.asList(res).toString(),
               java.util.Arrays.equals(parts,res));
  }

  private String fixPathFormat(String s){
    return s.replace('\\', '/');
  }

  public void testMakeRelativeTo() throws IOException, SecurityException {
    File base, abs;

    base = new File("src/test1/test2/file.txt");
    abs = new File("built/test1/test2/file.txt");
    assertEquals("Wrong Relative Path 1", "../../../built/test1/test2/file.txt",
                 fixPathFormat(FileOps.makeRelativeTo(abs,base).getPath()));
    base = new File("file.txt");
    abs = new File("built/test1/test2/file.txt");
    assertEquals("Wrong Relative Path 2", "built/test1/test2/file.txt",
                 fixPathFormat(FileOps.makeRelativeTo(abs,base).getPath()));
    base = new File("built/test1/test2test/file.txt");
    abs = new File("built/test1/test2/file.txt");
    assertEquals("Wrong Relative Path 3", "../test2/file.txt",
                 fixPathFormat(FileOps.makeRelativeTo(abs,base).getPath()));
    base = new File("file.txt");
    abs = new File("test.txt");
    assertEquals("Wrong Relative Path 4", "test.txt",
                 fixPathFormat(FileOps.makeRelativeTo(abs,base).getPath()));
  }

  
  public static void main(String[] args) {
    if (args.length != 1) System.exit(1);

    File dir = new File(args[0]);
   
    if (! dir.exists()) System.exit(2);

    FileOps.deleteDirectoryOnExit(dir);

    
    System.exit(0);
  }
  
  public void testConvertToAbsolutePathEntries() {
    String ud = System.getProperty("user.dir");
    String f = System.getProperty("file.separator");
    String p = System.getProperty("path.separator");
    String expected, actual, input;
    
    input = "."+p+"drjava"+p+p+f+"home"+f+"foo"+f+"junit.jar";
    expected = ud+f+"."+p+ud+f+"drjava"+p+ud+p+(new File(f+"home"+f+"foo"+f+"junit.jar")).getAbsolutePath();
    actual = FileOps.convertToAbsolutePathEntries(input);
    assertEquals("testConvertToAbsolutePathEntries for several paths failed, input = '" + input + "', expected = '" + 
                 expected + "', actual = '" + actual + "'", expected, actual);
    input = "";
    expected = ud;
    actual = FileOps.convertToAbsolutePathEntries(input);
    assertEquals("testConvertToAbsolutePathEntries for empty path failed, input = '" + input + "', expected = '" + 
                 expected + "', actual = '" + actual + "'", expected, actual); 
    input = p + p + p + ".";
    expected = ud + p + ud + p + ud + p + ud + f + ".";
    actual = FileOps.convertToAbsolutePathEntries(input);
    assertEquals("testConvertToAbsolutePathEntries for several empty paths failed, input = '" + input + 
                 "', expected = '" +expected+"', actual = '" + actual + "'", expected, actual);
    input = p + p;
    expected = ud + p + ud + p + ud;
    actual = FileOps.convertToAbsolutePathEntries(input);
    assertEquals("testConvertToAbsolutePathEntries for trailing empty paths failed, input = '" + input + 
                 "', expected = '" + expected + "', actual = '" + actual + "'", expected, actual);
  }
  
  
  public void testGetFiles() throws IOException {
    File dir1 = FileOps.createTempDirectory("DrJavaTestTempDir");
    assertTrue("dir1 exists", dir1.exists());
    File file1a = File.createTempFile("DrJavaTest-", ".temp", dir1).getCanonicalFile();
    assertTrue("file1a exists", file1a.exists());
    File file1b = File.createTempFile("DrJava-", ".temp", dir1).getCanonicalFile();
    assertTrue("file1b exists", file1b.exists());
    File dir2 = FileOps.createTempDirectory("DrJavaTestDir-", dir1).getCanonicalFile();
    assertTrue("dir2 exists", dir2.exists());
    File file2 = File.createTempFile("DrJavaTest-", ".temp", dir2).getCanonicalFile();
    assertTrue("file2 exists", file2.exists());
    
    FileFilter ff = new FileFilter() {
        public boolean accept(File f) {
          if (f.isDirectory()) return true;
          String name = f.getName();
          return name.startsWith("DrJavaTest");
        }
    };
    
    List<File> res1 = Arrays.asList(new File[] {file1a});
    List<File> res2 = Arrays.asList(new File[] {file1a, file2});
    
    assertEquals("non-recursive FilesInDir test", res1, FileOps.getFilesInDir(dir1, false, ff));
    assertEquals("recursive FileInDir test", res2, FileOps.getFilesInDir(dir1, true, ff));
  }
}
