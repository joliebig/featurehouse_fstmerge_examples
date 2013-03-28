

package edu.rice.cs.javalanglevels;
import edu.rice.cs.javalanglevels.parser.*;
import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.util.Log;
import junit.framework.TestCase;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.javalanglevels.ElementaryLevelTest.lf;


import java.util.*;
  
public class AdvancedLevelTest extends TestCase {
    
  public static final Log _log = new Log("LLConverter.txt", false);
  
  File dir1, dir2, dir3;
  FileFilter dj2Filter, dj2JavaFilter;
  
  public void setUp() { 
    dir1 = new File("testFiles/forAdvancedLevelTest"); 
    dir2 = new File("testFiles/forAdvancedLevelTest/importedFiles"); 
    dir3 = new File("testFiles/forAdvancedLevelTest/importedFiles2"); 
    
    dj2Filter = new FileFilter() {
      public boolean accept(File pathName) {
        String name = pathName.getAbsolutePath();
        return (name.endsWith(".dj2"));
      }
    };
    dj2JavaFilter = new FileFilter() {
      public boolean accept(File pathName) {
        String name = pathName.getAbsolutePath();
        return (name.endsWith(".dj2") || name.endsWith(".java"));
      }
    };
  }
  
  
  public void testSuccessful() {

    _log.log("Running testSuccessful");
    File[] files1 = dir1.listFiles(dj2Filter);
    File[] files2 = dir2.listFiles(dj2Filter);
    File[] files3 = dir3.listFiles(dj2Filter);

    LanguageLevelConverter llc = new LanguageLevelConverter();
    
    int len1 = files1.length;
    int len2 = files2.length;
    int len3 = files3.length;
    File[] testFiles = new File[len1 + len2 + len3];
    for (int i = 0; i < len1; i++) { testFiles[i] = files1[i]; }
    for (int i = 0; i < len2; i++) { testFiles[len1 + i] = files2[i]; }
    for (int i = 0; i < len3; i++) { testFiles[len1 + len2 + i] = files3[i]; }
        
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(testFiles, new Options(JavaVersion.JAVA_5, IterUtil.<File>empty()));
    

    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
    
    
    for(int i = 0; i < testFiles.length; i++) {
      File currFile = testFiles[i];
      String fileName = currFile.getAbsolutePath();
      fileName = fileName.substring(0, fileName.length() -4);
      File resultingFile = new File(fileName + ".java");
      File correctFile = new File(fileName + ".expected");
      
      if (correctFile.exists()) {
        try {
          assertEquals("File " + currFile.getName() + " should have been parsed and augmented correctly.",
                       lf(IOUtil.toString(correctFile)),
                       lf(IOUtil.toString(resultingFile)));
        }
        catch (IOException ioe) {
          fail(ioe.getMessage());
          
        }
      }
    }
    
    
    File dir2 = new File(dir1.getAbsolutePath() + System.getProperty("file.separator") + "importedFiles");
    testFiles = dir2.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        String name = pathName.getAbsolutePath();
        return name.endsWith("IsItPackageAndImport.dj1") || name.endsWith("ToReference.dj1");
      }});
      
      
      for(int i = 0; i < testFiles.length; i++) {
        File currFile = testFiles[i];
        String fileName = currFile.getAbsolutePath();
        fileName = fileName.substring(0, fileName.length() -4);
        File resultingFile = new File(fileName + ".java");
        File correctFile = new File(fileName + ".expected");
        
        if (correctFile.exists()) {
          try {
            assertEquals("File " + currFile.getName() + " should have been parsed and augmented correctly.",
                         lf(IOUtil.toString(correctFile)),
                         lf(IOUtil.toString(resultingFile)));
          }
          catch (IOException ioe) {
            fail(ioe.getMessage());
            
          }
        }
      }
      




      
      File f;
      dir2 = new File(dir1.getAbsolutePath() + System.getProperty("file.separator") + "importedFiles2");
      testFiles = dir2.listFiles(new FileFilter() {
        public boolean accept(File pathName) {
          return pathName.getAbsolutePath().endsWith("AlsoReferenced.dj1");
        }});
        
        
        for(int i = 0; i < testFiles.length; i++) {
          File currFile = testFiles[i];
          String fileName = currFile.getAbsolutePath();
          fileName = fileName.substring(0, fileName.length() -4);
          File resultingFile = new File(fileName + ".java");
          File correctFile = new File(fileName + ".expected");
          
          if (correctFile.exists()) {
            try {
              assertEquals("File " + currFile.getName() + " should have been parsed and augmented correctly.",
                           lf(IOUtil.toString(correctFile)),
                           lf(IOUtil.toString(resultingFile)));
            }
            catch (IOException ioe) {
              fail(ioe.getMessage());
              
            }
          }
        }
        




        
  }
  
  
  public void testNoNullPointer() { 
    _log.log("Running testNoNullPointer");
    dir1 = new File(dir1.getAbsolutePath() + "/shouldBreak");
    File[] testFiles = dir1.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        return pathName.getAbsolutePath().endsWith("SwitchDoesntAssign.dj2");
      }});
    _log.log("testFiles = " + testFiles);
    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    for (int i = 0; i<testFiles.length; i++) {
      result = llc.convert(new File[]{testFiles[i]}, new Options(JavaVersion.JAVA_5, IterUtil.<File>empty()));
      assertTrue("should be parse exceptions or visitor exceptions", !result.getFirst().isEmpty() || !result.getSecond().isEmpty());
      }
  }  
    
  
  public void testPackagedOrderMatters() {
    _log.log("Running testPackagedOrderMatters");
    dir2 = new File(dir1.getAbsolutePath() + System.getProperty("file.separator") + "lists-dj2" + 
                    System.getProperty("file.separator") + "src" + System.getProperty("file.separator") + "listFW");
    File[] testFiles = 
      new File[]{ new File(dir2, "NEList.dj2"), new File(dir2, "MTList.dj2"), new File(dir2, "IList.dj2")};
    


    LanguageLevelConverter llc = new LanguageLevelConverter();
    Pair<LinkedList<JExprParseException>, LinkedList<Pair<String, JExpressionIF>>> result;
    result = llc.convert(testFiles, new Options(JavaVersion.JAVA_5, IterUtil.<File>empty()));


    
    assertEquals("should be no parse exceptions", new LinkedList<JExprParseException>(), result.getFirst());
    
    assertEquals("should be no visitor exceptions", new LinkedList<Pair<String, JExpressionIF>>(), result.getSecond());
    
    
  }
}
