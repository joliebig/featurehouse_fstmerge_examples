

package edu.rice.cs.drjava.ui;

import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import edu.rice.cs.util.UnexpectedException;


public class JavadocFrame extends HTMLFrame {

  private static final int MAX_READ_PACKAGES_LINES = 100;
  private static final int MAX_READ_FOR_LINK_LINES = 100;
  private static final String[] INTRO_PAGE= {
    "overview-summary.html",
    "packages.html"
  };
  private static final String INDEX_PAGE= "allclasses-frame.html";

  private static String introPagePath(File destDir, String curClass) {
    
    File test = new File(destDir, curClass + ".html");
    for (int i = 0; !test.exists() && (i < INTRO_PAGE.length); i++) {
      test = new File(destDir, INTRO_PAGE[i]);
    }

    
    if (test.exists()) {
      if (test.getName().equals("packages.html")) {
      test = _parsePackagesFile(test, destDir);
      }
    }
    else {
      throw new IllegalStateException("No Javadoc HTML output files found!");
    }
    return test.getAbsolutePath();
  }

  
  private static File _parsePackagesFile(File packages, File destDir) {
    try {
      FileReader fr = new FileReader(packages);
      BufferedReader br = new BufferedReader(fr);
      try { 
        String line = br.readLine();
        int numLinesRead = 1;
        boolean found = false;
        while ((!found) &&
               (numLinesRead < MAX_READ_PACKAGES_LINES) &&
               (line != null)) {
          found = (line.indexOf("The front page has been relocated") != -1);
          if (!found) {
            line = br.readLine();
            numLinesRead++;
          }
        }
        
        
        if (found) {
          boolean foundLink = false;
          while ((!foundLink) &&
                 (numLinesRead < MAX_READ_FOR_LINK_LINES) &&
                 (line != null)) {
            foundLink = (line.indexOf("Non-frame version") != -1);
            if (!foundLink) {
              line = br.readLine();
              numLinesRead++;
            }
          }
          
          if (foundLink) {
            String start = "HREF=\"";
            int startIndex = line.indexOf(start) + start.length();
            int endIndex = line.indexOf("\">");
            if ((startIndex != -1) && (endIndex != -1)) {
              String fileName = line.substring(startIndex, endIndex);
              return new File(destDir, fileName);
            }
          }
        }
      }
      finally { br.close(); }
    }
    catch (IOException ioe) { throw new UnexpectedException(ioe); }
    return packages;
  }

  
  public JavadocFrame(File destDir, String curClass, boolean allDocs)
    throws MalformedURLException
  {
    
    super("Javadoc Viewer",
          new URL("file", "", introPagePath(destDir, curClass)),
          new URL("file", "", (new File(destDir, INDEX_PAGE)).getAbsolutePath()),
           "DrJavadoc.png", destDir);

    addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          URL url = event.getURL();
          jumpTo(url);
        }
      }
    });

    if (!allDocs) {
      _hideNavigationPane();
    }
  }
}
