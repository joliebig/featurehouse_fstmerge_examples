

package edu.rice.cs.util.newjvm;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.util.StringOps;

import java.io.File;
import java.io.IOException;


public class ExecJVMTest extends DrJavaTestCase {
  
  public void setUp() throws Exception {
    super.setUp();
    edu.rice.cs.util.swing.Utilities.TextAreaMessageDialog.TEST_MODE = true;
  }

  public void testExecFileCreator() throws IOException, InterruptedException {
    File tempFile = File.createTempFile("drjava-test", ".tmp").getCanonicalFile();
    assertTrue("temp file exists", tempFile.exists());
    boolean ret = tempFile.delete();
    assertTrue("temp file delete succeeded", ret);

    
    String className = getClass().getName() + "$FileCreator";
    String tempName = tempFile.getAbsolutePath();
    Process jvm = ExecJVM.runJVMPropagateClassPath(className, new String[] { tempName }, FileOption.NULL_FILE);

    int result = jvm.waitFor();

    
    try {
      assertEquals("jvm exit code", 0, result);
      assertTrue("jvm did not create file", tempFile.exists());
      assertTrue("jvm System.out not empty", jvm.getInputStream().read() == -1);
      assertTrue("jvm System.err not empty", jvm.getErrorStream().read() == -1);
    }
    finally {  }

    
    ret = tempFile.delete();
    assertTrue("temp file delete succeeded", ret);
  }

  public static final class FileCreator {
    public static void main(String[] args) {
      File file = new File(args[0]);
      boolean ret;
      try { ret = file.createNewFile(); }
      catch (IOException ioe) { ret = false; }
      if (!ret) throw new RuntimeException("file creation failed");
      System.exit(0);
    }
  }

  public void testExecWorkingDirNotFound() throws IOException, InterruptedException {
    
    File tempFile = File.createTempFile("drjava-test", ".tmp").getCanonicalFile();
    assertTrue("temp file exists", tempFile.exists());
    boolean ret = tempFile.delete();
    assertTrue("temp file delete succeeded", ret);

    
    File tempDir = new File(tempFile.toString() + File.separatorChar);
    ret = tempDir.mkdirs();
    assertTrue("temp dir exists", tempDir.exists());
    assertTrue("temp dir is dir", tempDir.isDirectory());
    ret = tempDir.delete();
    assertTrue("temp dir delete succeeded", ret);

    
    String className = getClass().getName() + "$" + StringOps.getSimpleName(NoOp.class);
    String tempName = tempFile.getAbsolutePath();
    Process jvm = ExecJVM.runJVMPropagateClassPath(className, new String[] { tempName }, tempDir);

    int result = jvm.waitFor();

    
    try {
      assertEquals("jvm exit code", 0, result);
      assertTrue("jvm System.out not empty", jvm.getInputStream().read() == -1);
      assertTrue("jvm System.err not empty", jvm.getErrorStream().read() == -1);
    }
    finally {
    }
  }

  public static final class NoOp {
    public static void main(String[] args) {
    }
  }
}
