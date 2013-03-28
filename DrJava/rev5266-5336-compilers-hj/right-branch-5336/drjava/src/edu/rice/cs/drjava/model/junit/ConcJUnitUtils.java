

package edu.rice.cs.drjava.model.junit;

import java.io.*;
import java.util.jar.*;
import javax.swing.*;
import java.awt.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.util.swing.SwingWorker;
import edu.rice.cs.util.swing.ProcessingDialog;


public class ConcJUnitUtils {  
  
  
  protected static boolean isValidJarFile(final File f, String... checkFilesInJar) {
    if ((f==null) || (FileOps.NULL_FILE.equals(f)) || (!f.exists())) return false;
      JarFile jf = null;
      try {
        jf = new JarFile(f);
        for(String s: checkFilesInJar) {
          JarEntry je = jf.getJarEntry(s);
          if (je==null) return false;
        }
        return true;
        
        
        









      }
      catch(IOException ioe) { return false; }
      finally {
        try {
          if (jf != null) jf.close();
        }
        catch(IOException ioe) {  }
      }
  }
  
  
  public static boolean isValidJUnitFile(final File f) {
    return isValidJarFile(f,
                          "junit/framework/Test.class",
                          "junit/runner/Version.class");
  }

  
  public static boolean isValidConcJUnitFile(final File f) {
    return isValidJarFile(f,
                          "junit/framework/Test.class",
                          "junit/runner/Version.class",
                          "junit/runner/ConcutestVersion.class",
                          "edu/rice/cs/cunit/concJUnit/ConcJUnitFileInstrumentorLauncher.class",
                          "edu/rice/cs/cunit/concJUnit/MultithreadedTestError.class",
                          "edu/rice/cs/cunit/concJUnit/ThreadSets.class");
  }

  
  public static boolean isValidRTConcJUnitFile(final File f) {
    return isValidJarFile(f,
                          "java/lang/Object.class",
                          "java/lang/Thread.class",
                          "java/lang/String.class",
                          "edu/rice/cs/cunit/concJUnit/ThreadSets.class");
  }

  
  public static boolean isCompatibleRTConcJUnitFile(final File f) {
    if (!isValidRTConcJUnitFile(f)) return false;
    try {
      JarFile jf = new JarFile(f);
      Manifest mf = jf.getManifest();
      if (mf==null) return false;
      String vendor = mf.getMainAttributes().getValue("Edu-Rice-Cs-CUnit-JavaVersion-Vendor");
      String version = mf.getMainAttributes().getValue("Edu-Rice-Cs-CUnit-JavaVersion");
      if ((vendor==null) || (version==null)) return false;
      return (vendor.equals(edu.rice.cs.plt.reflect.JavaVersion.CURRENT_FULL.vendor().toString()) &&
              version.equals(edu.rice.cs.plt.reflect.JavaVersion.CURRENT_FULL.toString()));
    }
    catch(IOException ioe) { return false; }
  }
  
  
  public static boolean showIncompatibleWantToRegenerateDialog(final Frame parentFrame,
                                                               final Runnable yesRunnable,
                                                               final Runnable noRunnable) {
    Object[] options = {"Yes","No"};
    int n = JOptionPane.showOptionDialog(parentFrame,
                                         "The specified ConcJUnit runtime file is incompatible with the\n"+
                                         "current version of Java.  Do you wish to regenerate the file?",
                                         "Regenerate ConcJUnit Runtime",
                                         JOptionPane.YES_NO_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         options[1]);
    if (n==0) {
      
      File concJUnitJarFile = FileOps.getDrJavaFile();
      if (DrJava.getConfig().getSetting(OptionConstants.JUNIT_LOCATION_ENABLED)) {
        concJUnitJarFile = DrJava.getConfig().getSetting(OptionConstants.JUNIT_LOCATION);
      }
      File rtFile = DrJava.getConfig().getSetting(OptionConstants.RT_CONCJUNIT_LOCATION);
      showGenerateRTConcJUnitJarFileDialog(parentFrame,
                                           rtFile,
                                           concJUnitJarFile,
                                           new Runnable1<File>() {
        public void run(File targetFile) {
          
          DrJava.getConfig().setSetting(OptionConstants.RT_CONCJUNIT_LOCATION, targetFile);
          yesRunnable.run();
        }
      },
                                           new Runnable() {
                                             public void run() {
                                               
                                               if (DrJava.getConfig().getSetting(OptionConstants.CONCJUNIT_CHECKS_ENABLED).
                                                     equals(OptionConstants.ConcJUnitCheckChoices.NO_LUCKY)) {
                                                 DrJava.getConfig().setSetting(OptionConstants.CONCJUNIT_CHECKS_ENABLED,
                                                                               OptionConstants.ConcJUnitCheckChoices.NO_LUCKY);
                                               }
                                               noRunnable.run();
                                             } });
      return true;
    }
    else {
      
      if (DrJava.getConfig().getSetting(OptionConstants.CONCJUNIT_CHECKS_ENABLED).
            equals(OptionConstants.ConcJUnitCheckChoices.NO_LUCKY)) {
        DrJava.getConfig().setSetting(OptionConstants.CONCJUNIT_CHECKS_ENABLED,
                                      OptionConstants.ConcJUnitCheckChoices.NO_LUCKY);
      }
      noRunnable.run();
      return false;
    }
  }
  
  
  public static void showGenerateRTConcJUnitJarFileDialog(final Frame parentFrame,
                                                          File rtFile,
                                                          final File concJUnitJarFile,
                                                          final Runnable1<File> successRunnable,
                                                          final Runnable failureRunnable) {
    if ((rtFile == null) || (FileOps.NULL_FILE.equals(rtFile))) {
      
      File drJavaFile = FileOps.getDrJavaApplicationFile();
      File parent = drJavaFile.getParentFile();
      if (parent == null) {
        parent = new File(System.getProperty("user.dir"));
      }
      rtFile = new File(parent, "rt.concjunit.jar"); 
    }
    JFileChooser saveChooser = new JFileChooser() {
      public void setCurrentDirectory(File dir) {
        
        super.setCurrentDirectory(dir);
        setDialogTitle("Save:  " + getCurrentDirectory());
      }
    };
    saveChooser.setPreferredSize(new Dimension(650, 410));
    saveChooser.setSelectedFile(rtFile);
    saveChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
      public boolean accept(File f) {
        return f.isDirectory() || 
          f.getPath().endsWith(".jar");
      }
      public String getDescription() { 
        return "Java Archive Files (*.jar)";
      }
    });
    saveChooser.setMultiSelectionEnabled(false);
    int rc = saveChooser.showSaveDialog(parentFrame);
    if (rc == JFileChooser.APPROVE_OPTION) {
      final File targetFile = saveChooser.getSelectedFile();
      int n = JOptionPane.YES_OPTION;
      if (targetFile.exists()) {
        Object[] options = {"Yes","No"};
        n = JOptionPane.showOptionDialog(parentFrame,
                                         "This file already exists.  Do you wish to overwrite the file?",
                                         "Confirm Overwrite",
                                         JOptionPane.YES_NO_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         options[1]);
      }
      if (n == JOptionPane.YES_OPTION) {
        if (parentFrame!=null) parentFrame.setEnabled(false);
        final ProcessingDialog processingDialog =
          new ProcessingDialog(parentFrame, "Creating ConcJUnit Runtime", "Processing, please wait.");
        final JProgressBar pb = processingDialog.getProgressBar();
        processingDialog.setVisible(true);
        try {
          final File tmpDir = FileOps.createTempDirectory("DrJavaGenerateRTConcJUnitJar");
          
          SwingWorker worker = new SwingWorker() {
            volatile Boolean _success = null;
            Thread _processIncrementer = new Thread(new Runnable() {
              public void run() {
                File tmpFile = new File(tmpDir, "rt.concjunit.jar");
                boolean indeterminate = true;
                try {
                  while (_success == null) {
                    Thread.sleep(1000);
                    if (tmpFile.exists()) {
                      if (indeterminate) {
                        pb.setIndeterminate(false);
                        indeterminate = false;
                      }
                      pb.setValue((int)(100.0/(30*1024*1024)*tmpFile.length()));
                    }
                  }
                }
                catch(InterruptedException ie) {
                  pb.setIndeterminate(true);
                }
              }
            });
            public Object construct() {
              _processIncrementer.start();
              _success = edu.rice.cs.drjava.model.junit.ConcJUnitUtils.
                generateRTConcJUnitJarFile(targetFile, concJUnitJarFile, tmpDir);
              return null;
            }
            
            public void finished() {
              pb.setValue(100);
              processingDialog.setVisible(false);
              processingDialog.dispose();
              if (parentFrame!=null) parentFrame.setEnabled(true);
              if ((_success != null) && (_success)) {
                successRunnable.run(targetFile);
                JOptionPane.showMessageDialog(parentFrame,
                                              "Successfully generated ConcJUnit Runtime File:\n"+targetFile,
                                              "Generation Successful",
                                              JOptionPane.INFORMATION_MESSAGE);
                edu.rice.cs.plt.io.IOUtil.deleteRecursively(tmpDir);
              }
              else {
                failureRunnable.run();
                JOptionPane.showMessageDialog(parentFrame,
                                              "Could not generate ConcJUnit Runtime File:\n"+targetFile,
                                              "Could Not Generate",
                                              JOptionPane.ERROR_MESSAGE);
              }
            }
          };
          worker.start();
        }
        catch(IOException ioe) {
          JOptionPane.showMessageDialog(parentFrame,
                                        "Could not generate ConcJUnit Runtime file:\n"+targetFile,
                                        "Could Not Generate",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
    }
    else {
      failureRunnable.run();
    }
  }
  
  
  public static boolean generateRTConcJUnitJarFile(File rtFile, File concJUnitJarFile, File tmpDir) {
    if (!isValidConcJUnitFile(concJUnitJarFile)) return false;

    try {
      JVMBuilder jvmb = new JVMBuilder(tmpDir).classPath(concJUnitJarFile);
      Process p = jvmb.start("edu.rice.cs.cunit.concJUnit.ConcJUnitFileInstrumentorLauncher", "-r");
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while((line = br.readLine()) != null) {  }
      try {
        p.waitFor();
      }
      catch(InterruptedException ie) { return false; }
      if (p.exitValue() != 0) { return false; }
      edu.rice.cs.plt.io.IOUtil.copyFile(new File(tmpDir, "rt.concjunit.jar"), rtFile);
      return isValidRTConcJUnitFile(rtFile);
    }
    catch(IOException ioe) { return false; }
  }
}
