package report.narrative;

import genj.fo.Document;
import genj.fo.Format;
import genj.gedcom.*;
import genj.io.GedcomReader;
import genj.io.GedcomReaderFactory;
import genj.io.GedcomWriter;
import junit.framework.TestCase;
import narrative.ReportNarrative;

import java.io.*;
import java.util.Locale;


public class ReportNarrativeTest extends TestCase {
    Gedcom gedcom;

    public void setUp() throws GedcomException, IOException {
        GedcomReader reader = GedcomReaderFactory.createReader(new FileInputStream("src/tst/report/narrative/narrativetest.ged"), null);
        gedcom = reader.read();














        
        validate(gedcom);

    }


    
    private static void validate(Gedcom gedcom) throws IOException
    {
        OutputStream sink = new OutputStream() {
            public void write(int arg0) {  }
        };

        GedcomWriter writer = new GedcomWriter(gedcom,sink);
        writer.write(); 
    }

  public void testDescendants(Locale locale) throws IOException {
    Locale.setDefault(locale);
    String lang = locale.getLanguage();
    ReportNarrative report = new ReportNarrative();
    report.ancestors = false;
    String indi = "I1"; 

    testDocument(lang, report, indi, "descendants");
  }

  
  public void testAncestors(Locale locale) throws IOException {
    Locale.setDefault(locale);
    String lang = locale.getLanguage();
    ReportNarrative report = new ReportNarrative();
    report.ancestors = true;
    String indi = "I3"; 

    testDocument(lang, report, indi, "ancestors");
  }

  private void testDocument(String lang, ReportNarrative report, String indi, String type) throws IOException {
    Document doc =
        (Document) report.startTest(gedcom, indi);
    Format formatter = Format.DEFAULT;
    File file = new File("/tmp/narrativetest." + type + "."  + lang + ".html"); 
    formatter.format(doc, file);
    
    File expected = new File("src/tst/report/narrative/narrativetest."  + lang + ".html");
    if (!type.equals("ancestors")) {
        expected = new File("src/tst/report/narrative/narrativetest." + type + "." + lang + ".html");
    }
    assertFilesEqual(expected, file,
        new String[] {
            "generated on .* with",
            "wurde am .* mit"});
  }

  private void assertFilesEqual(File expected, File actual, String[] deletePatterns) throws IOException {
        BufferedReader r1 = new BufferedReader(new FileReader(expected));
        BufferedReader r2 = new BufferedReader(new FileReader(actual));
        int lineNumber = 0;

        while (true) {
            ++lineNumber;
            String l1 = r1.readLine();
            String l2 = r2.readLine();
          if (l1 == null && l2 == null) {
                return; 
            }
            if (l1 == null) {
                fail("EOF on expected at line "+ lineNumber);
            }
            if (l2 == null) {
                fail("EOF on actual at line "+ lineNumber);
            }
          for (int i = 0; i < deletePatterns.length; i++) {
            l1 = l1.replaceAll(deletePatterns[i], "X");
            l2 = l2.replaceAll(deletePatterns[i], "X");
          }
            if (!l1.equals(l2)) {
                String separator = ">";
                if (separator == null) {
                    assertEquals("Files differ at line " + lineNumber, l1, l2);
                } else {
                    System.err.println("Files differ at line " + lineNumber);
                    String[] frags1 = l1.split(separator);
                    String[] frags2 = l2.split(separator);
                    int f = 0;
                    while (f < frags1.length) {
                        if (!frags1[f].equals(frags2[f])) {
                          System.err.println("Expected: " + frags1[f]);
                            System.err.println("Actual:   " + frags2[f]);
                            fail("Files differ in fragment " + f + " at line " + lineNumber);
                        }
                        ++f;
                    }
                    assertEquals("Files differ at line " + lineNumber, l1, l2);
                }
                return;
            }


        }
    }
}
