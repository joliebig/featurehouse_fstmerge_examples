

package edu.rice.cs.javalanglevels;

import java.io.*;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.*;

import junit.framework.TestCase;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.javalanglevels.ElementaryLevelTest.lf;


public class JExprParserTest extends TestCase {
  
  
  public void testParseSucceeds() throws IOException, ParseException {
    File directory = new File("testFiles");

    File[] testFiles = directory.listFiles(new FileFilter() {
      public boolean accept(File pathName) {
        return pathName.getAbsolutePath().endsWith(".test");
      }
    });

    for(int i = 0; i < testFiles.length; i++) {
      File currFile = testFiles[i];
      SourceFile sf = null;
      try {
        sf = new JExprParser(currFile).SourceFile();
      }
      catch (ParseException pe) {
        throw pe;
      }

      String path2 = currFile.getAbsolutePath();
      int indexOfLastDot2 = path2.lastIndexOf('.');
      String newPath2 = path2.substring(0, indexOfLastDot2) + ".actual";
      FileWriter fw = new FileWriter(newPath2);
      fw.write(sf.toString());
      fw.close();
      
      
      String path = currFile.getAbsolutePath();
      int indexOfLastDot = path.lastIndexOf('.');
      String newPath = path.substring(0, indexOfLastDot) + ".expected";
      File f = new File(newPath);
      String text = IOUtil.toString(f);
      assertEquals("The resulting SourceFile generated from " + currFile + " is not correct.",
                   lf(text),
                   lf(sf.toString()));
    }
  }
}
